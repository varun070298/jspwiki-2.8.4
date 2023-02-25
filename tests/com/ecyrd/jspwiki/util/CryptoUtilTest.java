package com.ecyrd.jspwiki.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.codec.binary.Base64;

public class CryptoUtilTest extends TestCase
{

    public static Test suite()
    {
        return new TestSuite( CryptoUtilTest.class );
    }

    public void testCommandLineHash() throws Exception
    {
        // Save old printstream
        PrintStream oldOut = System.out;

        // Swallow System out and get command output
        OutputStream out = new ByteArrayOutputStream();
        System.setOut( new PrintStream( out ) );
        CryptoUtil.main( new String[] { "--hash", "password" } );
        String output = new String( out.toString() );

        // Restore old printstream
        System.setOut( oldOut );

        // Run our tests
        assertTrue( output.startsWith( "{SSHA}" ) );
    }

    public void testCommandLineNoVerify() throws Exception
    {
        // Save old printstream
        PrintStream oldOut = System.out;

        // Swallow System out and get command output
        OutputStream out = new ByteArrayOutputStream();
        System.setOut( new PrintStream( out ) );
        // Supply a bogus password
        CryptoUtil.main( new String[] { "--verify", "password", "{SSHA}yfT8SRT/WoOuNuA6KbJeF10OznZmb28=" } );
        String output = new String( out.toString() );

        // Restore old printstream
        System.setOut( oldOut );

        // Run our tests
        assertTrue( output.startsWith( "false" ) );
    }
    
    public void testCommandLineSyntaxError1() throws Exception
    {
        // Try verifying password without the {SSHA} prefix
        try {
            CryptoUtil.main( new String[] { "--verify", "password", "yfT8SRT/WoOuNuA6KbJeF10OznZmb28=" } );
        }
        catch (IllegalArgumentException e)
        {
            // Excellent; we expected an error
        }
    }
    
    public void testCommandLineVerify() throws Exception
    {
        // Save old printstream
        PrintStream oldOut = System.out;

        // Swallow System out and get command output
        OutputStream out = new ByteArrayOutputStream();
        System.setOut( new PrintStream( out ) );
        CryptoUtil.main( new String[] { "--verify", "testing123", "{SSHA}yfT8SRT/WoOuNuA6KbJeF10OznZmb28=" } );
        String output = new String( out.toString() );

        // Restore old printstream
        System.setOut( oldOut );

        // Run our tests
        assertTrue( output.startsWith( "true" ) );
    }

    public void testExtractHash()
    {
        byte[] digest;

        digest = Base64.decodeBase64( "yfT8SRT/WoOuNuA6KbJeF10OznZmb28=".getBytes() );
        assertEquals( "foo", new String( CryptoUtil.extractSalt( digest ) ) );

        digest = Base64.decodeBase64( "tAVisOOQGAeVyP8UMFQY9qi83lxsb09e".getBytes() );
        assertEquals( "loO^", new String( CryptoUtil.extractSalt( digest ) ) );

        digest = Base64.decodeBase64( "BZaDYvB8czmNW3MjR2j7/mklODV0ZXN0eQ==".getBytes() );
        assertEquals( "testy", new String( CryptoUtil.extractSalt( digest ) ) );
    }

    public void testGetSaltedPassword() throws Exception
    {
        byte[] password;

        // Generate a hash with a known password and salt
        password = "testing123".getBytes();
        assertEquals( "{SSHA}yfT8SRT/WoOuNuA6KbJeF10OznZmb28=", CryptoUtil.getSaltedPassword( password, "foo".getBytes() ) );

        // Generate two hashes with a known password and 2 different salts
        password = "password".getBytes();
        assertEquals( "{SSHA}tAVisOOQGAeVyP8UMFQY9qi83lxsb09e", CryptoUtil.getSaltedPassword( password, "loO^".getBytes() ) );
        assertEquals( "{SSHA}BZaDYvB8czmNW3MjR2j7/mklODV0ZXN0eQ==", CryptoUtil.getSaltedPassword( password, "testy".getBytes() ) );
    }

    public void testMultipleHashes() throws Exception
    {
        String p1 = CryptoUtil.getSaltedPassword( "password".getBytes() );
        String p2 = CryptoUtil.getSaltedPassword( "password".getBytes() );
        String p3 = CryptoUtil.getSaltedPassword( "password".getBytes() );
        assertNotSame( p1, p2 );
        assertNotSame( p2, p3 );
        assertNotSame( p1, p3 );
    }

    public void testSaltedPasswordLength() throws Exception
    {
        // Generate a hash with a known password and salt
        byte[] password = "mySooperRandomPassword".getBytes();
        String hash = CryptoUtil.getSaltedPassword( password, "salt".getBytes() );

        // slappasswd says that a 4-byte salt should give us 6 chars for prefix
        // + 20 chars for the hash + 12 for salt (38 total)
        assertEquals( 38, hash.length() );
    }

    public void verifySaltedPassword() throws Exception
    {
        byte[] password;

        // Verify with a known digest
        password = "testing123".getBytes("UTF-8");
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}yfT8SRT/WoOuNuA6KbJeF10OznZmb28=" ) );

        // Verify with two more known digests
        password = "password".getBytes();
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}tAVisOOQGAeVyP8UMFQY9qi83lxsb09e" ) );
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}BZaDYvB8czmNW3MjR2j7/mklODV0ZXN0eQ==" ) );

        // Verify with three consecutive random generations (based on
        // slappasswd)
        password = "testPassword".getBytes();
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}t2tfJHm/QZYUh0OZ8tkm05l2LLbuc3ZF" ) );
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}0FKV9iM2cA5bAMws7mSgwg+zik/GT+wy" ) );
        assertTrue( CryptoUtil.verifySaltedPassword( password, "{SSHA}/0Dzvh+8+w0YO673Qr7vqEOmdeMSrbGG" ) );
    }
}
