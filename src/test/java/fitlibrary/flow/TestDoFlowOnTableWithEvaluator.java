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

import fitlibrary.SetUpFixture;
import fitlibrary.collection.CollectionSetUpTraverse;
import fitlibrary.runResults.TestResults;
import fitlibrary.table.Table;
import fitlibrary.traverse.Evaluator;

@RunWith(JMock.class)
public class TestDoFlowOnTableWithEvaluator {
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
	public void runWithCollectionSetUpTraverse() {
		final Evaluator mockEvaluator = context.mock(Evaluator.class,"mockCollectionSetUpTraverse");
		final Evaluator evaluator = new CollectionSetUpTraverse() {
			@Override
			public Object interpretAfterFirstRow(Table table2, TestResults testResults2) {
				return mockEvaluator.interpretAfterFirstRow(table2, testResults2);
			}
		};
		verifyWithEvaluator(evaluator, mockEvaluator);
	}
	@Test
	public void runWithCollectionSetUpFixture() {
		final Evaluator mockEvaluator = context.mock(Evaluator.class,"mockSetUpFixture");
		final Evaluator evaluator = new SetUpFixture() {
			@Override
			public Object interpretAfterFirstRow(Table table2, TestResults testResults2) {
				return mockEvaluator.interpretAfterFirstRow(table2, testResults2);
			}
		};
		verifyWithEvaluator(evaluator, mockEvaluator);
	}
	@Test
	public void runWithEvaluator() {
		final Evaluator mockEvaluator = context.mock(Evaluator.class,"mockEvaluator");
		context.checking(new Expectations() {{
			allowing(mockEvaluator).setRuntimeContext(doFlowDriver.getRuntime());
			allowing(mockEvaluator).getSystemUnderTest(); will(returnValue(null));
		}});
		verifyWithEvaluator(mockEvaluator, mockEvaluator);
	}

	private void verifyWithEvaluator(Evaluator evaluator, Evaluator mockEvaluator) {
		doFlowDriver.startingOnTable(table);
		doFlowDriver.startingOnRow();
		doFlowDriver.interpretingRowReturning(table.at(0), evaluator);
		doFlowDriver.pushingObjectOnScopeStack(evaluator);
		doFlowDriver.callingSetUpOn(evaluator,table.at(0));
		doFlowDriver.interpretingEvaluator(mockEvaluator,table);

		doFlowDriver.runTable(table);
	}
}
