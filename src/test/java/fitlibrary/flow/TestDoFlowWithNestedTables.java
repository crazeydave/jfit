/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static fitlibrary.matcher.TableBuilderForTests.cell;
import static fitlibrary.matcher.TableBuilderForTests.row;
import static fitlibrary.matcher.TableBuilderForTests.table;
import static fitlibrary.matcher.TableBuilderForTests.tables;

import java.util.List;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.utility.CollectionUtility;

@RunWith(JMock.class)
public class TestDoFlowWithNestedTables {
	final Mockery context = new Mockery();
	final DoFlowDriverForTests driver = new DoFlowDriverForTests(context);
	final Tables tables = makeTables();

	private Tables makeTables() {
		return tables().with(table().with(
				row().with(cell(),cell()),
				row().with(
						cell().with(
								table().with(row().with(cell()))),
						cell())
		)).mock(context);
	}
	
	@Test
	public void innerTableIsRun() {
		final String s = "s";
		final String t = "t";
		final Object doS = new DoTraverse(s);
		final Object doT = new DoTraverse(t);

		final Table table = tables.at(0);
		driver.startingOnTable(table);
		driver.interpretingRowReturning(table.at(0), doS);
		driver.pushingObjectOnScopeStack(s);
		driver.callingSetUpOn(s,table.at(0));
		final IScopeState scopeState = driver.startingOnInnerTablesWithCurrentScopeState();
		
		final Table innerTable = table.at(1).at(0).at(0);
		driver.startingOnTable(innerTable);
		driver.interpretingRowReturning(innerTable.at(0), doT);
		driver.pushingObjectOnScopeStack(t);
		driver.callingSetUpOn(t,innerTable.at(0));
		driver.restoringScopeGiving(scopeState,list(t));
		driver.callingTearDownOn(t, innerTable.at(0));
		
		driver.poppingScopeStackAtEndOfLastTableGiving(list(s));
		driver.callingTearDownOn(s, table.at(0));
		driver.finishingTable(table);

		driver.runStorytest(tables);
	}
	protected List<Object> list(Object... ss) {
		return CollectionUtility.list(ss);
	}
}
