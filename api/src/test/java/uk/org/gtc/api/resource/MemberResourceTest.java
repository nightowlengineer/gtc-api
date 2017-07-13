package uk.org.gtc.api.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import uk.org.gtc.api.MemberServiceFactory;
import uk.org.gtc.api.domain.CsvMember;
import uk.org.gtc.api.domain.ImportDiff;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.service.MemberService;

/**
 * Unit test for simple App.
 */
public class MemberResourceTest extends BaseTest
{
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    final MemberService memberService = Mockito.mock(MemberService.class);
    final MemberResource memberResource;
    
    public final MemberDO appliedMember = new MemberDO();
    public final MemberDO declinedMember = new MemberDO();
    public final MemberDO approvedMember = new MemberDO();
    public final MemberDO invoicedMember = new MemberDO();
    public final MemberDO paidMember = new MemberDO();
    public final MemberDO currentMember = new MemberDO();
    public final MemberDO lapsedMember = new MemberDO();
    public final MemberDO removedMember = new MemberDO();
    
    public final String id = new ObjectId().toString();
    
    public final List<MemberDO> members = new ArrayList<>();
    public final List<MemberDO> lapsedMembers = new ArrayList<>();
    
    public MemberResourceTest()
    {
        MemberServiceFactory.setInstance(memberService);
        memberResource = new MemberResource();
    }
    
    @Before
    public void createMembers()
    {
        appliedMember.setStatus(MemberStatus.APPLIED);
        declinedMember.setStatus(MemberStatus.DECLINED);
        approvedMember.setStatus(MemberStatus.APPROVED);
        invoicedMember.setStatus(MemberStatus.INVOICED);
        paidMember.setStatus(MemberStatus.PAID);
        currentMember.setStatus(MemberStatus.CURRENT);
        currentMember.setMembershipNumber(1000L);
        lapsedMember.setStatus(MemberStatus.LAPSED);
        lapsedMember.setMembershipNumber(2000L);
        removedMember.setStatus(MemberStatus.REMOVED);
        removedMember.setMembershipNumber(9000L);
    }
    
    @Before
    public void setupMemberLists()
    {
        members.add(appliedMember);
        members.add(declinedMember);
        members.add(approvedMember);
        members.add(invoicedMember);
        members.add(paidMember);
        members.add(currentMember);
        members.add(lapsedMember);
        members.add(removedMember);
        
        lapsedMembers.add(lapsedMember);
    }
    
    @Test
    public void testErrorImportMemberDeleted()
    {
        final ImportDiff diffs = new ImportDiff();
        final Set<Long> errorSet = new HashSet<>();
        final Set<Long> existingSet = new HashSet<>();
        final Set<Long> importedSet = new HashSet<>();
        final Set<Long> deletedSet = new HashSet<>();
        existingSet.add(9000L);
        diffs.setImportedSet(importedSet);
        diffs.setExistingSet(existingSet);
        diffs.setErrorSet(errorSet);
        diffs.setDeletedSet(deletedSet);
        Mockito.when(memberService.findByMemberNumber(9000L)).thenReturn(lapsedMembers);
        Mockito.when(memberService.delete(lapsedMember)).thenReturn(false);
        
        final ImportDiff newDiffs = memberResource.importDeleteMember(diffs);
        
        Assert.assertEquals(0, newDiffs.getDeletedSet().size());
        Assert.assertEquals(1, newDiffs.getErrorSet().size());
    }
    
    @Test
    public void testGetCurrent() throws Exception
    {
        final List<MemberDO> currentMembers = new ArrayList<>();
        currentMembers.add(currentMember);
        Mockito.when(memberService.getByStatus(MemberStatus.CURRENT)).thenReturn(currentMembers);
        
        memberResource.getByStatus(MemberStatus.CURRENT);
    }
    
    @Test
    public void testImportMemberAlreadyExists()
    {
        final ImportDiff diffs = new ImportDiff();
        final Set<Long> errorSet = new HashSet<>();
        final Set<Long> existingSet = new HashSet<>();
        final Set<Long> importedSet = new HashSet<>();
        final Set<Long> createdSet = new HashSet<>();
        final Set<Long> updatedSet = new HashSet<>();
        final Set<Long> deletedSet = new HashSet<>();
        
        diffs.setImportedSet(importedSet);
        diffs.setExistingSet(existingSet);
        diffs.setErrorSet(errorSet);
        diffs.setDeletedSet(deletedSet);
        diffs.setUpdatedSet(updatedSet);
        diffs.setCreatedSet(createdSet);
        
        final CsvMember csvMember = new CsvMember();
        csvMember.setMembershipNumber(1000L);
        
        Mockito.when(memberService.getByMemberNumber(1000L)).thenReturn(currentMember);
        Mockito.when(memberService.update(currentMember, currentMember)).thenReturn(currentMember);
        final ImportDiff newDiffs = memberResource.importCreateUpdateMember(csvMember, diffs);
        Assert.assertEquals(0, newDiffs.getCreatedSet().size());
        Assert.assertEquals(1, newDiffs.getUpdatedSet().size());
        Assert.assertEquals(0, newDiffs.getErrorSet().size());
    }
    
    @Test
    public void testImportMemberDeleted()
    {
        final ImportDiff diffs = new ImportDiff();
        final Set<Long> errorSet = new HashSet<>();
        final Set<Long> existingSet = new HashSet<>();
        final Set<Long> importedSet = new HashSet<>();
        final Set<Long> deletedSet = new HashSet<>();
        existingSet.add(9000L);
        diffs.setImportedSet(importedSet);
        diffs.setExistingSet(existingSet);
        diffs.setErrorSet(errorSet);
        diffs.setDeletedSet(deletedSet);
        
        Mockito.when(memberService.findByMemberNumber(9000L)).thenReturn(lapsedMembers);
        Mockito.when(memberService.delete(lapsedMember)).thenReturn(true);
        final ImportDiff newDiffs = memberResource.importDeleteMember(diffs);
        Assert.assertEquals(1, newDiffs.getDeletedSet().size());
    }
    
    @Test
    public void testImportMemberNonexistent()
    {
        final ImportDiff diffs = new ImportDiff();
        final Set<Long> errorSet = new HashSet<>();
        final Set<Long> existingSet = new HashSet<>();
        final Set<Long> importedSet = new HashSet<>();
        final Set<Long> createdSet = new HashSet<>();
        final Set<Long> updatedSet = new HashSet<>();
        final Set<Long> deletedSet = new HashSet<>();
        
        diffs.setImportedSet(importedSet);
        diffs.setExistingSet(existingSet);
        diffs.setErrorSet(errorSet);
        diffs.setDeletedSet(deletedSet);
        diffs.setUpdatedSet(updatedSet);
        diffs.setCreatedSet(createdSet);
        
        final CsvMember csvMember = new CsvMember();
        csvMember.setMembershipNumber(1234L);
        final MemberDO newMember = new MemberDO();
        newMember.setMembershipNumber(1234L);
        
        Mockito.when(memberService.getByMemberNumber(1234L)).thenReturn(null);
        Mockito.when(memberService.create(newMember)).thenReturn(newMember);
        final ImportDiff newDiffs = memberResource.importCreateUpdateMember(csvMember, diffs);
        Assert.assertEquals(1, newDiffs.getCreatedSet().size());
        Assert.assertEquals(0, newDiffs.getErrorSet().size());
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
