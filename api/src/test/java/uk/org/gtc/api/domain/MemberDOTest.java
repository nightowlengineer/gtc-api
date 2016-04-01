package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for MemberDO.
 */
public class MemberDOTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public MemberDOTest(final String testName)
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
		
		final MemberDO memberFull = new MemberDO(MemberType.FULL, MemberStatus.CURRENT, 1234L, Salutation.MR, "John", "Smith",
				"test@example.com", phoneNumbers, addresses, "Tester", Year.parse("2015"), sponsorMembers, "Website", "Company");
		
		assertEquals(MemberType.FULL, memberFull.getType());
		assertEquals(MemberStatus.CURRENT, memberFull.getStatus());
		assertEquals(1234L, memberFull.getMembershipNumber().longValue());
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
		assertEquals("Company", memberFull.getCompany());
		
		assertSame(memberFull, memberFull);
		assertEquals(memberFull, memberFull);
	}
	
}
