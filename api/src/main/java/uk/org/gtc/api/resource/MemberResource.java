package uk.org.gtc.api.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.service.MemberService;

@Path("member")
@Api("member")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource extends GenericResource<MemberDO>
{
	
	private MemberService memberService;
	
	public MemberResource(MemberService memberService)
	{
		super(memberService);
		this.memberService = memberService;
	}
	
	@Override
	@GET
	@Timed
	@Path("all")
	@ApiOperation(value = "Return a list of all members", response = MemberDO.class, responseContainer = "List")
	public List<MemberDO> getAll()
	{
		logger().debug("Fetching all members");
		return super.getAll();
	}
	
	@GET
	@Timed
	@Path("id/{id}")
	@ApiOperation("Get member by GUID")
	public MemberDO getMemberById(@PathParam("id") String id) throws WebApplicationException
	{
		logger().debug("Fetching member by ID " + id);
		return super.getItemById(id);
	}
	
	@GET
	@Timed
	@Path("{memberNumber}")
	@ApiOperation("Get member by Membership Number")
	public MemberDO getMemberByNumber(@PathParam("memberNumber") Long memberNumber) throws Exception
	{
		logger().debug("Fetching member by membership number " + memberNumber);
		return memberService.getByMemberNumber(memberNumber);
	}
	
	@GET
	@Timed
	@Path("search/{query}")
	@ApiOperation("Get member by Membership Number")
	public List<MemberDO> findMember(@PathParam("query") String query) throws Exception
	{
		logger().debug("Finding member using " + query);
		final List<MemberDO> results = new ArrayList<MemberDO>();
		final List<MemberDO> members = memberService.getAll();
		for (final MemberDO member : members)
		{
			if (member.getFirstName().toLowerCase().contains(query.toLowerCase()))
			{
				if (!results.contains(member))
				{
					results.add(member);
				}
				continue;
			}
			if (member.getLastName().toLowerCase().contains(query.toLowerCase()))
			{
				if (!results.contains(member))
				{
					results.add(member);
				}
				continue;
			}
			if (member.getMembershipNumber().toString().toLowerCase().contains(query.toLowerCase()))
			{
				if (!results.contains(member))
				{
					results.add(member);
				}
				continue;
			}
		}
		return results;
	}
	
	@GET
	@Timed
	@Path("{membershipNumber}/{lastName}/verify")
	@ApiOperation("Verify a member by their Membership Number and their Last Name")
	public Boolean verifyMemberByNumberAndSurname(final @PathParam("membershipNumber") Long membershipNumber,
			final @PathParam("lastName") String lastName) throws Exception
	{
		try
		{
			final MemberDO memberToVerify = memberService.getByMemberNumber(membershipNumber);
			if (memberToVerify.getLastName().equalsIgnoreCase(lastName) && memberToVerify.getStatus() == MemberStatus.CURRENT)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		
	}
	
	@GET
	@Timed
	@Path("nextMemberNumber")
	@ApiOperation(value = "Fetch the next logical membership number")
	public Long getNextMembershipNumber()
	{
		return memberService.getNextMemberNumber();
	}
	
	@POST
	@Timed
	@ApiOperation("Create a new member")
	public MemberDO createMember(MemberDO member) throws Exception
	{
		if (memberService.findByMemberNumber(member.getMembershipNumber()).isEmpty())
		{
			return super.createItem(member);
		}
		else
			throw new Exception("A member already exists with this membership number");
	}
	
	@GET
	@Path("{memberNumber}/welcome")
	public Object welcomeEmail(@PathParam("memberNumber") Long memberNumber) throws Exception
	{
		final MemberDO app = memberService.getByMemberNumber(memberNumber);
		return memberService.sendEmail(app);
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(MemberResource.class);
	}
}