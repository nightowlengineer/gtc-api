package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MemberDOTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public MemberDOTest(String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(MemberDOTest.class);
	}
	
	public void testBlankConstructor() throws Exception
	{
		final MemberDO member = new MemberDO();
		assertEquals(new MemberDO(), member);
	}
	
	public void testConstructor() throws Exception
	{
		final List<Address> addresses = new ArrayList<Address>();
		addresses.add(new Address());
		addresses.add(new Address());
		
		final List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		phoneNumbers.add(new PhoneNumber(LocationType.HOME, "01234 567890"));
		
		final List<Long> sponsorMembers = new ArrayList<Long>();
		sponsorMembers.add(1234L);
		
		final ApplicationDO application = new ApplicationDO(MemberType.FULL, MemberStatus.CURRENT, Salutation.MR, "John", "Smith",
				"test@example.com", phoneNumbers, addresses, "Tester", Year.parse("2015"), sponsorMembers, "Website");
				
		final MemberDO memberFull = new MemberDO(1234L, MemberType.FULL, MemberStatus.CURRENT, Salutation.MR, "John", "Smith",
				"test@example.com", phoneNumbers, addresses, "Tester", Year.parse("2015"), sponsorMembers, "Website");
				
		final MemberDO memberApplication = new MemberDO(1234L, application);
		
		assertEquals(MemberType.FULL, memberApplication.getType());
		assertEquals(MemberStatus.CURRENT, memberApplication.getStatus());
		assertEquals(Salutation.MR, memberApplication.getSalutation());
		assertEquals("John", memberApplication.getFirstName());
		assertEquals("Smith", memberApplication.getLastName());
		assertEquals("test@example.com", memberApplication.getEmail());
		assertEquals(phoneNumbers, memberApplication.getPhoneNumbers());
		assertEquals(addresses, memberApplication.getAddresses());
		assertEquals("Tester", memberApplication.getCurrentPost());
		assertEquals(Year.parse("2015"), memberApplication.getCareerStartDate());
		assertEquals(sponsorMembers, memberApplication.getSponsorMembers());
		assertEquals("Website", memberApplication.getReferralSource());
		assertEquals(1234L, memberApplication.getMembershipNumber().longValue());
		
		assertEquals(MemberType.FULL, memberFull.getType());
		assertEquals(MemberStatus.CURRENT, memberFull.getStatus());
		assertEquals(Salutation.MR, memberFull.getSalutation());
		assertEquals("John", memberFull.getFirstName());
		assertEquals("Smith", memberFull.getLastName());
		assertEquals("test@example.com", memberFull.getEmail());
		assertEquals(phoneNumbers, memberFull.getPhoneNumbers());
		assertEquals(addresses, memberFull.getAddresses());
		assertEquals("Tester", memberFull.getCurrentPost());
		assertEquals(Year.parse("2015"), memberFull.getCareerStartDate());
		assertEquals(sponsorMembers, memberFull.getSponsorMembers());
		assertEquals("Website", memberFull.getReferralSource());
		assertEquals(1234L, memberFull.getMembershipNumber().longValue());
		
		assertEquals(memberFull, memberApplication);
		assertSame(memberFull, memberFull);
		assertEquals(memberFull, memberFull);
	}
	
}