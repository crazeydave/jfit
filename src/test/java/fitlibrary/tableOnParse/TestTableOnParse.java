/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.tableOnParse;

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
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.TableFactory;

@RunWith(JMock.class)
public class TestTableOnParse {
	Mockery context = new Mockery();
	TestResults testResults = context.mock(TestResults.class);
	VariableResolver resolver = context.mock(VariableResolver.class);
	Row row0 = new RowOnParse();
	Row row1 = new RowOnParse();
	Row row2 = new RowOnParse();
	Table table01;
	
	@Before
	public void useListsFactory() {
		TableFactory.useOnLists(false);
		row0.add(new CellOnParse("0"));
		row1.add(new CellOnParse("1"));
		row2.add(new CellOnParse("2"));
		table01 = table(row0,row1);
	}
	@After
	public void stopUsingListsFactory() {
		TableFactory.pop();
	}
	@Test
	public void pass() {
		context.checking(new Expectations() {{
			oneOf(testResults).pass();
		}});
		table01.pass(testResults);
		assertThat(table01.at(0).didPass(),is(true));
	}
	@Test
	public void ignore() {
		context.checking(new Expectations() {{
			oneOf(testResults).ignore();
		}});
		table01.ignore(testResults);
	}
	@Test
	public void error() {
		final RuntimeException e = new RuntimeException();
		context.checking(new Expectations() {{
			oneOf(testResults).exception();
		}});
		table01.error(testResults, e);
		assertThat(table01.at(0).at(0).hadError(),is(true));
	}
	@Test
	public void errorWithTableListener() {
		final RuntimeException e = new RuntimeException();
		context.checking(new Expectations() {{
			oneOf(testResults).exception();
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
		Table fromAt = table01.fromAt(0);
		assertThat(fromAt.size(),is(2));
		assertThat(fromAt.at(0),is(row0));
		assertThat(fromAt.at(1),is(row1));
	}
	@Test
	public void fromAt1() {
		Table fromAt = table01.fromAt(1);
		assertThat(fromAt.size(),is(1));
		assertThat(fromAt.at(0),is(row1));
	}
	@Test
	public void hasRowsAfter() {
		assertThat(table01.hasRowsAfter(row0),is(true));
		assertThat(table01.hasRowsAfter(row1),is(false));
		assertThat(table01.hasRowsAfter(row2),is(false)); // Not in the table!
	}
	@Test
	public void deepCopy() {
		table01.setLeader("LL");
		table01.setTrailer("TT");
		Table deepCopy = table01.deepCopy();
		assertThat(deepCopy.size(),is(2));
		assertThat(deepCopy.at(0).at(0).text(), is("0"));
		assertThat(deepCopy.at(1).at(0).text(), is("1"));
		assertThat(deepCopy.getLeader(), is("LL"));
		assertThat(deepCopy.getTrailer(), is("TT"));
	}
	@Test public void toHtml() {
		final StringBuilder stringBuilder = new StringBuilder();
		table01.setLeader("LL");
		table01.setTrailer("TT");
		table01.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),
				is("LL<table border=\"1\" cellspacing=\"0\">\n"+
				   "<tr>\n<td>0</td></tr>\n<tr>\n<td>1</td></tr></table>TT"));
	}
	
	protected static Table table(Row... rows) {
		Table table = new TableOnParse();
		for (Row row: rows)
			table.add(row);
		return table;
	}
}
