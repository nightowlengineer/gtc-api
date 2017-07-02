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
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(AddressTest.class);
    }

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AddressTest(final String testName)
    {
        super(testName);
    }

    public void testBlankConstructor() throws Exception
    {
        final Address address = new Address();
        assertNotSame(new Address(), address);
    }

    public void testConstructor() throws Exception
    {
        final List<String> addressLines = new ArrayList<>();
        addressLines.add("123 Some Road");
        addressLines.add("Another town");

        final Address address = new Address(addressLines, "United Kingdom", "AB12 3CD", LocationType.HOME);

        assertEquals(addressLines, address.getLines());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("AB12 3CD", address.getPostcode());
        assertEquals(LocationType.HOME, address.getAddressType());
    }

}
