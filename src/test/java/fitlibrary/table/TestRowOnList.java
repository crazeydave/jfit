/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import fitlibrary.runtime.RuntimeContextContainer;
import fitlibrary.runtime.RuntimeContextInternal;

public class TestRowOnList {
	RuntimeContextInternal runtime = new RuntimeContextContainer();
	Row row = row();
	
	@Test
	public void sizeOfEmptyRow() {
		assertThat(row.size(),is(0));
		assertThat(row.isEmpty(),is(true));
	}
	@Test public void columnSpanWithNoCellsIsZero() throws Exception {
		assertThat(row.getColumnSpan(),is(0));
	}
	@Test public void columnSpanWithOneCellWithNoColumnSpanSetIsOne() throws Exception {
		row.addCell("1");
		assertThat(row.getColumnSpan(),is(1));
	}
	@Test public void columnSpanWithOneCellWithColumnSpanSet() throws Exception {
		row.addCell("1", 4);
		assertThat(row.getColumnSpan(),is(4));
	}
	@Test public void columnSpanWithMultipleCellsWithAndWithoutColumnSpanSet() throws Exception {
		row.addCell("1", 4);
		row.addCell("2");
		row.addCell("3", 2);
		row.addCell("4");
		assertThat(row.getColumnSpan(),is(8));
	}
	@Test public void setColumnSpanWithNoCellsHadsOneCell() {
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.at(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithOneCellWithNoColumnSpanSet() {
		row.addCell("1");
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.at(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithOneCellWithColumnSpanSet() {
		row.addCell("1", 3);
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.at(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithMultipleCellsWithAndWithoutColumnSpanSetAddToTheColumnSpanOfLastCell() throws Exception {
		row.addCell("1", 4);
		row.addCell("2");
		row.addCell("3", 2);
		row.addCell("4",4);
		row.setColumnSpan(15);
		assertThat(row.getColumnSpan(),is(15));
		assertThat(row.at(0).getColumnSpan(),is(4));
		assertThat(row.at(1).getColumnSpan(), is(1));
		assertThat(row.at(2).getColumnSpan(),is(2));
		assertThat(row.at(3).getColumnSpan(),is(8));
	}
	@Test
	public void plainMethodNameNoArg() {
		Row row2 = row("aa");
		assertThat(row2.methodNameForPlain(runtime),is("aa"));
		assertThat(row2.methodNameForCamel(runtime),is("aa"));
	}
	@Test
	public void plainMethodNameOneArg() {
		Row row2 = row("aa","1");
		assertThat(row2.methodNameForPlain(runtime),is("aa|"));
		assertThat(row2.methodNameForCamel(runtime),is("aa"));
	}
	@Test
	public void plainMethodNameOneArgTwoKeywords() {
		Row row2 = row("aa","1","bb");
		assertThat(row2.methodNameForPlain(runtime),is("aa|bb"));
		assertThat(row2.methodNameForCamel(runtime),is("aaBb"));
	}
	@Test
	public void plainMethodNameTwoArgs() {
		Row row2 = row("aa","1","bb","2");
		assertThat(row2.methodNameForPlain(runtime),is("aa|bb|"));
		assertThat(row2.methodNameForCamel(runtime),is("aaBb"));
	}
	@Test
	public void plainMethodNameTwoArgsThreeKeywords() {
		Row row2 = row("aa","1","bb","2",".");
		assertThat(row2.methodNameForPlain(runtime),is("aa|bb|."));
		assertThat(row2.methodNameForCamel(runtime),is("aaBbDot"));
	}
	@Test
	public void canReplaceRowAtStart() {
		Row row0 = row("a","b");
		Row row1 = row("x","y");
		Row row2 = row("m","n");
		Table table = table();
		table.add(row0);
		table.add(row1);
		assertThat(table.at(0),is(row0));
		assertThat(table.at(1),is(row1));
		table.replaceAt(0, row2);
		assertThat(table.at(0),is(row2));
		assertThat(table.at(1),is(row1));
	}
	@Test
	public void canReplaceRowAtEnd() {
		Row row0 = row("a","b");
		Row row1 = row("x","y");
		Row row2 = row("m","n");
		Table table = table();
		table.add(row0);
		table.add(row1);
		table.replaceAt(1, row2);
		assertThat(table.at(0),is(row0));
		assertThat(table.at(1),is(row2));
	}
	@Test
	public void elementsFrom0() {
		Row row0 = row("a","b","c");
		Row copy = row0.fromAt(0);
		assertThat(copy.size(),is(3));
		assertThat(copy.at(0),is(row0.at(0)));
		assertThat(copy.at(1),is(row0.at(1)));
		assertThat(copy.at(2),is(row0.at(2)));
	}
	@Test
	public void elementsFrom2() {
		Row row0 = row("a","b","c");
		Row copy = row0.fromAt(2);
		assertThat(copy.size(),is(1));
		assertThat(copy.at(0),is(row0.at(2)));
	}
	@Test
	public void elementsFromTo() {
		Row row0 = row("a","b","c");
		Row copy = row0.fromTo(1,2);
		assertThat(copy.size(),is(1));
		assertThat(copy.at(0),is(row0.at(1)));
	}
	@Test
	public void last() {
		Row row0 = row("a","b","c");
		assertThat(row0.last(),is(row0.at(2)));
	}

	
	protected static Row row() {
		return new RowOnList();
	}
	protected static Row row(String... text) {
		Row row = row();
		for (String cellText: text)
			row.add(new CellOnList(cellText));
		return row;
	}
	protected static Table table() {
		return new TableOnList();
	}
}
