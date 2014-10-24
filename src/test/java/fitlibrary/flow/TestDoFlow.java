/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static fitlibrary.matcher.TableBuilderForTests.cell;
import static fitlibrary.matcher.TableBuilderForTests.row;
import static fitlibrary.matcher.TableBuilderForTests.table;
import static fitlibrary.matcher.TableBuilderForTests.tables;

import java.awt.Point;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fit.Fixture;
import fitlibrary.DoFixture;
import fitlibrary.SelectFixture;
import fitlibrary.matcher.TableBuilderForTests.TableBuilder;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.DomainAdapter;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.utility.CollectionUtility;

@RunWith(JMock.class)
public class TestDoFlow {
	final Mockery context = new Mockery();
	final DoFlowDriverForTests driver = new DoFlowDriverForTests(context);
	final Tables tables = makeTables();

	// FOLLOWING DON'T CHANGE SCOPE AT ALL:
	@Test
	public void nullSoNoScopeChange() {
		verifyNoScopeChangeWith(null);
	}
	@Test
	public void anObjectOfTheClassFixtureSoNoScopeChange() {
		verifyNoScopeChangeWith(new Fixture());
	}
	@Test
	public void aNonWrappableSoNoScopeChange() {
		verifyNoScopeChangeWith("string");
	}
	@Test
	public void anObjectOfTheClassDoFixtureWithNoSutSoNoScopeChange() {
		verifyNoScopeChangeWith(new DoFixture());
	}
	@Test
	public void aDoTraverseWithNoSutSoNoScopeChange() {
		verifyNoScopeChangeWith(new DoTraverse());
	}
	@Test
	public void aSelectFixtureWithNoSutSoNoScopeChange() {
		context.checking(new Expectations() {{
			exactly(2).of(driver.getRuntime()).showAsAfterTable(with(any(String.class)),with(any(String.class)));
		}});
		verifyNoScopeChangeWith(new SelectFixture());
	}
	// THE FOLLOWING CHANGE THE STACK:
	@Test
	public void aDoTraverseWithSutSoScopeChange() {
		verifyScopePush(new DoTraverse(new Point()),new Point());
	}
	@Test
	public void aPojoWithSutSoScopeChange() {
		PojoWithSut pojoWithSut = new PojoWithSut();
		verifyScopePush(new DoTraverse(pojoWithSut),pojoWithSut);
	}
	static class PojoWithSut implements DomainAdapter {
		@Override
		public Object getSystemUnderTest() {
			return new Point();
		}
	}
	@Test
	public void aDoEvaluatorSoScopeChange() {
		final DoEvaluatorExample doEval = context.mock(DoEvaluatorExample.class);
		context.checking(new Expectations() {{
			exactly(2).of(doEval).setRuntimeContext(with(aNonNull(RuntimeContextInternal.class)));
			allowing(doEval).getSystemUnderTest(); will(returnValue(null));
		}});
		verifyScopePush(doEval,doEval);
	}
	interface DoEvaluatorExample extends DoEvaluator {
		//
	}
	@Test
	public void aDoEvaluatorWithSetUpTearDownSoScopeChange() {
		final DoEvaluator doEval = context.mock(DoEvaluator.class);
		context.checking(new Expectations() {{
			exactly(2).of(doEval).setRuntimeContext(with(aNonNull(RuntimeContextInternal.class)));
			allowing(doEval).getSystemUnderTest(); will(returnValue(null));
		}});
		verifyScopePush(doEval,doEval);
	}

	@Test
	public void twoDoTraversesSoScopeStackChange() {
		String s = "s";
		String t = "t";
		final Object doS = new DoTraverse(s);
		final Object doT = new DoTraverse(t);

		Table table0 = tables.at(0);
		Table table1 = tables.at(1);
		
		driver.startingOnTable(table0);
		driver.interpretingRowReturning(table0.at(0), doS);
		driver.pushingObjectOnScopeStack(s);
		driver.callingSetUpOn(s,table0.at(0));
		driver.interpretingRowReturning(table0.at(1), doT);
		driver.pushingObjectOnScopeStack(t);
		driver.callingSetUpOn(t,table0.at(1));
		driver.poppingScopeStackAtEndOfTable(list(t,s));
		driver.callingTearDownOn(t, table0.at(0));
		driver.callingTearDownOn(s, table0.at(0));
		driver.finishingTable(table0);

		driver.startingOnTable(table1);
		driver.interpretingRowReturning(table1.at(0), doS);
		driver.pushingObjectOnScopeStack(s);
		driver.callingSetUpOn(s,table1.at(0));
		driver.interpretingRowReturning(table1.at(1), doT);
		driver.pushingObjectOnScopeStack(t);
		driver.callingSetUpOn(t,table1.at(1));
		driver.poppingScopeStackAtEndOfLastTableGiving(list(t,s));
		driver.callingTearDownOn(t, table1.at(0));
		driver.callingTearDownOn(s, table1.at(0));
		driver.finishingTable(table1);

		driver.runStorytest(tables);
	}

	private void verifyScopePush(final Object result, final Object sut) {
		Table table0 = tables.at(0);
		Table table1 = tables.at(1);

		driver.startingOnTable(table0);
		driver.interpretingRowReturning(table0.at(0), result);
		driver.pushingObjectOnScopeStack(sut); // -- extra
		driver.callingSetUpOn(sut,table0.at(0)); // -- extra
		driver.interpretingRowReturning(table0.at(1), null);
		driver.poppingScopeStackAtEndOfTable(list(result));
		driver.callingTearDownOn(result, table0.at(0));
		driver.finishingTable(table0);

		driver.startingOnTable(table1);
		driver.interpretingRowReturning(table1.at(0), result);
		driver.pushingObjectOnScopeStack(sut); // -- extra
		driver.callingSetUpOn(sut,table1.at(0)); // -- extra
		driver.interpretingRowReturning(table1.at(1), null);
		driver.poppingScopeStackAtEndOfLastTableGiving(list(result));
		driver.callingTearDownOn(result, table1.at(0));
		driver.finishingTable(table1);

		driver.runStorytest(tables);
	}
	private void verifyNoScopeChangeWith(final Object result) {
		Table table0 = tables.at(0);
		Table table1 = tables.at(1);
		
		driver.startingOnTable(table0);
		driver.interpretingRowReturning(table0.at(0),result);
		driver.interpretingRowReturning(table0.at(1), null);
		driver.poppingAtEndOfTable();
		driver.finishingTable(table0);

		driver.startingOnTable(table1);
		driver.interpretingRowReturning(table1.at(0), result);
		driver.interpretingRowReturning(table1.at(1), null);
		driver.poppingScopeStackAtEndOfLastTableGiving();
		driver.finishingTable(table1);

		driver.runStorytest(tables);
	}
	private Tables makeTables() {
		return tables().with(
				tableWith2RowsOf2(),
				tableWith2RowsOf2()
		).mock(context);
	}
	private TableBuilder tableWith2RowsOf2() {
		return table().with(
				row().with(cell(),cell()),
				row().with(cell(),cell())
		);
	}
	protected List<Object> list(Object... ss) {
		return CollectionUtility.list(ss);
	}
}
