package uk.org.gtc.api.domain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SalutationTest extends TestCase
{
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public SalutationTest(final String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(SalutationTest.class);
	}
	
	public void testValues() throws Exception
	{
		assertEquals("DR", Salutation.DR.toString());
		assertEquals("MISS", Salutation.MISS.toString());
		assertEquals("MR", Salutation.MR.toString());
		assertEquals("MRS", Salutation.MRS.toString());
		assertEquals("MS", Salutation.MS.toString());
		assertEquals("SIR", Salutation.SIR.toString());
		assertEquals("MX", Salutation.MX.toString());
		
		final Salutation[] salutations = Salutation.values();
		assertEquals(7, salutations.length);
		assertEquals(Salutation.DR, Salutation.valueOf("DR"));
		assertEquals(Salutation.MISS, Salutation.valueOf("MISS"));
		assertEquals(Salutation.MR, Salutation.valueOf("MR"));
		assertEquals(Salutation.MRS, Salutation.valueOf("MRS"));
		assertEquals(Salutation.MS, Salutation.valueOf("MS"));
		assertEquals(Salutation.SIR, Salutation.valueOf("SIR"));
		assertEquals(Salutation.MX, Salutation.valueOf("MX"));
	}
	
}
