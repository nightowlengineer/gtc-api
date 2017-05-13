package uk.org.gtc.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mongodb.MongoException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.CsvMember;
import uk.org.gtc.api.domain.ImportDiff;
import uk.org.gtc.api.domain.LocationType;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.domain.MemberType;
import uk.org.gtc.api.domain.Salutation;
import uk.org.gtc.api.exception.MemberImportException;
import uk.org.gtc.api.exception.MemberNotFoundException;
import uk.org.gtc.api.service.MemberService;
import us.monoid.json.JSONException;

@Path("member")
@Api("member")
@Produces(MediaType.APPLICATION_JSON)
public class MemberResource extends GenericResource<MemberDO>
{
	private final MemberService memberService;
	
	public MemberResource(final MemberService memberService)
	{
		super(memberService);
		this.memberService = memberService;
	}
	
	@POST
	@Path("{id}/accept")
	@RolesAllowed("MEMBERSHIP_MANAGE")
	public MemberDO acceptMembership(final @PathParam("id") String id)
	{
		final MemberDO appliedMember = memberService.getById(id);
		final MemberDO approvedMember = memberService.getById(id);
		
		approvedMember.setMembershipNumber(memberService.getNextMemberNumber());
		
		return memberService.update(appliedMember, approvedMember);
	}
	
	private List<String> checkValidMember(final MemberDO member, final Boolean shouldThrowException)
	{
		final List<String> validationMessages = new ArrayList<>();
		final Set<ConstraintViolation<MemberDO>> violations = validator.validate(member);
		if (!violations.isEmpty())
		{
			for (final ConstraintViolation<MemberDO> v : violations)
			{
				final String message = "ID [" + member.getId() + "] (#" + member.getMembershipNumber() + ") failed validation: '"
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
		final List<List<String>> fullMessages = new ArrayList<>();
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
	@RolesAllowed("ADMIN")
	public List<List<String>> cleanupMembers()
	{
		final List<MemberDO> members = memberService.getAll();
		for (final MemberDO member : members)
		{
			final String email = member.getEmail();
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
	@RolesAllowed("MEMBERSHIP_MANAGE")
	public MemberDO createMember(final MemberDO member) throws Exception
	{
		if (UtilityHelper.isNull(member.getMembershipNumber()))
		{
			member.setMembershipNumber(memberService.getNextMemberNumber());
		}
		
		checkValidMember(member, true);
		
		if (memberService.findByMemberNumber(member.getMembershipNumber()).isEmpty())
		{
			return super.createItem(member);
		}
		else
		{
			throw new ValidationException("A member already exists with this membership number");
		}
		
	}
	
	@GET
	@Timed
	@Path("search/{query}")
	@ApiOperation("Get member by Membership Number")
	@RolesAllowed("MEMBERSHIP_READ")
	public List<MemberDO> findMember(final @PathParam("query") String query)
	{
		logger().debug("Finding member using %s", query);
		final List<MemberDO> results = new ArrayList<>();
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
			if (member.getFirstName().toLowerCase().concat(" ").concat(member.getLastName().toLowerCase()).contains(query.toLowerCase()))
			{
				if (!results.contains(member))
				{
					results.add(member);
				}
			}
		}
		return results;
	}
	
	@Override
	@GET
	@Timed
	@Path("all")
	@ApiOperation(value = "Return a list of all members", response = MemberDO.class, responseContainer = "List")
	@RolesAllowed("MEMBERSHIP_READ")
	public List<MemberDO> getAll()
	{
		logger().debug("Fetching all members");
		return super.getAll();
	}
	
	@GET
	@Timed
	@Path("applications")
	@ApiOperation(value = "Return a list of people who are in the application stage", response = MemberDO.class, responseContainer = "List")
	@RolesAllowed("MEMBERSHIP_READ")
	public List<MemberDO> getApplications()
	{
		logger().debug("Fetching all members in the application stage");
		return memberService.getByStatus(MemberStatus.APPLIED, MemberStatus.APPROVED, MemberStatus.INVOICED, MemberStatus.PAID,
				MemberStatus.DECLINED);
	}
	
	@GET
	@Timed
	@Path("status/{status}")
	@RolesAllowed("MEMBERSHIP_READ")
	public List<MemberDO> getByStatus(final @PathParam("status") MemberStatus status)
	{
		logger().debug("Fetching all " + status + " members");
		return memberService.getByStatus(status);
	}
	
	/**
	 * @param context
	 * @return
	 */
	private Long getCurrentUserMembershipNumber(final SecurityContext context)
	{
		final Auth0User prin = (Auth0User) context.getUserPrincipal();
		Long membershipNumber = null;
		try
		{
			membershipNumber = prin.getAppMetadata().getLong("membershipNumber");
		}
		catch (final JSONException e)
		{
			logger().warn("Could not find membership number for this user: " + prin.getUserId());
			throw new MemberNotFoundException();
		}
		return membershipNumber;
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
	@RolesAllowed("MEMBERSHIP_READ")
	public MemberDO getMemberById(final @PathParam("id") String id)
	{
		logger().debug("Fetching member by ID " + id);
		MemberDO member = null;
		try
		{
			member = super.getItemById(id);
		}
		catch (final WebApplicationException wae)
		{
			if (wae.getResponse().equals(org.eclipse.jetty.server.Response.SC_NOT_FOUND))
			{
				throw new MemberNotFoundException();
			}
			else
			{
				throw new WebApplicationException(wae);
			}
		}
		
		return member;
	}
	
	@GET
	@Timed
	@Path("{memberNumber}")
	@ApiOperation("Get member by Membership Number")
	@RolesAllowed("MEMBERSHIP_READ")
	public MemberDO getMemberByNumber(final @PathParam("memberNumber") Long memberNumber) throws MongoException
	{
		logger().debug("Fetching member by membership number " + memberNumber);
		
		final MemberDO fetchedMember = memberService.getByMemberNumber(memberNumber);
		checkValidMember(fetchedMember, false);
		return fetchedMember;
	}
	
	@GET
	@Timed
	@Path("memberTypes")
	@ApiOperation(value = "Return the list of possible member types", response = Array.class)
	@PermitAll
	public MemberType[] getMemberTypes()
	{
		return MemberType.values();
	}
	
	@GET
	@Timed
	@Path("me")
	@ApiOperation("Get the current user's member record")
	@PermitAll
	public MemberDO getMyMembership(final @Context SecurityContext context) throws JSONException
	{
		final Long membershipNumber = getCurrentUserMembershipNumber(context);
		return memberService.getByMemberNumber(membershipNumber);
	}
	
	@GET
	@Timed
	@Path("nextMemberNumber")
	@ApiOperation(value = "Fetch the next logical membership number")
	@PermitAll
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
	
	/**
	 * Gets a {@link Set} of member numbers.
	 * 
	 * @return a {@link Set} of member numbers;
	 */
	private Collection<? extends Long> getSetOfMemberNumbers()
	{
		final Set<Long> memberNumbers = new HashSet<>();
		// Get set of existing membership numbers
		final List<MemberDO> members = memberService.getAll();
		for (final MemberDO member : members)
		{
			memberNumbers.add(member.getMembershipNumber());
		}
		return memberNumbers;
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
	
	/**
	 * Create or update a member from an import.
	 * 
	 * @param csvMember
	 * @param diffs
	 * @return an updated list of diffs
	 */
	public ImportDiff importCreateUpdateMember(final CsvMember csvMember, final ImportDiff diffs)
	{
		// Add to overall import set
		final Set<Long> importedSet = diffs.getImportedSet();
		final Set<Long> updatedSet = diffs.getUpdatedSet();
		final Set<Long> createdSet = diffs.getCreatedSet();
		
		importedSet.add(csvMember.getMembershipNumber());
		final MemberDO existingMember = memberService.getByMemberNumber(csvMember.getMembershipNumber());
		
		// Member exists
		if (existingMember != null)
		{
			final MemberDO updatedMember = memberService.getByMemberNumber(csvMember.getMembershipNumber());
			if (updatedMember.getEmail() != null && !updatedMember.getEmail().equals(csvMember.getEmail()))
			{
				updatedMember.setEmail(csvMember.getEmail());
			}
			if (updatedMember.getStatus() != null && !updatedMember.getStatus().equals(csvMember.getStatus()))
			{
				updatedMember.setStatus(csvMember.getStatus());
			}
			if (updatedMember.getType() != null && !updatedMember.getType().equals(csvMember.getType()))
			{
				updatedMember.setType(csvMember.getType());
			}
			memberService.update(existingMember, updatedMember);
			updatedSet.add(csvMember.getMembershipNumber());
		}
		// Member does not exist
		else
		{
			memberService.create(new MemberDO(csvMember.getType(), csvMember.getStatus(), csvMember.getMembershipNumber(),
					csvMember.getSalutation(), csvMember.getFirstName(), csvMember.getLastName(), csvMember.getEmail(), null, null, null,
					null, null, null, null, null));
			createdSet.add(csvMember.getMembershipNumber());
		}
		
		diffs.setCreatedSet(createdSet);
		diffs.setUpdatedSet(updatedSet);
		diffs.setImportedSet(importedSet);
		
		return diffs;
	}
	
	/**
	 * Get the existing list of members in the database, and remove all that
	 * exist in the imported document. From here, we have the intersection of
	 * members that have a record in this system, but not in the import, and
	 * should therefore be deleted.
	 * 
	 * @param diffs
	 * @return an updated list of diffs
	 */
	public ImportDiff importDeleteMember(final ImportDiff diffs)
	{
		final Set<Long> existingSet = diffs.getExistingSet();
		final Set<Long> importedSet = diffs.getImportedSet();
		final Set<Long> deletedSet = diffs.getDeletedSet();
		final Set<Long> errorSet = diffs.getErrorSet();
		existingSet.removeAll(importedSet);
		
		for (final Long memberNumber : existingSet)
		{
			String errorMessage = null;
			final List<MemberDO> memberList = memberService.findByMemberNumber(memberNumber);
			if (memberList.size() == 1)
			{
				final Boolean deleteStatus = memberService.delete(memberList.get(0));
				if (deleteStatus)
				{
					deletedSet.add(memberNumber);
					continue;
				}
				else
				{
					errorMessage = memberNumber + " was not deleted due to an unknown error";
				}
			}
			else if (memberList.size() > 1)
			{
				errorMessage = memberNumber + " was not deleted as there is more than one existing member with that number.";
				
			}
			else if (memberList.isEmpty())
			{
				errorMessage = memberNumber + " could not be deleted, as a record did not exist.";
			}
			errorSet.add(memberNumber);
			logger().error(errorMessage);
		}
		
		diffs.setDeletedSet(deletedSet);
		diffs.setErrorSet(errorSet);
		diffs.setImportedSet(importedSet);
		diffs.setExistingSet(existingSet);
		
		return diffs;
	}
	
	@POST
	@Timed
	@Path("upload/{delete}")
	@ApiOperation("Administrator can upload new membership information from a CSV file")
	@RolesAllowed("MEMBERSHIP_MANAGE")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public ImportDiff importMembersFromCsv(@FormDataParam("file") final InputStream csv, @PathParam("delete") final Boolean delete)
	{
		final CsvMapper mapper = new CsvMapper();
		final CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');
		MappingIterator<CsvMember> it;
		try
		{
			it = mapper.readerFor(CsvMember.class).with(schema).readValues(csv);
		}
		catch (final IOException ioe)
		{
			logger().warn("Could not process import", ioe);
			throw new MemberImportException("Couldn't process the file that was provided. Please confirm it matches the spec.");
		}
		
		final Set<Long> createdSet = new HashSet<>();
		final Set<Long> updatedSet = new HashSet<>();
		final Set<Long> deletedSet = new HashSet<>();
		final Set<Long> errorSet = new HashSet<>();
		final Set<Long> importedSet = new HashSet<>();
		final Set<Long> existingSet = new HashSet<>();
		
		existingSet.addAll(getSetOfMemberNumbers());
		
		final ImportDiff diffs = new ImportDiff();
		diffs.setCreatedSet(createdSet);
		diffs.setUpdatedSet(updatedSet);
		diffs.setDeletedSet(deletedSet);
		diffs.setErrorSet(errorSet);
		diffs.setImportedSet(importedSet);
		diffs.setExistingSet(existingSet);
		
		// Process members to create/update
		while (it.hasNext())
		{
			final CsvMember csvMember = it.next();
			importCreateUpdateMember(csvMember, diffs);
		}
		
		if (delete)
		{
			importDeleteMember(diffs);
		}
		
		return diffs;
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
	@RolesAllowed("MEMBERSHIP_MANAGE")
	public MemberDO updateMemberById(final @PathParam("id") String id, final MemberDO member) throws WebApplicationException
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
				{
					throw new WebApplicationException();
				}
				break;
			case APPROVED:
			case DECLINED:
				if (!existingStatus.equals(MemberStatus.APPLIED))
				{
					throw new WebApplicationException();
				}
				break;
			case INVOICED:
				if (!existingStatus.equals(MemberStatus.APPROVED))
				{
					throw new WebApplicationException();
				}
				break;
			case PAID:
				if (!existingStatus.equals(MemberStatus.INVOICED))
				{
					throw new WebApplicationException();
				}
				break;
			case CURRENT:
				if (!existingStatus.equals(MemberStatus.PAID))
				{
					throw new WebApplicationException();
				}
				break;
			case LAPSED:
				if (!existingStatus.equals(MemberStatus.CURRENT))
				{
					throw new WebApplicationException();
				}
				break;
			case REMOVED:
				if (!existingStatus.equals(MemberStatus.CURRENT) && !existingStatus.equals(MemberStatus.LAPSED))
				{
					throw new WebApplicationException();
				}
				break;
			default:
				break;
			}
		}
		
		return memberService.update(existingMember, member);
	}
	
	@PUT
	@Timed
	@Path("me")
	@ApiOperation("Update a member's own record")
	@RolesAllowed("MEMBER")
	public MemberDO updateMyMembership(final @Context SecurityContext context, final MemberDO newMember)
			throws WebApplicationException, JSONException
	{
		final Long membershipNumber = getCurrentUserMembershipNumber(context);
		
		final MemberDO existingMember = memberService.getByMemberNumber(membershipNumber);
		
		if (!existingMember.getStatus().equals(newMember.getStatus()))
		{
			throw new WebApplicationException("You can not update your own membership status.");
		}
		
		if (!existingMember.getType().equals(newMember.getType()))
		{
			throw new WebApplicationException("You can not change your own membership type.");
		}
		
		newMember.setId(existingMember.getId());
		newMember.setCreatedDate(existingMember.getCreatedDate());
		newMember.setLastUpdatedDate(new Date());
		newMember.setStatus(existingMember.getStatus());
		newMember.setType(existingMember.getType());
		
		return memberService.update(existingMember, newMember);
	}
	
	@GET
	@Timed
	@Path("{membershipNumber}/{lastName}/verify")
	@ApiOperation("Verify a member by their Membership Number and their Last Name")
	@PermitAll
	public Boolean verifyMemberByNumberAndSurname(final @PathParam("membershipNumber") Long membershipNumber,
			final @PathParam("lastName") String lastName)
	{
		final MemberDO memberToVerify = memberService.getByMemberNumber(membershipNumber);
		return memberToVerify.getLastName().equalsIgnoreCase(lastName) && memberToVerify.getStatus() == MemberStatus.CURRENT;
	}
	
	@GET
	@Path("{memberNumber}/welcome")
	@PermitAll
	public Response welcomeEmail(final @PathParam("memberNumber") Long memberNumber)
	{
		return Response.noContent().status(Status.NOT_IMPLEMENTED).build();
	}
}