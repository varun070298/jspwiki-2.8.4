
package com.ecyrd.jspwiki.util;


import junit.framework.*;

public class AllTests extends TestCase
{
    public AllTests( String s )
    {
        super( s );
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Utility suite tests");

        suite.addTest( ClassUtilTest.suite() );
        suite.addTest( CommentedPropertiesTest.suite() );
        suite.addTest( CryptoUtilTest.suite() );
        suite.addTest( com.ecyrd.jspwiki.util.comparators.AllTests.suite() );
        suite.addTest( MailUtilTest.suite() );
        suite.addTest( PageSorterTest.suite() );
        suite.addTest( PriorityListTest.suite() );
        suite.addTest( SerializerTest.suite() );
        suite.addTest( TextUtilTest.suite() );
        suite.addTest( TimedCounterListTest.suite() );
        suite.addTest( UtilJ2eeCompatTest.suite() );
        
        return suite;
    }
}
