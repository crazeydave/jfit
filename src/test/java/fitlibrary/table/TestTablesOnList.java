/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import static fitlibrary.matcher.TableBuilderForTests.table;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.runResults.TestResults;

@RunWith(JMock.class)
public class TestTablesOnList {
	Mockery context = new Mockery();
	TestResults testResults = context.mock(TestResults.class);

	final Table table1 = table().mock(context, "", 0);
	final Table table2 = table().mock(context, "", 1);
	final Tables tables12 = tables(table1,table2);
	
	@Before
	public void useListsFactory() {
		TableFactory.useOnLists(true);
	}
	@After
	public void stopUsingListsFactory() {
		TableFactory.pop();
	}
	@Test
	public void emptyTables() {
		assertThat(TableFactory.tables().toString(), is(""));
	}
	@Test
	public void iteratorIsEmptyWhenNoElements() {
		assertThat(TableFactory.tables().iterator().hasNext(), is(false));
	}
	@Test
	public void iteratorHasOneWhenOneElement() {
		Iterator<Table> iterator = tables(table1).iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(table1));
		assertThat(iterator.hasNext(), is(false));
	}
	@Test
	public void iteratorHasTwoWhenTwoElements() {
		Iterator<Table> iterator = tables12.iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(table1));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(table2));
		assertThat(iterator.hasNext(), is(false));
	}
	@Test
	public void iterableAfterFirstIsEmptyWhenNoElements() {
		Iterator<Table> iterator = tables().iterableFrom(1).iterator();
		assertThat(iterator.hasNext(), is(false));
	}
	@Test
	public void iterableAfterFirstIsEmptyWhenOneElement() {
		Iterator<Table> iterator = tables(table1).iterableFrom(1).iterator();
		assertThat(iterator.hasNext(), is(false));
	}
	@Test
	public void iterableAfterFirstHasOneWhenTwoElements() {
		Iterator<Table> iterator = tables12.iterableFrom(1).iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(table2));
		assertThat(iterator.hasNext(), is(false));
	}
	@Test
	public void isEmpty() {
		assertThat(tables().size(), is(0));
		assertThat(tables().isEmpty(), is(true));
	}
	@Test
	public void followingTables() {
		Tables followingTables = tables12.fromAt(1);
		assertThat(followingTables.size(), is(1));
		assertThat(followingTables.at(0), is(table2));
	}
	@Test
	public void error() {
		final FitLibraryException e = new FitLibraryException("aa");
		context.checking(new Expectations() {{
			oneOf(table1).error(testResults,e);
		}});
		tables12.error(testResults,e);
	}
	@Test
	public void addToTag() {
		context.checking(new Expectations() {{
			oneOf(table1).addToTag("extra");
		}});
		tables12.addToTag("extra");
	}
	@Test
	public void deepCopy() {
		final Table table1copy = table().mock(context, "", 55);
		final Table table2copy = table().mock(context, "", 66);
		context.checking(new Expectations() {{
			oneOf(table1).deepCopy(); will(returnValue(table1copy));
			oneOf(table2).deepCopy(); will(returnValue(table2copy));
		}});
		tables12.setLeader("LL");
		tables12.setTrailer("TT");
		Tables deepCopy = tables12.deepCopy();
		assertThat(deepCopy.size(), is(2));
		assertThat(deepCopy.at(0), is(table1copy));
		assertThat(deepCopy.at(1), is(table2copy));
		assertThat(deepCopy.getLeader(), is("LL"));
		assertThat(deepCopy.getTrailer(), is("TT"));
	}
	@Test public void toHtmlWithElements() {
		final StringBuilder stringBuilder = new StringBuilder();
		context.checking(new Expectations() {{
			oneOf(table1).toHtml(stringBuilder);
			oneOf(table2).toHtml(stringBuilder);
		}});
		tables12.setLeader("LL");
		tables12.setTrailer("TT");
		tables12.toHtml(stringBuilder);
		assertThat(stringBuilder.toString(),is(""));
	}
	@Test public void addTables() {
		tables12.addTables(tables(table2,table2));
		assertThat(tables12.size(), is(4));
		assertThat(tables12.at(2), is(table2));
		assertThat(tables12.at(3), is(table2));
	}

	protected static Tables tables(Table... ts) {
		Tables tables = new TablesOnList();
		for (Table table: ts)
			tables.add(table);
		return tables;
	}
}
