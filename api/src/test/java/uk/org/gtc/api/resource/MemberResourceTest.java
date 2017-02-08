package uk.org.gtc.api.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.service.MemberService;

/**
 * Unit test for simple App.
 */
public class MemberResourceTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	final MemberService memberService = Mockito.mock(MemberService.class);
	
	final MemberResource memberResource = new MemberResource(memberService);
	
	public final MemberDO appliedMember = new MemberDO();
	public final MemberDO declinedMember = new MemberDO();
	public final MemberDO approvedMember = new MemberDO();
	public final MemberDO invoicedMember = new MemberDO();
	public final MemberDO paidMember = new MemberDO();
	public final MemberDO currentMember = new MemberDO();
	public final MemberDO lapsedMember = new MemberDO();
	public final MemberDO removedMember = new MemberDO();
	
	public final String id = new ObjectId().toString();
	
	@Before
	public void createMembers()
	{
		appliedMember.setStatus(MemberStatus.APPLIED);
		declinedMember.setStatus(MemberStatus.DECLINED);
		approvedMember.setStatus(MemberStatus.APPROVED);
		invoicedMember.setStatus(MemberStatus.INVOICED);
		paidMember.setStatus(MemberStatus.PAID);
		currentMember.setStatus(MemberStatus.CURRENT);
		lapsedMember.setStatus(MemberStatus.LAPSED);
		removedMember.setStatus(MemberStatus.REMOVED);
	}
	
	@Test
	public void testGetCurrent() throws Exception
	{
		final List<MemberDO> currentMembers = new ArrayList<MemberDO>();
		currentMembers.add(currentMember);
		Mockito.when(memberService.getByStatus(MemberStatus.CURRENT)).thenReturn(currentMembers);
		
		memberResource.getByStatus(MemberStatus.CURRENT);
	}
	
	@Test
	public void testUpdateMemberAppliedApproved() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberAppliedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberAppliedDeclined() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberAppliedInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberAppliedLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberAppliedPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberAppliedRemoved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(appliedMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberApprovedApplied() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberApprovedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberApprovedDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberApprovedInvoiced() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberApprovedLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberApprovedPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberApprovedRemoved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(approvedMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentApplied() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentLapsed() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberCurrentPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberCurrentRemoved() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(currentMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedApplied() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberDeclinedRemoved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(declinedMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedApplied() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedPaid() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberInvoicedRemoved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(invoicedMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberLapsedApplied() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberLapsedApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberLapsedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberLapsedDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberLapsedInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberLapsedPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
	
	@Test
	public void testUpdateMemberLapsedRemoved() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(lapsedMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberPaidApplied() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberPaidApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberPaidCurrent() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberPaidDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberPaidInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberPaidLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberPaidRemoved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(paidMember);
		
		memberResource.updateMemberById(id, removedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedApplied() throws Exception
	{
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, appliedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedApproved() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, approvedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedCurrent() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, currentMember);
	}
	
	@Test
	public void testUpdateMemberRemovedDeclined() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, declinedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedInvoiced() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, invoicedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedLapsed() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, lapsedMember);
	}
	
	@Test
	public void testUpdateMemberRemovedPaid() throws Exception
	{
		exception.expect(WebApplicationException.class);
		
		Mockito.when(memberService.getById(id)).thenReturn(removedMember);
		
		memberResource.updateMemberById(id, paidMember);
	}
}
