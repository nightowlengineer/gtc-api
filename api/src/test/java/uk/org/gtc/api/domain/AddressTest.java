package uk.org.gtc.api.domain;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AddressTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AddressTest(String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(AddressTest.class);
	}
	
	public void testBlankConstructor() throws Exception
	{
		final Address address = new Address();
		assertEquals(new Address(), address);
	}
	
	public void testConstructor() throws Exception
	{
		final List<String> addressLines = new ArrayList<String>();
		addressLines.add("123 Some Road");
		addressLines.add("Another town");
		
		final Address address = new Address(addressLines, "United Kingdom", "AB12 3CD", LocationType.HOME);
		
		assertEquals(addressLines, address.getLines());
		assertEquals("United Kingdom", address.getCountry());
		assertEquals("AB12 3CD", address.getPostcode());
		assertEquals(LocationType.HOME, address.getAddressType());
	}
	
}
