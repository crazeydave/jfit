/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.table;

import static fitlibrary.matcher.TableBuilderForTests.row;
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
import fitlibrary.runResults.TableListener;
import fitlibrary.runResults.TestResults;

@RunWith(JMock.class)
public class TestTableOnList {
	Mockery context = new Mockery();
	TestResults testResults = context.mock(TestResults.class);
	VariableResolver resolver = context.mock(VariableResolver.class);
	Row row0 = row().mock(context, "", 0);
	Row row1 = row().mock(context, "", 1);
	Row row2 = row().mock(context, "", 2);
	Table table01 = table(row0,row1);
	Table table012 = table(row0,row1,row2);
	
	@Before
	public void useListsFactory() {
		TableFactory.useOnLists(true);
	}
	@After
	public void stopUsingListsFactory() {
		TableFactory.pop();
	}
	@Test
	public void pass() {
		context.checking(new Expectations() {{
			oneOf(row0).pass(testResults);
		}});
		table01.pass(testResults);
	}
	@Test
	public void ignore() {
		context.checking(new Expectations() {{
			oneOf(row0).ignore(testResults);
		}});
		table01.ignore(testResults);
	}
	@Test
	public void error() {
		final RuntimeException e = new RuntimeException();
		context.checking(new Expectations() {{
			oneOf(row0).error(testResults,e);
		}});
		table01.error(testResults, e);
	}
	@Test
	public void errorWithTableListener() {
		final RuntimeException e = new RuntimeException();
		context.checking(new Expectations() {{
			oneOf(row0).error(testResults,e);
		}});
		table01.error(new TableListener(testResults), e);
	}
	@Test
	public void newRow() {
		table01.newRow();
		assertThat(table01.size(),is(3));
	}
	@Test
	public void phaseBoundaryCount() {
		table01.setLeader("12<hr/>34<hr/>456<hr/>");
		assertThat(table01.phaseBoundaryCount(),is(2));
	}
	@Test
	public void addFoldingText() {
		table01.addFoldingText("12");
		assertThat(table01.getTrailer(),is("12"));
	}
	@Test
	public void replaceAt() {
		table01.replaceAt(0,row2);
		assertThat(table01.size(),is(2));
		assertThat(table01.at(0),is(row2));
		assertThat(table01.at(1),is(row1));
	}
	@Test
	public void replaceAtAfter() {
		table01.replaceAt(2,row2);
		assertThat(table01.size(),is(3));
		assertThat(table01.at(0),is(row0));
		assertThat(table01.at(1),is(row1));
		assertThat(table01.at(2),is(row2));
	}
	@Test
	public void fromAt0() {
		Table fromAt = table012.fromAt(0);
		assertThat(fromAt.size(),is(3));
		assertThat(fromAt.at(0),is(row0));
		assertThat(fromAt.at(1),is(row1));
		assertThat(fromAt.at(2),is(row2));
	}
	@Test
	public void fromAt1() {
		Table fromAt = table012.fromAt(1);
		assertThat(fromAt.size(),is(2));
		assertThat(fromAt.at(0),is(row1));
		assertThat(fromAt.at(1),is(row2));
	}
	@Test
	public void fromAt2() {
		Table fromAt = table012.fromAt(2);
		assertThat(fromAt.size(),is(1));
		assertThat(fromAt.at(0),is(row2));
	}
	@Test
	public void hasRowsAfter() {
		assertThat(table01.hasRowsAfter(row0),is(true));
		assertThat(table01.hasRowsAfter(row1),is(false));
		assertThat(table01.hasRowsAfter(row2),is(false)); // Not in the table!
	}
	@Test
	public void deepCopy() {
		final Row row1copy = row().mock(context, "", 55);
		final Row row2copy = row().mock(context, "", 66);
		context.checking(new Expectations() {{
			oneOf(row0).deepCopy(); will(returnValue(row1copy));
			oneOf(row1).deepCopy(); will(returnValue(row2copy));
		}});
		table01.setLeader("LL");
		table01.setTrailer("TT");
		Table deepCopy = table01.deepCopy();
		assertThat(deepCopy.size(), is(2));
		assertThat(deepCopy.at(0), is(row1copy));
		assertThat(deepCopy.at(1), is(row2copy));
		assertThat(deepCopy.getLeader(), is("LL"));
		assertThat(deepCopy.getTrailer(), is("TT"));
	}
	@Test public void toHtml() {
		final StringBuilder stringBuilder = new StringBuilder();
		context.checking(new Expectations() {{
			oneOf(row0).toHtml(stringBuilder);
			oneOf(row1).toHtml(stringBuilder);
		}});
		table01.setLeader("LL");
		table01.setTrailer("TT");
		table01.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is("LL<table border=\"1\" cellspacing=\"0\"></table>TT"));
	}
	
	protected static Table table(Row... rows) {
		Table table = new TableOnList();
		for (Row row: rows)
			table.add(row);
		return table;
	}
}
