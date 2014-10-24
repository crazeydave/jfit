/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static fitlibrary.matcher.TableBuilderForTests.cell;
import static fitlibrary.matcher.TableBuilderForTests.row;
import static fitlibrary.matcher.TableBuilderForTests.table;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.table.Table;

@RunWith(JMock.class)
public class TestDoFlowOnTableWithNestedTables {
	final Mockery context = new Mockery();
	final DoFlowOnTableDriver driver = new DoFlowOnTableDriver(context);
	final Table table = makeTable();

	private Table makeTable() {
		return table().with(
				row().with(
						cell().with(
								table().with(row().with(cell()))),
						cell())
		).mock(context);
	}
	
	@Test
	public void innerTableIsRun() {
		driver.startingOnTable(table);
		driver.startingOnRow();
		driver.runInnerTables(table.at(0).at(0));
		
		driver.runTable(table);
	}
}
