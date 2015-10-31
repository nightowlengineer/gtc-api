package uk.org.gtc.api.domain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MemberTypeTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public MemberTypeTest(String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(MemberTypeTest.class);
	}
	
	public void testValues() throws Exception
	{
		assertEquals("ACADEMIC", MemberType.ACADEMIC.toString());
		assertEquals("AFFILIATE", MemberType.AFFILIATE.toString());
		assertEquals("ASSOCIATE", MemberType.ASSOCIATE.toString());
		assertEquals("FELLOW", MemberType.FELLOW.toString());
		assertEquals("FULL", MemberType.FULL.toString());
		assertEquals("HONORARY", MemberType.HONORARY.toString());
		assertEquals("RETIRED", MemberType.RETIRED.toString());
		assertEquals("SPONSOR", MemberType.SPONSOR.toString());
		assertEquals("STUDENT", MemberType.STUDENT.toString());
		
		MemberType[] memberTypes = MemberType.values();
		assertEquals(9, memberTypes.length);
		
		assertEquals(MemberType.ACADEMIC, MemberType.valueOf("ACADEMIC"));
		assertEquals(MemberType.AFFILIATE, MemberType.valueOf("AFFILIATE"));
		assertEquals(MemberType.ASSOCIATE, MemberType.valueOf("ASSOCIATE"));
		assertEquals(MemberType.FELLOW, MemberType.valueOf("FELLOW"));
		assertEquals(MemberType.FULL, MemberType.valueOf("FULL"));
		assertEquals(MemberType.HONORARY, MemberType.valueOf("HONORARY"));
		assertEquals(MemberType.RETIRED, MemberType.valueOf("RETIRED"));
		assertEquals(MemberType.SPONSOR, MemberType.valueOf("SPONSOR"));
		assertEquals(MemberType.STUDENT, MemberType.valueOf("STUDENT"));
	}
	
}
