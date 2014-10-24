/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TestSimpleWikiTranslator {
	protected List<File> files = new ArrayList<File>();
	protected Map<File,List<String>> fileContents = new HashMap<File, List<String>>();
	protected SimpleWikiTranslator simpleWikiTranslator = new SimpleWikiTranslator(new FileAccess( ) {
		@Override
		public Iterator<File> filesWithSuffix(String suffix) {
			return files.iterator();
		}
		@Override
		public NullIterator<String> linesOf(File file) {
			return new NullIterator<String>(fileContents.get(file).iterator());
		}
	});
	Mockery context = new Mockery();
	protected HtmlReceiver receiver = context.mock(HtmlReceiver.class);
	
	@Test
	public void onlyText() {
		createFile("/a/b", "a", "b");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/>a<br/>\nb<br/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void commentedOutText() {
		createFile("/a/b", "#a", "b");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/>b<br/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void oneRowInOneTable() {
		createFile("/a/b", "|a|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void oneRowInOneTableWithText() {
		createFile("/a/b", "b", "|a|", "c");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/>b<br/>\n<table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/>c<br/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void twoRowsInOneTable() {
		createFile("/a/b", "|a|", "|b|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n<tr><td>b</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void twoRowsSecondBlankInOneTable() {
		createFile("/a/b", "|a|", "| |");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n<tr><td></td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@SuppressWarnings("rawtypes")
	@Test
	public void splitWorks() {
		assertThat(SimpleWikiTranslator.split(""),is((List)new ArrayList<String>()));
		assertThat(SimpleWikiTranslator.split("||"),is(CollectionUtility.list("")));
		assertThat(SimpleWikiTranslator.split("| |"),is(CollectionUtility.list("")));
		assertThat(SimpleWikiTranslator.split("|aa|b |"),is(CollectionUtility.list("aa","b")));
	}
	@Test
	public void twoOneRowTables() {
		createFile("/a/b", "|a|", "b", "|c|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/>b<br/>\n<table border=\"1\" cellspacing=\"0\">\n<tr><td>c</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void definedAction() {
		createFile("/a/b", "|a|", "", "|b|", "----");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/><br/>\n<table border=\"1\" cellspacing=\"0\">\n<tr><td>b</td></tr>\n</table>\n<br/><hr/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void italicQuotesAreRemoved() {
		createFile("/a/b", "|''a''|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void boldQuotesAreRemoved() {
		createFile("/a/b", "|'''a'''|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void singleQuotesAreNotRemoved() {
		createFile("/a/b", "|'a'|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>'a'</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void wikiEscapesAreRemoved() {
		createFile("/a/b", "|!-a-!|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void wikiEscapesAreRemovedButBarRemains() {
		createFile("/a/b", "|!-a|b-!|");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a|b</td></tr>\n</table>\n<br/></html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void contentsTableIsRemoved() {
		createFile("/a/b", "|!contents|", "", "|b|", "----");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),"<html>\n<br/><br/>\n<br/>\n<table border=\"1\" cellspacing=\"0\">\n<tr><td>b</td></tr>\n</table>\n<br/><hr/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}
	@Test
	public void plainTextIsConverted() {
		createFile("/a/b", "- some action call");
		context.checking(new Expectations() {{
			oneOf(receiver).take(new File("/a/b"),
					"<html>\n<br/>\n<table border=\"1\" cellspacing=\"0\"><tr><td><i>run plain</i></td><td>some action call</td></tr>\n</table>\n<br/>\n</html>");
		}});
		simpleWikiTranslator.translate(receiver);
	}

	@Test
	public void stringToString() {
		assertThat(SimpleWikiTranslator.translate("|a|"),
				is("<html>\n<br/><table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/></html>"));
	}
	@Test
	public void secondDefinedActionBodyIsEmpty() {
		assertThat(SimpleWikiTranslator.translate("|a|\n\n|comment|\n----\n|b|\n"),
				is("<html>\n<br/>"+
				   "<table border=\"1\" cellspacing=\"0\">\n<tr><td>a</td></tr>\n</table>\n<br/><br/>\n"+
				   "<table border=\"1\" cellspacing=\"0\">\n<tr><td>comment</td></tr>\n</table>\n<br/><hr/>\n"+
				   "<table border=\"1\" cellspacing=\"0\">\n<tr><td>b</td></tr>\n</table>\n<br/></html>"));
	}

	private void createFile(String fileName, String... wiki) {
		File file = new File(fileName);
		files.add(file);
		fileContents.put(file, Arrays.asList(wiki));
	}
	
}
