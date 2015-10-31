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
public class ApplicationDOTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ApplicationDOTest(String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(ApplicationDOTest.class);
	}
	
	public void testBlankConstructor() throws Exception
	{
		final ApplicationDO application = new ApplicationDO();
		assertEquals(new ApplicationDO(), application);
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
				
		assertEquals(MemberType.FULL, application.getType());
		assertEquals(MemberStatus.CURRENT, application.getStatus());
		assertEquals(Salutation.MR, application.getSalutation());
		assertEquals("John", application.getFirstName());
		assertEquals("Smith", application.getLastName());
		assertEquals("test@example.com", application.getEmail());
		assertEquals(phoneNumbers, application.getPhoneNumbers());
		assertEquals(addresses, application.getAddresses());
		assertEquals("Tester", application.getCurrentPost());
		assertEquals(Year.parse("2015"), application.getCareerStartDate());
		assertEquals(sponsorMembers, application.getSponsorMembers());
		assertEquals("Website", application.getReferralSource());
	}
	
}
