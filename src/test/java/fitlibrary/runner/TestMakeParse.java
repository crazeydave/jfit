/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.runner;

import fit.Parse;
import fitlibrary.runner.CustomRunner;
import fitlibrary.runner.CustomRunnerException;
import fitlibrary.utility.ParseUtility;
import junit.framework.TestCase;

public class TestMakeParse extends TestCase {
    private static final String HEADER = "<html><head><title>test</title></head><body>\n";
    private static final String FOOTER = "</body></html>\n";
	private CustomRunner runner = new CustomRunner("test");
	
	public void testNoTableForRow() {
		try {
			runner.addRow("a");
			fail("No exception thrown");
		} catch (CustomRunnerException e) {
			//
		}
	}
	public void testNoTables() {
		try {
			runner.getTables();
			fail("No exception thrown");
		} catch (CustomRunnerException e) {
			//
		}
	}
	public void testOneRow() {
		runner.addTable("a");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>a</td></tr></table>\n");
	}
	public void testOneRowInItalics() {
		runner.addTable("<i>a</i>");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td><i>a</i></td></tr></table>\n");
	}
	public void testLeader() {
		runner.addTableWithLeaderText("a","leader<br>\n");
	    assertBody("leader<br>\n"+
	              "<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>a</td></tr></table>\n");
	}
	public void testTrailer() {
		runner.addTable("a");
		runner.addTableTrailer("trailer");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>a</td></tr></table>"+
	              "trailer\n");
	}
	public void testMiddleTrailerNotLost() {
		runner.addTable("a");
		runner.addTableTrailer("TRAILER");
		runner.addTable("b");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>a</td></tr></table>TRAILER\n"+
	              "<br><table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>b</td></tr></table>\n");
	}
	public void testTwoMiddleTrailers() {
		runner.addTable("a");
		runner.addTableTrailer("TRAILER");
		runner.addTableTrailer("222");
		runner.addTable("b");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>a</td></tr></table>TRAILER<br>222\n"+
	              "<br><table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>b</td></tr></table>\n");
	}
	public void testTwoRows() {
	    runner.addTable("a");
	    runner.addRow("b|c");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	               "<tr>\n"+
	               "<td ColSpan=2>a</td></tr>\n"+
	               "<tr>\n"+
	               "<td>b</td>\n"+
	               "<td>c</td></tr></table>\n");
	}
	public void testThreeRows() {
	    runner.addTable("z|a");
	    runner.addRow("b|c|d");
	    runner.addRow("b|c");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	               "<tr>\n"+
	               "<td>z</td>\n"+
	               "<td ColSpan=2>a</td></tr>\n"+
	               "<tr>\n"+
	               "<td>b</td>\n"+
	               "<td>c</td>\n"+
	               "<td>d</td></tr>\n"+
	               "<tr>\n"+
	               "<td>b</td>\n"+
	               "<td ColSpan=2>c</td></tr></table>\n");
	}
	public void testTwoTables() {
	    runner.addTable("a");
	    runner.addRow("b|c|d");
		runner.addTable("fit.Summary");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	               "<tr>\n"+
	               "<td ColSpan=3>a</td></tr>\n"+
	               "<tr>\n"+
	               "<td>b</td>\n"+
	               "<td>c</td>\n"+
	               "<td>d</td></tr></table>\n"+
	               "<br><table border cellspacing=0 cellpadding=3>\n"+
		           "<tr>\n"+
		           "<td>fit.Summary</td></tr></table>\n");
	}
	public void testParse() {
	    runner.addTable("a");
	    runner.addRow("b|c");
	    Parse segment = runner.getTables();
	    String body = "<table border cellspacing=0 cellpadding=3>\n"+
	                  "<tr>\n"+
	                  "<td ColSpan=2>a</td></tr>\n"+
	                  "<tr>\n"+
	                  "<td>b</td>\n"+
	                  "<td>c</td></tr></table>\n";
	    assertEquals(HEADER+body+FOOTER,ParseUtility.toString(segment));
	}
	public void testSplicingJustSegments() {
	    runner.addTables(makeSegments());
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
		        "<tr>\n"+
		        "<td>SEGMENT</td></tr></table>\n"+
		        "<br><table border cellspacing=0 cellpadding=3>\n"+
		        "<tr>\n"+
		        "<td>END</td></tr></table>\n");
	}
	public void testTrailerOnSplicedTable() {
	    runner.addTables(makeSegments());
	    runner.addTableTrailer("TRAILER");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
		        "<tr>\n"+
		        "<td>SEGMENT</td></tr></table>\n"+
		        "<br><table border cellspacing=0 cellpadding=3>\n"+
		        "<tr>\n"+
		        "<td>END</td></tr></table>\n"+
		        "<br>TRAILER");
	}
	public void testSplicingOfSegments() {
	    // Such splicing is used in FolderRunner, with SetUp and TearDown
	    runner.addTable("AAAA");
	    runner.addTables(makeSegments());
	    runner.addTable("BBBB");
	    assertBody("<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>AAAA</td></tr></table>\n"+
	              "<table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>SEGMENT</td></tr></table>\n"+
	              "<br><table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>END</td></tr></table>\n\n"+
	              "<br><table border cellspacing=0 cellpadding=3>\n"+
	              "<tr>\n"+
	              "<td>BBBB</td></tr></table>\n");
	}
	public void testSplice() {
	    final String spliceBody =
	        "<html><head><title>segment</title></head><body>\n"+
	        "<table border cellspacing=0 cellpadding=3>\n"+
	        "<tr>\n"+
	        "<td>SEGMENT</td></tr></table>\n"+
	        "<br><table border cellspacing=0 cellpadding=3>\n"+
	        "<tr>\n"+
	        "<td>END</td></tr></table>\n"+FOOTER;
	    assertEquals(spliceBody,makeSegments());
	}
	private Parse makeSegments() {
        CustomRunner segmentRunner = new CustomRunner("segment");
	    segmentRunner.addTable("SEGMENT");
	    segmentRunner.addTable("END");
	    Parse tables = segmentRunner.getTables();
        return tables;
    }
    private void assertBody(String body) {
        assertEquals(HEADER+body+FOOTER,runner.toString());
    }
    private void assertEquals(String expected, Parse table) {
        assertEquals(expected,ParseUtility.toString(table));
    }
}
