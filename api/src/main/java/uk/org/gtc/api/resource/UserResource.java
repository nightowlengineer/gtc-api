package uk.org.gtc.api.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.GtcConfiguration;
import uk.org.gtc.api.SendGridHelper;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.UserAppMetadata;
import uk.org.gtc.api.service.MemberService;

@SuppressWarnings("rawtypes")
@Path("user")
@Api("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends GenericResource
{
    private final GtcConfiguration configuration;
    private final MemberService memberService;
    private final SendGridHelper emailService;
    
    private String accessToken;
    private Date accessTokenExpiresAt;
    
    public UserResource(final GtcConfiguration configuration, final MemberService memberService, final SendGridHelper emailService)
            throws Auth0Exception
    {
        this.configuration = configuration;
        this.accessToken = getAccessToken();
        this.memberService = memberService;
        this.emailService = emailService;
    }
    
    /**
     * Gets the current access token, or requests a new token if the current one
     * has expired.
     * Note that management API tokens cannot be refreshed/renewed: <a href=
     * "https://auth0.com/docs/api/management/v2/tokens#frequently-asked-questions">'Can
     * I refresh my token?'</a>
     *
     * @return a valid management token
     * @throws Auth0Exception
     */
    private String getAccessToken() throws Auth0Exception
    {
        // No access token or the expiry time has been reached
        if (this.accessToken == null || this.accessTokenExpiresAt == null
                || Calendar.getInstance().getTime().after(this.accessTokenExpiresAt))
        {
            logger().info("Getting new management token");
            final String audience = configuration.auth0Domain + "/api/v2/";
            final AuthAPI auth = new AuthAPI(configuration.auth0Domain, configuration.auth0MgmtApiId, configuration.auth0MgmtApiKey);
            
            final TokenHolder tokens = auth.requestToken(audience).execute();
            
            // Store the expiry time (defaults to 86400 seconds/24 hours)
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, Math.toIntExact(tokens.getExpiresIn()));
            this.accessTokenExpiresAt = cal.getTime();
            
            this.accessToken = tokens.getAccessToken();
            return this.accessToken;
        }
        logger().debug("Using existing access token");
        return this.accessToken;
    }
    
    @GET
    @Timed
    @Path("auth0/getActiveUserCount")
    @ApiOperation("Get count of Auth0 users active within the last 30 days")
    @RolesAllowed("MEMBERSHIP_MANAGE")
    public Integer getActiveAuth0Users() throws Auth0Exception
    {
        final ManagementAPI mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
        
        return mgmt.stats().getActiveUsersCount().execute();
    }
    
    private List<User> getAllAuth0Users() throws Auth0Exception
    {
        List<User> users = new ArrayList<>();
        users = getNextUserPage(users, 0);
        return users;
    }
    
    @GET
    @Timed
    @Path("auth0/users")
    @ApiOperation("Get a list of all Auth0 users")
    @RolesAllowed("ADMIN")
    public List<User> getAuth0Users() throws Auth0Exception
    {
        return getAllAuth0Users();
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
        
        final ManagementAPI mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
        final List<User> users = mgmt.users().list(filter).execute().getItems();
        logger().debug("Querying with {} matched {} users", query, users.size());
        return users;
    }
    
    private List<User> getNextUserPage(final List<User> users, final Integer pageNumber) throws Auth0Exception
    {
        final ManagementAPI mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
        final UserFilter filter = new UserFilter();
        filter.withSort("email:1");
        filter.withTotals(true);
        filter.withPage(pageNumber, 50);
        final UsersPage page = mgmt.users().list(filter).execute();
        users.addAll(page.getItems());
        if (page.getTotal() > users.size())
        {
            getNextUserPage(users, pageNumber + 1);
        }
        return users;
    }
    
    @GET
    @ApiOperation("Returns the current user's metadata")
    @Path("metadata/app")
    @PermitAll
    public String getUserAppMetadata(final @Context SecurityContext context)
    {
        final Auth0User prin = (Auth0User) context.getUserPrincipal();
        return prin.getAppMetadata().toString();
    }
    
    @GET
    @ApiOperation("Returns the current user's roleset")
    @Path("roles")
    @PermitAll
    public List<ApplicationRole> getUserRoles(final @Context SecurityContext context)
    {
        final Auth0User prin = (Auth0User) context.getUserPrincipal();
        final String userAppMetadataString = prin.getAppMetadata().toString();
        final ObjectMapper om = new ObjectMapper();
        try
        {
            return om.readValue(userAppMetadataString, UserAppMetadata.class).getRoles();
        }
        catch (final IOException e)
        {
            throw new WebApplicationException("Invalid roleset");
        }
    }
    
    @Override
    Logger logger()
    {
        return LoggerFactory.getLogger(UserResource.class);
    }
    
    @GET
    @Timed
    @Path("auth0/syncAuth0Users")
    @ApiOperation("Syncs Auth0 users with membership records. Returns number of accounts updated.")
    @RolesAllowed("MEMBERSHIP_MANAGE")
    public Integer syncAuth0Users() throws Auth0Exception, UnsupportedEncodingException
    {
        final ManagementAPI mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
        final String emailField = "email.raw";
        final String membershipNumberKey = "membershipNumber";
        
        Integer updateCount = 0;
        final List<MemberDO> members = memberService.getAll();
        for (final MemberDO member : members)
        {
            final List<User> usersByEmail = getAuth0UsersByField(emailField, member.getEmail());
            
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
                if (!roles.contains(ApplicationRole.FORUM.toString()))
                {
                    logger().debug("User {} doesn't have FORUM role", user.getEmail());
                    roles.add(ApplicationRole.FORUM.toString());
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
                        mgmt.users().update(user.getId(), newUser).execute();
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
        
        final List<User> users = getAllAuth0Users();
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
                if (roles.contains(ApplicationRole.FORUM.toString()))
                {
                    logger().debug("User {} has FORUM role", user.getEmail());
                    roles.remove(ApplicationRole.FORUM.toString());
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
                    mgmt.users().update(user.getId(), newUser).execute();
                }
            }
        }
        
        return updateCount;
    }
}