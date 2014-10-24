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
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.utility.CollectionUtility;

@RunWith(JMock.class)
public class TestDoFlowWithEvaluator {
	final Mockery context = new Mockery();
	final DoFlowDriverForTests doFlowDriver = new DoFlowDriverForTests(context);
	
	final Tables tables = tables().with(table().with(
			row().with(cell(),cell()),
			row().with(cell(),cell()))).mock(context);
	
	@Before
	public void allows() {
		context.checking(new Expectations() {{
			allowing(tables.at(0)).fromAt(0); will(returnValue(tables.at(0)));
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
		doFlowDriver.showTearDown = true;
		Table table0 = tables.at(0);
		
		doFlowDriver.startingOnTable(table0);
		doFlowDriver.interpretingRowReturning(table0.at(0), evaluator);
		doFlowDriver.pushingObjectOnScopeStack(evaluator);
		doFlowDriver.callingSetUpOn(evaluator,table0.at(0));
		doFlowDriver.interpretingEvaluator(mockEvaluator,table0);
		doFlowDriver.poppingScopeStackAtEndOfLastTableGiving(list(evaluator));
		doFlowDriver.callingTearDownOn(evaluator, table0.at(0));
		doFlowDriver.finishingTable(table0);

		doFlowDriver.runStorytest(tables);
	}
	protected List<Object> list(Object... ss) {
		return CollectionUtility.list(ss);
	}
}
