package uk.org.gtc.api.service;

import java.io.IOException;

import org.mongojack.JacksonDBCollection;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

import uk.org.gtc.api.EmailHelper;
import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.MemberDO;

public class ApplicationService extends GenericService<ApplicationDO>
{
	private JacksonDBCollection<ApplicationDO, String> collection;
	private MandrillApi mandrill;
	
	public ApplicationService(JacksonDBCollection<ApplicationDO, String> applications, MandrillApi mandrill)
	{
		super(applications);
		this.collection = applications;
		this.mandrill = mandrill;
	}
	
	public Object sendEmail(MemberDO member) throws MandrillApiError, IOException
	{
		return EmailHelper.singleRecipient(mandrill, "welcome", member, Boolean.TRUE);
	}
	
}
