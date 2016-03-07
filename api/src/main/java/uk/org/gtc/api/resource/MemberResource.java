package uk.org.gtc.api.resource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mongodb.MongoException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.LocationType;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.domain.MemberType;
import uk.org.gtc.api.domain.Salutation;
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
	
	@POST
	@Path("{id}/accept")
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberDO acceptMembership(@PathParam("id") String id)
	{
		final MemberDO appliedMember = memberService.getById(id);
		final MemberDO approvedMember = memberService.getById(id);
		
		approvedMember.setMembershipNumber(memberService.getNextMemberNumber());
		
		final MemberDO acceptedMember = memberService.update(appliedMember, approvedMember);
		return acceptedMember;
	}
	
	private List<String> checkValidMember(final MemberDO member, final Boolean shouldThrowException)
	{
		final List<String> validationMessages = new ArrayList<String>();
		final Set<ConstraintViolation<MemberDO>> violations = validator.validate(member);
		if (!violations.isEmpty())
		{
			for (ConstraintViolation<MemberDO> v : violations)
			{
				String message = "ID [" + member.getId() + "] (#" + member.getMembershipNumber() + ") failed validation: '"
						+ v.getInvalidValue() + "' " + v.getMessage();
				if (shouldThrowException)
				{
					throw new ValidationException(validationMessages.toString());
				}
				validationMessages.add(message);
			}
			return validationMessages;
		}
		return Collections.emptyList();
	}
	
	private List<List<String>> checkValidMembers(final List<MemberDO> members, final Boolean shouldThrowException)
	{
		final List<List<String>> fullMessages = new ArrayList<List<String>>();
		for (final MemberDO member : members)
		{
			final List<String> validationMessages = checkValidMember(member, shouldThrowException);
			if (!validationMessages.isEmpty())
			{
				fullMessages.add(checkValidMember(member, shouldThrowException));
			}
		}
		return fullMessages;
	}
	
	@GET
	@Timed
	@Path("cleanup")
	@ApiOperation("Process members and clean up the data")
	@RolesAllowed("MEMBERSHIPADMIN")
	public List<List<String>> cleanupMembers()
	{
		final List<MemberDO> members = memberService.getAll();
		for (MemberDO member : members)
		{
			String email = member.getEmail();
			if (!UtilityHelper.isNullOrEmpty(email))
			{
				member.setEmail(email.trim());
				memberService.update(member, member);
			}
		}
		return checkValidMembers(members, false);
	}
	
	@POST
	@Timed
	@ApiOperation("Create a new member")
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberDO createMember(final MemberDO member) throws Exception
	{
		if (memberService.findByMemberNumber(member.getMembershipNumber()).isEmpty())
		{
			return super.createItem(member);
		}
		else
			throw new Exception("A member already exists with this membership number");
	}
	
	@GET
	@Timed
	@Path("search/{query}")
	@ApiOperation("Get member by Membership Number")
	@RolesAllowed("MEMBERSHIPADMIN")
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
	
	@Override
	@GET
	@Timed
	@Path("all")
	@ApiOperation(value = "Return a list of all members", response = MemberDO.class, responseContainer = "List")
	@RolesAllowed("MEMBERSHIPADMIN")
	public List<MemberDO> getAll()
	{
		logger().debug("Fetching all members");
		return super.getAll();
	}
	
	@GET
	@Timed
	@Path("applications")
	@ApiOperation(value = "Return a list of people who are in the application stage", response = MemberDO.class, responseContainer = "List")
	@RolesAllowed("MEMBERSHIPADMIN")
	public List<MemberDO> getApplications()
	{
		logger().debug("Fetching all members in the application stage");
		return memberService.getByStatus(MemberStatus.APPLIED, MemberStatus.APPROVED, MemberStatus.INVOICED, MemberStatus.PAID,
				MemberStatus.DECLINED);
	}
	
	@GET
	@Timed
	@Path("status/{status}")
	@RolesAllowed("MEMBERSHIPADMIN")
	public List<MemberDO> getByStatus(@PathParam("status") final String status)
	{
		logger().debug("Fetching all " + status + " members");
		return memberService.getByStatus(MemberStatus.valueOf(status.toUpperCase()));
	}
	
	@GET
	@Timed
	@Path("locationTypes")
	@ApiOperation(value = "Return the list of possible locations", response = Array.class)
	@PermitAll
	public LocationType[] getLocationTypes()
	{
		return LocationType.values();
	}
	
	@GET
	@Timed
	@Path("id/{id}")
	@ApiOperation("Get member by GUID")
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberDO getMemberById(@PathParam("id") String id) throws WebApplicationException
	{
		logger().debug("Fetching member by ID " + id);
		return super.getItemById(id);
	}
	
	@GET
	@Timed
	@Path("{memberNumber}")
	@ApiOperation("Get member by Membership Number")
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberDO getMemberByNumber(@PathParam("memberNumber") Long memberNumber) throws MongoException
	{
		logger().debug("Fetching member by membership number " + memberNumber);
		
		MemberDO fetchedMember = memberService.getByMemberNumber(memberNumber);
		checkValidMember(fetchedMember, false);
		return fetchedMember;
	}
	
	@GET
	@Timed
	@Path("memberTypes")
	@ApiOperation(value = "Return the list of possible member types", response = Array.class)
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberType[] getMemberTypes()
	{
		return MemberType.values();
	}
	
	@GET
	@Timed
	@Path("nextMemberNumber")
	@ApiOperation(value = "Fetch the next logical membership number")
	@RolesAllowed("MEMBERSHIPADMIN")
	public Long getNextMembershipNumber()
	{
		return memberService.getNextMemberNumber();
	}
	
	@GET
	@Timed
	@Path("salutationTypes")
	@ApiOperation(value = "Return the list of possible salutations", response = Array.class)
	@PermitAll
	public Salutation[] getSalutationTypes()
	{
		return Salutation.values();
	}
	
	@GET
	@Timed
	@Path("statusTypes")
	@ApiOperation(value = "Return the list of possible statuses", response = Array.class)
	@PermitAll
	public MemberStatus[] getStatusTypes()
	{
		return MemberStatus.values();
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(MemberResource.class);
	}
	
	@PUT
	@Timed
	@Path("id/{id}")
	@ApiOperation("Update member by GUID")
	@RolesAllowed("MEMBERSHIPADMIN")
	public MemberDO updateMemberById(@PathParam("id") String id, MemberDO member) throws WebApplicationException
	{
		final MemberDO existingMember = memberService.getById(id);
		
		if (!existingMember.getStatus().equals(member.getStatus()))
		{
			final MemberStatus existingStatus = existingMember.getStatus();
			final MemberStatus newStatus = member.getStatus();
			switch (newStatus)
			{
			case APPLIED:
				if (!existingStatus.equals(MemberStatus.DECLINED) && !existingStatus.equals(MemberStatus.REMOVED)
						&& !existingStatus.equals(MemberStatus.LAPSED))
					throw new WebApplicationException();
				break;
			case APPROVED:
			case DECLINED:
				if (!existingStatus.equals(MemberStatus.APPLIED))
					throw new WebApplicationException();
				break;
			case INVOICED:
				if (!existingStatus.equals(MemberStatus.APPROVED))
					throw new WebApplicationException();
				break;
			case PAID:
				if (!existingStatus.equals(MemberStatus.INVOICED))
					throw new WebApplicationException();
				break;
			case CURRENT:
				if (!existingStatus.equals(MemberStatus.PAID))
					throw new WebApplicationException();
				break;
			case LAPSED:
				if (!existingStatus.equals(MemberStatus.CURRENT))
					throw new WebApplicationException();
				break;
			case REMOVED:
				if (!existingStatus.equals(MemberStatus.CURRENT) && !existingStatus.equals(MemberStatus.LAPSED))
					throw new WebApplicationException();
				break;
			default:
				break;
			}
		}
		
		final MemberDO updatedMember = memberService.update(existingMember, member);
		
		return updatedMember;
	}
	
	@GET
	@Timed
	@Path("{membershipNumber}/{lastName}/verify")
	@ApiOperation("Verify a member by their Membership Number and their Last Name")
	@PermitAll
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
	@Path("{memberNumber}/welcome")
	@RolesAllowed("MEMBERSHIPADMIN")
	public Object welcomeEmail(@PathParam("memberNumber") Long memberNumber) throws Exception
	{
		final MemberDO app = memberService.getByMemberNumber(memberNumber);
		return memberService.sendEmail(app);
	}
}