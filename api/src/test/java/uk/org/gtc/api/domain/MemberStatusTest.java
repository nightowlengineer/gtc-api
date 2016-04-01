package uk.org.gtc.api.domain;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MemberStatusTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public MemberStatusTest(final String testName)
	{
		super(testName);
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(MemberStatusTest.class);
	}
	
	public void testValues() throws Exception
	{
		assertEquals("APPLIED", MemberStatus.APPLIED.toString());
		assertEquals("APPROVED", MemberStatus.APPROVED.toString());
		assertEquals("CURRENT", MemberStatus.CURRENT.toString());
		assertEquals("DECLINED", MemberStatus.DECLINED.toString());
		assertEquals("INVOICED", MemberStatus.INVOICED.toString());
		assertEquals("LAPSED", MemberStatus.LAPSED.toString());
		assertEquals("PAID", MemberStatus.PAID.toString());
		assertEquals("REMOVED", MemberStatus.REMOVED.toString());
		
		final MemberStatus[] memberStatuses = MemberStatus.values();
		assertEquals(8, memberStatuses.length);
		
		assertEquals(MemberStatus.APPLIED, MemberStatus.valueOf("APPLIED"));
		assertEquals(MemberStatus.APPROVED, MemberStatus.valueOf("APPROVED"));
		assertEquals(MemberStatus.CURRENT, MemberStatus.valueOf("CURRENT"));
		assertEquals(MemberStatus.DECLINED, MemberStatus.valueOf("DECLINED"));
		assertEquals(MemberStatus.INVOICED, MemberStatus.valueOf("INVOICED"));
		assertEquals(MemberStatus.LAPSED, MemberStatus.valueOf("LAPSED"));
		assertEquals(MemberStatus.PAID, MemberStatus.valueOf("PAID"));
		assertEquals(MemberStatus.REMOVED, MemberStatus.valueOf("REMOVED"));
		
	}
	
}
