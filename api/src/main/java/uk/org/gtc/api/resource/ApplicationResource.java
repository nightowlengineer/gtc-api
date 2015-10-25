package uk.org.gtc.api.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.service.ApplicationService;

@Path("application")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource extends GenericResource<ApplicationDO>
{
	
	private ApplicationService applicationService;
	
	public ApplicationResource(ApplicationService applicationService)
	{
		super(applicationService);
		this.applicationService = applicationService;
	}
}