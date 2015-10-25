package uk.org.gtc.api.service;

import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.ApplicationDO;

public class ApplicationService extends GenericService<ApplicationDO>
{
	private JacksonDBCollection<ApplicationDO, String> collection;
	
	public ApplicationService(JacksonDBCollection<ApplicationDO, String> applications)
	{
		super(applications);
		this.collection = applications;
	}
	
}
