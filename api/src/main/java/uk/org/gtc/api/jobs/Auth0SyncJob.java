package uk.org.gtc.api.jobs;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import uk.org.gtc.api.ApplicationMode;
import uk.org.gtc.api.Auth0Mgmt;
import uk.org.gtc.api.EmailService;
import uk.org.gtc.api.EmailServiceFactory;
import uk.org.gtc.api.GtcConfiguration;
import uk.org.gtc.api.MemberServiceFactory;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.MemberService;

@Every("1min")
@DelayStart("30s")
public class Auth0SyncJob extends Job
{
    
    /**
     * Sync metadata (first/last names, membership numbers) with Auth0.
     */
    @Override
    public void doJob(final JobExecutionContext context) throws JobExecutionException
    {
        final GtcConfiguration configuration = GtcConfiguration.getInstance();
        final MemberService memberService = MemberServiceFactory.getInstance();
        final EmailService emailService = EmailServiceFactory.getInstance();
        Auth0Mgmt.getInstance();
        final String emailField = "email.raw";
        final String membershipNumberKey = "membershipNumber";
        
        Integer updateCount = 0;
        final List<MemberDO> members = memberService.getAll();
        for (final MemberDO member : members)
        {
            List<User> usersByEmail = new ArrayList<>();
            try
            {
                usersByEmail = getAuth0UsersByField(emailField, member.getEmail());
            }
            catch (UnsupportedEncodingException | Auth0Exception e)
            {
                logger().error("Error getting Auth0 users by email address", e);
            }
            
            // No match found for any information we hold.
            if (usersByEmail.isEmpty())
            {
                // No-op currently
                // Skip to next member
                break;
            }
            else if (usersByEmail.size() > 1)
            {
                logger().error("Multiple users found with the email {} on Auth0", member.getEmail());
                // Skip updating these accounts
                break;
            }
            
            // Update membership numbers & roles when matched by email
            for (final User user : usersByEmail)
            {
                Boolean updated = false;
                final User newUser = new User(configuration.auth0UserConnection);
                final String rolesKey = "roles";
                final Map<String, Object> appMetadata = user.getAppMetadata();
                final Map<String, Object> newAppMetadata = new HashMap<>();
                
                Long membershipNumberAppMetadata = null;
                if (appMetadata.containsKey(membershipNumberKey))
                {
                    final Integer mdKey = (Integer) appMetadata.get(membershipNumberKey);
                    membershipNumberAppMetadata = mdKey.longValue();
                }
                
                // Update membership number
                if (membershipNumberAppMetadata == null || !membershipNumberAppMetadata.equals(member.getMembershipNumber()))
                {
                    logger().debug("{} has {} in app metadata, {} on system", user.getEmail(), membershipNumberAppMetadata,
                            member.getMembershipNumber());
                    logger().debug("User {} doesn't have membership number", user.getEmail());
                    newAppMetadata.put(membershipNumberKey, member.getMembershipNumber());
                    updated = true;
                }
                
                // Update relevant roles for the member
                @SuppressWarnings("unchecked")
                final List<String> roles = (List<String>) appMetadata.getOrDefault(rolesKey, new ArrayList<>());
                logger().debug("Found {} as roles", roles);
                if (!roles.contains(ApplicationRole.MEMBER.toString()))
                {
                    logger().debug("User {} doesn't have MEMBER role", user.getEmail());
                    roles.add(ApplicationRole.MEMBER.toString());
                    newAppMetadata.put(rolesKey, roles);
                    updated = true;
                }
                
                // Update user object with roles and/or membership number
                if (updated)
                {
                    appMetadata.putAll(newAppMetadata);
                    newUser.setAppMetadata(appMetadata);
                    // Update Auth0 with new user object
                    logger().debug("Updating user {} with {}", user.getEmail(), newAppMetadata);
                    updateCount++;
                    try
                    {
                        if (configuration.appMode == ApplicationMode.LIVE)
                        {
                            Auth0Mgmt.mgmt.users().update(user.getId(), newUser).execute();
                        }
                        else
                        {
                            logger().debug("Would update user {} with {}", user.getId(), newUser);
                        }
                    }
                    catch (final Auth0Exception a0e)
                    {
                        logger().error("Could not update user " + user.getId(), a0e);
                        break;
                    }
                    emailService.sendAccountLinkedNotification(member);
                }
            }
        }
        
        List<User> users = new ArrayList<>();
        try
        {
            users = getAllAuth0Users();
        }
        catch (final Auth0Exception e)
        {
            logger().error("Error getting all Auth0 users", e);
        }
        
        for (final User user : users)
        {
            Integer membershipNumberAppMetadata = null;
            final Map<String, Object> appMetadata = user.getAppMetadata();
            if (appMetadata.containsKey(membershipNumberKey))
            {
                membershipNumberAppMetadata = (Integer) appMetadata.get(membershipNumberKey);
            }
            List<MemberDO> memberResults = new ArrayList<>();
            if (membershipNumberAppMetadata != null)
            {
                memberResults = memberService.findByMemberNumber(membershipNumberAppMetadata.longValue());
            }
            if (memberResults.isEmpty())
            {
                // Update user account to remove membership number
                Boolean updated = false;
                final User newUser = new User(configuration.auth0UserConnection);
                final String rolesKey = "roles";
                final Map<String, Object> newAppMetadata = new HashMap<>();
                
                // Remove membership number
                if (membershipNumberAppMetadata != null)
                {
                    logger().debug("User {} has membership number", user.getEmail());
                    newAppMetadata.put(membershipNumberKey, null);
                    updated = true;
                }
                
                // Update relevant roles for the member
                @SuppressWarnings("unchecked")
                final List<String> roles = (List<String>) appMetadata.getOrDefault(rolesKey, new ArrayList<>());
                logger().debug("Found {} as roles", roles);
                if (roles.contains(ApplicationRole.MEMBER.toString()))
                {
                    logger().debug("User {} has MEMBER role", user.getEmail());
                    roles.remove(ApplicationRole.MEMBER.toString());
                    newAppMetadata.put(rolesKey, roles);
                    updated = true;
                }
                
                // Update user object with roles and/or membership number
                if (updated)
                {
                    newUser.setAppMetadata(newAppMetadata);
                    // Update Auth0 with new user object
                    logger().debug("Updating user {} with {}", user.getEmail(), newAppMetadata);
                    updateCount++;
                    try
                    {
                        if (configuration.appMode == ApplicationMode.LIVE)
                        {
                            Auth0Mgmt.mgmt.users().update(user.getId(), newUser).execute();
                        }
                        else
                        {
                            logger().debug("Would update user {} with {}", user.getId(), newUser);
                        }
                    }
                    catch (final Auth0Exception e)
                    {
                        logger().error("Could not update user", e);
                    }
                }
            }
        }
    }
    
    private List<User> getAllAuth0Users() throws Auth0Exception
    {
        List<User> users = new ArrayList<>();
        users = getNextUserPage(users, 0);
        return users;
    }
    
    private List<User> getNextUserPage(final List<User> users, final Integer pageNumber) throws Auth0Exception
    {
        final UserFilter filter = new UserFilter();
        filter.withSort("email:1");
        filter.withTotals(true);
        filter.withPage(pageNumber, 50);
        final UsersPage page = Auth0Mgmt.mgmt.users().list(filter).execute();
        users.addAll(page.getItems());
        if (page.getTotal() > users.size())
        {
            getNextUserPage(users, pageNumber + 1);
        }
        return users;
    }
    
    private List<User> getAuth0UsersByField(final String field, final String value) throws UnsupportedEncodingException, Auth0Exception
    {
        // Only search for users with a verified email, otherwise
        // they could be handed control of another member's details
        // by spoofing the email
        final String query = new StringBuilder().append("email_verified : true AND ")
                .append(field + " ")
                .append(": ")
                .append(value + " ")
                .toString()
                .trim();
        final UserFilter filter = new UserFilter();
        filter.withQuery(query);
        Auth0Mgmt.getInstance();
        final List<User> users = Auth0Mgmt.mgmt.users().list(filter).execute().getItems();
        logger().debug("Querying with {} matched {} users", query, users.size());
        return users;
    }
    
    Logger logger()
    {
        return LoggerFactory.getLogger(Auth0SyncJob.class);
    }
}
