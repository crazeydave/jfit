/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.utility;

import java.text.ParseException;

import fit.Parse;
import fitlibrary.utility.ParseUtility;
import junit.framework.TestCase;

public class TestParseUtility extends TestCase {
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
    public void testAppend() {
        String expected = "<html><title>setup</title><body>"+
    		"s1<table><tr><td>SetUp</td></tr></table>s2"+
    		"s3<table><tr><td>SetUp</td></tr></table>s4"+
    		"<br>front<table><tr><td>TearDown</td></tr></table>back"+
        	"T3<table><tr><td>TearDown</td></tr></table>T4"+
    		"</body></html>";
        ParseUtility.append(setUp,tearDown);
        assertEquals(expected,setUp);
    }
    public void testAppendNull() {
        ParseUtility.append(setUp,null);
        assertEquals(setUpHtml,setUp);
    }
    public void testAppendSetUp() {
        String expected = "<html><title>table</title><body>"+
    		"s1<table><tr><td>SetUp</td></tr></table>s2"+
    		"s3<table><tr><td>SetUp</td></tr></table>"+
    	   	"s4<br>t1<table><tr><td>Test</td></tr></table>t2"+
    	   	"t3<table><tr><td>Test</td></tr></table>t4"+
    		"</body></html>";
        ParseUtility.appendToSetUp(setUp,tables);
        assertEquals(expected,setUp);
    }
    public void testAppendSetUpWithNull() {
        ParseUtility.appendToSetUp(setUp,null);
        assertEquals(setUpHtml,setUp);
    }
    public void testAppendAll() {
        String expected = "<html><title>table</title><body>"+
    		"s1<table><tr><td>SetUp</td></tr></table>s2"+
    		"s3<table><tr><td>SetUp</td></tr></table>"+
    	   	"s4<br>t1<table><tr><td>Test</td></tr></table>t2"+
    	   	"t3<table><tr><td>Test</td></tr></table>"+
        	"t4<br>front<table><tr><td>TearDown</td></tr></table>back"+
        	"T3<table><tr><td>TearDown</td></tr></table>T4"+
    		"</body></html>";
        ParseUtility.append(tables,tearDown);
        ParseUtility.appendToSetUp(setUp,tables);
        assertEquals(expected,setUp);
    }
    private void assertEquals(String expected, Parse tables2) {
        assertEquals(expected, ParseUtility.toString(tables2));
    }
    public void testFixHeader() {
        String result = ParseUtility.removeHeader(tables);
        assertEquals("<html><title>table</title><body>", result);
        assertEquals("t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>", tables);
    }
    public void testInitialTable() {
        assertEquals("<html><title>table</title><body>"+
                "t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>",tables);
    }
    public void testChangeHeader() {
        ParseUtility.changeHeader(tables,"<html><title>new</title><body><hr>");
        assertEquals("<html><title>new</title><body><hr>"+
                "t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>",tables);
    }
    public void testCompleteTrailerThatIsComplete() {
        ParseUtility.completeTrailer(tables);
        assertEquals("<html><title>table</title><body>"+
                "t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>",tables);
    }
    public void testCompleteTrailerThatIsInComplete() {
        tables.last().trailer = "JUNK";
        ParseUtility.completeTrailer(tables);
        assertEquals("<html><title>table</title><body>"+
                "t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>JUNK"+
                "\n</body></html>\n",tables);
    }
    public void testFixTrailersNone() throws ParseException {
        String setUpHtml2 = "<html><title>setup</title><body>"+
    		"<table><tr><td>SetUp</td></tr></table>"+
    		"</body></html>";
        Parse setUp2 = new Parse(setUpHtml2);
        ParseUtility.fixTrailers(setUp2,tables);
        assertEquals("<html><title>setup</title><body>"+
        		"<table><tr><td>SetUp</td></tr></table>",
        		setUp2);
        assertEquals("<html><title>table</title><body>"+
                "t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>",
                tables);
    }
    public void testFixTrailers() {
        ParseUtility.removeHeader(tables);
        ParseUtility.fixTrailers(setUp,tables);
        assertEquals("<html><title>setup</title><body>"+
                "s1<table><tr><td>SetUp</td></tr></table>s2"+
            	"s3<table><tr><td>SetUp</td></tr></table>",
                setUp);
        assertEquals(
                "s4<br>t1<table><tr><td>Test</td></tr></table>t2"+
               	"t3<table><tr><td>Test</td></tr></table>t4"+
                "</body></html>",
                tables);
    }    
    public void testOneTableWithOneActionTranslated() {
    	String initial = "<html>\n"+
    		"<br/>* <i>One</i><br/>\nt2\n"+
    		"</html>";
    	String expected = "<html>\n"+
			"\n<table><tr><td><i>One</i></td></tr></table>\n\nt2\n"+
			"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
    public void testTwoTablesWithOneActionTranslated() {
    	String initial = "<html>\n"+
    		"<br/>* <i>One</i><br/>\n"+
    		"<br/>* <i>Two</i><br/>\n"+
    		"</html>";
    	String expected = "<html>\n"+
    		"\n<table><tr><td><i>One</i></td></tr></table>\n\n"+
    		"\n<table><tr><td><i>Two</i></td></tr></table>\n\n"+
    		"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
    public void testOneTableWithOneActionWithSeveralKeywords() {
    	String initial = "<html>\n"+
    		"<br/>* <i>one</i>two<i>three</i><br/>\n"+
    		"</html>";
    	String expected = "<html>\n"+
			"\n<table><tr><td><i>one</i></td><td>two</td><td><i>three</i></td></tr></table>\n\n"+
			"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
    public void testWithNL() {
    	String initial = "<html>\n"+
    		"\n* <i>one</i>two<i>three</i>\n"+
    		"</html>";
    	String expected = "<html>\n"+
			"\n<table><tr><td><i>one</i></td><td>two</td><td><i>three</i></td></tr></table>\n"+
			"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
    public void testOneTableWithOneActionWithSeveralKeywordsBold() {
    	String initial = "<html>\n"+
    		"<br/>* <i>one</i>two<b>is</b>1<br/>\n"+
    		"</html>";
    	String expected = "<html>\n"+
			"\n<table><tr><td><i>one</i></td><td>two</td><td><b>is</b></td><td>1</td></tr></table>\n\n"+
			"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
    public void testOneTableWithBold() {
    	String initial = "<html>\n"+
    		"<br/>* <b>one</b> <i>two</i> 1<br/>\n"+
    		"</html>";
    	String expected = "<html>\n"+
			"\n<table><tr><td><b>one</b></td><td><i>two</i></td><td>1</td></tr></table>\n\n"+
			"</html>";
    	assertEquals(expected,ParseUtility.translate(initial));
    }
}
