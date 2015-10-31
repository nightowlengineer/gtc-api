package uk.org.gtc.api.service;

import org.mongojack.JacksonDBCollection;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;

import uk.org.gtc.api.domain.ApplicationDO;

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
	
	public Boolean sendEmail(ApplicationDO application)
	{
		MandrillMessage message = new MandrillMessage();
		//mandrill.messages().sendTemplate(templateName, templateContent, m, async)
		return Boolean.TRUE;
	}
	
}
