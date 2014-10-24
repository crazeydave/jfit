/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.runner;

import java.text.ParseException;


import fit.Parse;
import fitlibrary.runner.HtmlRunner;
import fitlibrary.utility.ParseUtility;
import junit.framework.TestCase;

public class TestHtmlRunner extends TestCase {
    private static final String html = "<html><title>table</title><body>"+
        "t1<table><tr><td>Test</td></tr></table>t2"+
    	"t3<table><tr><td>Test</td></tr></table>t4"+
        "</body></html>";
    private static String setUpHtml = "<html><title>setup</title><body>"+
    	"s1<table><tr><td>SetUp</td></tr></table>s2"+
    	"s3<table><tr><td>SetUp</td></tr></table>s4"+
    	"</body></html>";
    private static String tearDownHtml = "<html><title>teardown</title><body>"+
		"front<table><tr><td>TearDown</td></tr></table>back"+
    	"T3<table><tr><td>TearDown</td></tr></table>T4"+
		"</body></html>";
    private Parse tables, setUp, tearDown;
    
    @Override
	public void setUp() throws ParseException {
        tables = new Parse(html);
        setUp = new Parse(setUpHtml);
        tearDown = new Parse(tearDownHtml);
    }
    public void testNone() {
        check(null, null, html);
    }
    public void testSetUp() { 
        String expected = "<html><title>table</title><body>"+
    		"s1<table><tr><td>SetUp</td></tr></table>s2"+
        	"s3<table><tr><td>SetUp</td></tr></table>s4"+
        	"<br>t1<table><tr><td>Test</td></tr></table>t2"+
        	"t3<table><tr><td>Test</td></tr></table>t4"+
        	"</body></html>";
        check(setUp, null, expected);
    }
    public void testTearDown() { 
        String expected = "<html><title>table</title><body>"+
        	"t1<table><tr><td>Test</td></tr></table>t2"+
        	"t3<table><tr><td>Test</td></tr></table>t4"+
    		"<br>front<table><tr><td>TearDown</td></tr></table>back"+
        	"T3<table><tr><td>TearDown</td></tr></table>T4"+
        	"</body></html>";
        check(null, tearDown, expected);
    }
    public void testSetUpAndTearDown() { 
        String expected = "<html><title>table</title><body>"+
    		"s1<table><tr><td>SetUp</td></tr></table>s2"+
        	"s3<table><tr><td>SetUp</td></tr></table>s4"+
        	"<br>t1<table><tr><td>Test</td></tr></table>t2"+
        	"t3<table><tr><td>Test</td></tr></table>t4"+
    		"<br>front<table><tr><td>TearDown</td></tr></table>back"+
        	"T3<table><tr><td>TearDown</td></tr></table>T4"+
        	"</body></html>";
        check(setUp, tearDown, expected);
    }
    private void check(Parse setUp2, Parse tearDown2, String expected) {
        Parse result = HtmlRunner.integrateSetUpAndTearDown(tables, setUp2, tearDown2);
        assertEquals(expected,result);
    }
    private void assertEquals(String expected, Parse tables2) {
        assertEquals(expected, ParseUtility.toString(tables2));
    }
}
