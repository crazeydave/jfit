/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.table;

import static fitlibrary.matcher.TableBuilderForTests.table;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.runResults.TestResults;
import fitlibrary.utility.StringTablesPair;

@RunWith(JMock.class)
public class TestCellOnList {
	Mockery context = new Mockery();
	VariableResolver resolver = context.mock(VariableResolver.class);
	TestResults testResults = context.mock(TestResults.class);
	final Table table0 = table().mock(context, "", 0);
	final Table table1 = table().mock(context, "", 1);
	final Cell cellA = new CellOnList("AbC");
	final Cell cellXml = new CellOnList("Ab<x/><y><z/></y>C");
	final Cell cellVar = new CellOnList("@{a}");
	
	@Before
	public void useListsFactory() {
		TableFactory.useOnLists(true);
	}
	@After
	public void stopUsingListsFactory() {
		TableFactory.pop();
	}
	@Test public void text() {
		assertThat(cellA.text(),is("AbC"));
	}
	@Test public void textWithResolver() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbCD")));
		}});
		assertThat(cellA.text(resolver),is("AbCD"));
	}
	@Test public void textLower() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbCD")));
		}});
		assertThat(cellA.textLower(resolver),is("abcd"));
	}
	@Test public void fullText() {
		assertThat(cellXml.fullText(),is("Ab<x/><y><z/></y>C"));
		assertThat(cellXml.text(),is("AbC"));
	}
	@Test public void setText() {
		cellA.setText("xy<a/>z");
		assertThat(cellA.fullText(),is("xy<a/>z"));
		assertThat(cellA.text(),is("xyz"));
	}
	@Test public void setUnvisitedEscapedText() {
		cellA.setUnvisitedEscapedText("xyz");
		assertThat(cellA.fullText(),is(" <span class=\"fit_grey\">xyz</span>"));
		assertThat(cellA.text(),is("xyz"));
	}
	@Test public void isBlank() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("")));
		}});
		assertThat(cellA.isBlank(resolver),is(true));
	}
	@Test public void isNotBlank() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
		}});
		assertThat(cellA.isBlank(resolver),is(false));
	}
	@Test public void matchesTextInLowerCase() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("ABC")));
		}});
		assertThat(cellA.matchesTextInLowerCase("AbC",resolver),is(true));
	}
	@Test public void matchesNoTextInLowerCase() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
		}});
		assertThat(cellA.matchesTextInLowerCase("AC",resolver),is(false));
	}
	@Test public void hasNoEmbeddedTables() {
		context.checking(new Expectations() {{
			oneOf(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
		}});
		assertThat(cellA.hasEmbeddedTables(resolver),is(false));
	}
	@Test public void hasEmbeddedTables() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
		}});
		cellA.add(table0);
		assertThat(cellA.hasEmbeddedTables(resolver),is(true));
	}
	@Test public void hasEmbeddedTablesAfterResolving() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC",TableFactory.tables(table0))));
		}});
		assertThat(cellA.hasEmbeddedTables(resolver),is(true));
	}
	@Test public void getNoEmbeddedTables() {
		assertThat(cellA.getEmbeddedTables().size(),is(0));
	}
	@Test public void getEmbeddedTables() {
		cellA.add(table0);
		Tables embeddedTables = cellA.getEmbeddedTables();
		assertThat(embeddedTables.size(),is(1));
		assertThat(embeddedTables.at(0),is(table0));
	}
	@Test public void setInnerTables() {
		cellA.setInnerTables(TableFactory.tables(table0));
		Tables embeddedTables = cellA.getEmbeddedTables();
		assertThat(embeddedTables.size(),is(1));
		assertThat(embeddedTables.at(0),is(table0));
	}
	@Test public void pass() {
		context.checking(new Expectations() {{
			oneOf(testResults).pass();
		}});
		cellA.pass(testResults);
		assertThat(cellA.didPass(),is(true));
	}
	@Test public void passOrFailPasses() {
		context.checking(new Expectations() {{
			oneOf(testResults).pass();
		}});
		cellA.passOrFail(testResults,true);
		assertThat(cellA.didPass(),is(true));
		assertThat(cellA.didFail(),is(false));
	}
	@Test public void passOrFailFails() {
		context.checking(new Expectations() {{
			oneOf(testResults).fail();
		}});
		cellA.passOrFail(testResults,false);
		assertThat(cellA.didPass(),is(false));
		assertThat(cellA.didFail(),is(true));
	}
	@Test public void passWitMsg() {
		context.checking(new Expectations() {{
			oneOf(testResults).pass();
		}});
		cellA.pass(testResults,"msg");
		assertThat(cellA.didPass(),is(true));
		assertThat(cellA.fullText(),is("AbC<hr>msg <span class=\"fit_label\">actual</span>"));
	}
	@Test public void passIfNotEmbedded() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
			oneOf(testResults).pass();
		}});
		cellA.passIfNotEmbedded(testResults,resolver);
		assertThat(cellA.didPass(),is(true));
	}
	@Test public void passIfNotEmbeddedFails() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
		}});
		cellA.add(table0);
		cellA.passIfNotEmbedded(testResults,resolver);
		assertThat(cellA.didPass(),is(false));
		assertThat(cellA.didFail(),is(false));
	}
	@Test public void passIfBlank() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("")));
			oneOf(testResults).pass();
		}});
		cellA.passOrFailIfBlank(testResults,resolver);
		assertThat(cellA.didPass(),is(true));
		assertThat(cellA.didFail(),is(false));
	}
	@Test public void passOrFailIfBlankFails() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
			oneOf(testResults).fail();
		}});
		cellA.passOrFailIfBlank(testResults,resolver);
		assertThat(cellA.didPass(),is(false));
		assertThat(cellA.didFail(),is(true));
	}
	@Test public void fail() {
		context.checking(new Expectations() {{
			oneOf(testResults).fail();
		}});
		cellA.fail(testResults);
		assertThat(cellA.didFail(),is(true));
	}
	@Test public void failWithMsg() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
			oneOf(testResults).fail();
		}});
		cellA.fail(testResults,"msg",resolver);
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">expected</span><hr>msg <span class=\"fit_label\">actual</span>"));
	}
	// Need to check that diffing works correctly with this.
	@Test public void failWithStringEquals() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue(new StringTablesPair("AbC")));
			oneOf(testResults).fail();
		}});
		cellA.failWithStringEquals(testResults,"msg",resolver);
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">expected</span><hr>msg <span class=\"fit_label\">actual</span>"));
	}
	@Test public void failHtml() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.failHtml(testResults,"<i>msg</i>");
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC<i>msg</i>"));
	}
	@Test public void wrongHtml() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.wrongHtml(testResults,"<i>msg</i>");
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">expected</span><hr><i>msg</i> <span class=\"fit_label\">actual</span>"));
	}
	@Test public void expectedElementMissing() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.expectedElementMissing(testResults);
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">missing</span>"));
	}
	@Test public void actualElementMissing() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.actualElementMissing(testResults);
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">surplus</span>"));
	}
	@Test public void actualElementMissingWithMsg() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.actualElementMissing(testResults,"msg");
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is(" <span class=\"fit_grey\">msg</span> <span class=\"fit_label\">surplus</span>"));
	}
	@Test public void unexpected() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).fail();
		}});
		cellA.unexpected(testResults,"msg");
		assertThat(cellA.didFail(),is(true));
		assertThat(cellA.fullText(),is("AbC <span class=\"fit_label\">unexpected msg</span>"));
	}
	@Test public void exceptionExpectedNot() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).exception();
		}});
		cellA.exceptionExpected(false,new FitLibraryException("AA"),testResults);
		assertThat(cellA.hadError(),is(true));
		assertThat(cellA.fullText(),is("AbC<hr/> <span class=\"fit_label\">AA</span>"));
	}
	@Test public void exceptionExpected() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).pass();
		}});
		cellA.exceptionExpected(true,new FitLibraryException("AA"),testResults);
		assertThat(cellA.didPass(),is(true));
		assertThat(cellA.fullText(),is("AbC"));
	}
	@Test public void error() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).exception();
		}});
		cellA.error(testResults);
		assertThat(cellA.hadError(),is(true));
		assertThat(cellA.fullText(),is("AbC"));
	}
	@Test public void errorWithMessage() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).exception();
		}});
		cellA.error(testResults,"msg");
		assertThat(cellA.hadError(),is(true));
		assertThat(cellA.fullText(),is("AbC<hr/> <span class=\"fit_label\">msg</span>"));
	}
	@Test public void ignore() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("AbC"); will(returnValue("AbC"));
			oneOf(testResults).ignore();
		}});
		cellA.ignore(testResults);
		assertThat(cellA.wasIgnored(),is(true));
		assertThat(cellA.fullText(),is("AbC"));
	}
	@Test public void unresolved() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("@{a}"); will(returnValue(new StringTablesPair("@{a}")));
		}});
		assertThat(cellVar.unresolved(resolver),is(true));
	}
	@Test public void resolved() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("@{a}"); will(returnValue(new StringTablesPair("A")));
		}});
		assertThat(cellVar.unresolved(resolver),is(false));
	}
	@Test
	public void deepCopy() {
		final Table table0copy = table().mock(context, "", 55);
		final Table table1copy = table().mock(context, "", 66);
		context.checking(new Expectations() {{
			oneOf(table0).deepCopy(); will(returnValue(table0copy));
			oneOf(table1).deepCopy(); will(returnValue(table1copy));
		}});
		cellA.setLeader("LL");
		cellA.setTrailer("TT");
		cellA.add(table0);
		cellA.add(table1);
		Cell deepCopy = (Cell) cellA.deepCopy();
		assertThat(deepCopy.size(), is(2));
		assertThat(deepCopy.at(0), is(table0copy));
		assertThat(deepCopy.at(1), is(table1copy));
		assertThat(deepCopy.fullText(), is(cellA.fullText()));
		assertThat(deepCopy.getLeader(), is("LL"));
		assertThat(deepCopy.getTrailer(), is("TT"));
	}
// Should annotations be copied as well???
	
	
	@Test public void elementAddedToEmptyCell() {
		cellA.add(table0);
		assertThat(cellA.size(),is(1));
		assertThat(cellA.at(0),is(table0));
	}
	@Test public void elementAddedToCellWithEmbedded() {
		cellA.add(table0);
		cellA.add(table1);
		assertThat(cellA.size(),is(2));
		assertThat(cellA.at(0),is(table0));
		assertThat(cellA.at(1),is(table1));
	}
	@Test public void toHtml() {
		StringBuilder stringBuilder = new StringBuilder();
		cellA.setLeader("LL");
		cellA.setTrailer("TT");
		cellA.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is("LL<td>AbC</td>TT"));
	}
	@Test public void toHtmlWithElements() {
		final StringBuilder stringBuilder = new StringBuilder();
		context.checking(new Expectations() {{
			oneOf(table0).toHtml(stringBuilder);
			oneOf(table1).toHtml(stringBuilder);
		}});
		cellA.add(table0);
		cellA.add(table1);
		cellA.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is("<td>AbC</td>"));
	}
	@Test public void toHtmlWithPass() {
		context.checking(new Expectations() {{
			allowing(testResults).pass();
		}});
		StringBuilder stringBuilder = new StringBuilder();
		cellA.pass(testResults);
		cellA.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is("<td class=\"pass\">AbC</td>"));
	}
	@Test public void toHtmlWithFail() {
		context.checking(new Expectations() {{
			allowing(testResults).fail();
		}});
		StringBuilder stringBuilder = new StringBuilder();
		cellA.fail(testResults);
		cellA.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is("<td class=\"fail\">AbC</td>"));
	}
	@Test public void withPreamble() {
		CellOnList cell = new CellOnList(TableFactory.tables(TableFactory.table()));
		cell.addPrefixToFirstInnerTable("preamble");
		assertThat(cell.at(0).getLeader(),is(" <span class=\"fit_label\">preamble</span>"));
		assertThat(cell.toString(),is("<td> <span class=\"fit_label\">preamble</span><table border=\"1\" cellspacing=\"0\"></table></td>"));
	}
}
