package uk.org.gtc.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.ApplicationService;
import uk.org.gtc.api.service.MemberService;

@Path("application")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource extends GenericResource<ApplicationDO>
{
	private ApplicationService applicationService;
	private MemberService memberService;
	
	public ApplicationResource(ApplicationService applicationService, MemberService memberService)
	{
		super(applicationService);
		this.applicationService = applicationService;
		this.memberService = memberService;
	}
	
	@GET
	@Path("{id}/accept")
	public void acceptMembership(@PathParam("id") String id, ApplicationDO application)
	{
		MemberDO member = new MemberDO(application, memberService.getNextMemberNumber());
		member.setId(id);
		memberService.create(member);
	}
}