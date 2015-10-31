package uk.org.gtc.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.MergeVar;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.MergeVarBucket;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;

import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberType;

public class EmailHelper
{
	
	public EmailHelper()
	{
	
	}
	
	public static Map<String, String> getTemplateContent(final MergeVar[] mergeValues)
	{
		final Map<String, String> templateContent = new HashMap<String, String>();
		for (MergeVar value : mergeValues)
		{
			templateContent.put(value.getName(), value.getContent().toString());
		}
		return templateContent;
	}
	
	public static MergeVar[] getMergeValues(final MemberDO member)
	{
		final MergeVar[] mergeValues = { new MergeVar("member_first_name", member.getFirstName()),
				new MergeVar("member_grade", member.getType().toString()), new MergeVar("member_number", member.getMembershipNumber()),
				new MergeVar("initial_password", "still in development"),
				new MergeVar("sponsor_info", member.getType().equals(MemberType.SPONSOR)) };
				
		return mergeValues;
	}
	
	public static List<MergeVarBucket> makeBucket(final String email, final MergeVar[] mergeValues)
	{
		final MergeVarBucket mergeBucket = new MergeVarBucket();
		mergeBucket.setRcpt(email);
		mergeBucket.setVars(mergeValues);
		
		final List<MergeVarBucket> mergeBuckets = new ArrayList<MergeVarBucket>();
		mergeBuckets.add(mergeBucket);
		
		return mergeBuckets;
	}
	
	public static Object singleRecipient(final MandrillApi mandrill, final String template, final MemberDO member, final Boolean send)
			throws MandrillApiError, IOException
	{
		final Recipient recipient = new Recipient();
		recipient.setEmail(member.getEmail());
		
		final List<Recipient> recipients = new ArrayList<Recipient>();
		recipients.add(recipient);
		
		final MandrillMessage message = new MandrillMessage();
		message.setTo(recipients);
		message.setMergeLanguage("handlebars");
		message.setMergeVars(makeBucket(member.getEmail(), getMergeValues(member)));
		
		final Map<String, String> templateContent = getTemplateContent(getMergeValues(member));
		// return templateContent;
		
		if (send)
		{
			final MandrillMessageStatus[] status = mandrill.messages().sendTemplate("welcome", templateContent, message, false);
			Boolean statusResult = Boolean.FALSE;
			
			for (MandrillMessageStatus indStatus : status)
			{
				if (indStatus.getStatus().equalsIgnoreCase("sent"))
					statusResult = Boolean.TRUE;
				else
					statusResult = Boolean.FALSE;
			}
			return statusResult;
		}
		else
		{
			return mandrill.templates().render("welcome", templateContent, templateContent);
		}
		
	}
}
