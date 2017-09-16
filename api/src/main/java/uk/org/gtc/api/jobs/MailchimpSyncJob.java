package uk.org.gtc.api.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecwid.maleorang.MailchimpClient;
import com.ecwid.maleorang.MailchimpException;
import com.ecwid.maleorang.MailchimpObject;
import com.ecwid.maleorang.method.v3_0.batches.StartBatchMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.EditMemberMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.GetMembersMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.MemberInfo;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.DelayStart;
import de.spinscale.dropwizard.jobs.annotations.Every;
import uk.org.gtc.api.ApplicationMode;
import uk.org.gtc.api.GtcConfiguration;
import uk.org.gtc.api.MemberServiceFactory;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.MemberService;

@Every("1min")
@DelayStart("30s")
public class MailchimpSyncJob extends Job
{
    /**
     * Sync metadata (first/last names) with Mailchimp.
     * In the future, this method should use membership numbers, not just email
     * addresses which could be changed by users on Mailchimp. On the latter
     * case, a webhook could be implemented to listen to the 'subscribe' and
     * 'upemail' events from Mailchimp to trigger actions internally
     */
    @Override
    public void doJob(final JobExecutionContext context) throws JobExecutionException
    {
        final GtcConfiguration configuration = GtcConfiguration.getInstance();
        final MemberService memberService = MemberServiceFactory.getInstance();
        final List<MemberInfo> mailchimpMembers = new ArrayList<>();
        final List<MemberDO> allMembers = memberService.getAll();
        // Extract email addresses from membership database
        final Set<String> emailList = allMembers.stream().map(MemberDO::getEmail).collect(Collectors.toSet());
        
        // Get list of members from Mailchimp
        final GetMembersMethod getMailchimpMembers = new GetMembersMethod(configuration.mailchimpListId);
        // TODO: Pagination.
        // 2000 is the max size of this list on the current plan
        getMailchimpMembers.count = 2000;
        getMailchimpMembers.fields = "members.email_address,members.merge_fields";
        
        try (final MailchimpClient client = new MailchimpClient(configuration.mailchimpApiKey))
        {
            mailchimpMembers.addAll(client.execute(getMailchimpMembers).members);
        }
        catch (final IOException | MailchimpException e)
        {
            logger().error("Couldn't communicate with Mailchimp during scheduled job run", e);
        }
        
        // Intersect email list with those from Mailchimp
        // (only update members we know about)
        emailList.retainAll(mailchimpMembers.stream().map(m -> m.email_address).collect(Collectors.toSet()));
        final List<MemberDO> intersectedMembers = allMembers.stream().filter(m -> emailList.contains(m.getEmail()))
                .collect(Collectors.toList());
        
        final List<EditMemberMethod> batchMethods = new ArrayList<>();
        for (final MemberDO member : intersectedMembers)
        {
            final Map<String, Object> mappingFields = new HashMap<>();
            mappingFields.put("FNAME", member.getFirstName());
            mappingFields.put("LNAME", member.getLastName());
            mappingFields.put("TYPE", member.getType().toString());
            mappingFields.put("MEMNUM", member.getMembershipNumber());
            
            final MemberInfo memberInfo = new MemberInfo();
            memberInfo.email_address = member.getEmail();
            memberInfo.merge_fields = new MailchimpObject();
            memberInfo.merge_fields.mapping.putAll(mappingFields);
            
            if (!mailchimpMembers.contains(memberInfo))
            {
                final EditMemberMethod method = new EditMemberMethod.Update(configuration.mailchimpListId, member.getEmail());
                method.merge_fields = new MailchimpObject();
                method.merge_fields.mapping.putAll(mappingFields);
                if (logger().isDebugEnabled())
                {
                    logger().debug("Updating {}", method.toString());
                }
                batchMethods.add(method);
            }
        }
        
        logger().info("Executing scheduled job MailchimpSyncJob");
        if (batchMethods.size() > 0)
        {
            try (final MailchimpClient client = new MailchimpClient(configuration.mailchimpApiKey))
            {
                logger().info("Sending batch update to Mailchimp with {} operations inside", batchMethods.size());
                if (configuration.appMode == ApplicationMode.LIVE)
                {
                    client.execute(new StartBatchMethod(batchMethods));
                }
                else
                {
                    logger().debug(batchMethods.toString());
                }
            }
            catch (final IOException | MailchimpException e)
            {
                logger().error("Couldn't communicate with Mailchimp during scheduled job run", e);
            }
        }
    }
    
    Logger logger()
    {
        return LoggerFactory.getLogger(MailchimpSyncJob.class);
    }
}
