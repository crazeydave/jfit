/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static fitlibrary.matcher.TableBuilderForTests.cell;
import static fitlibrary.matcher.TableBuilderForTests.row;
import static fitlibrary.matcher.TableBuilderForTests.table;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fit.ColumnFixture;
import fit.Fixture;
import fit.Parse;
import fit.exception.FitParseException;
import fitlibrary.table.Table;

@RunWith(JMock.class)
public class TestDoFlowOnTableWithFixture {
	final Mockery context = new Mockery();
	final DoFlowOnTableDriver doFlowDriver = new DoFlowOnTableDriver(context);
	final Table table = table().with(
			row().with(cell(),cell()),
			row().with(cell(),cell())
	).mock(context);
	
	@Before
	public void allows() {
		context.checking(new Expectations() {{
			allowing(table).fromAt(0); will(returnValue(table));
		}});
	}
	@Test
	public void runWithFixture() throws FitParseException {
		final MockFixture mockFixture = context.mock(MockFixture.class);
		final Fixture evaluator = new ColumnFixture() {
			@Override
			public void doTable(Parse parse) {
				mockFixture.doTable(parse);
			}
		};
		doFlowDriver.startingOnTable(table);
		doFlowDriver.startingOnRow();
		doFlowDriver.interpretingRowReturning(table.at(0),evaluator);
		doFlowDriver.interpretingFixture(mockFixture, table);

		doFlowDriver.runTable(table);
	}

	static interface MockFixture {
		void doTable(Parse parse);
	}
}
