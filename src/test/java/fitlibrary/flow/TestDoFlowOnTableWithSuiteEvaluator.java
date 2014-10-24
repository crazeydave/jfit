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
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.suite.SuiteEvaluator;
import fitlibrary.table.Table;

@RunWith(JMock.class)
public class TestDoFlowOnTableWithSuiteEvaluator {
	final Mockery context = new Mockery();
	final DoFlowOnTableDriver driver = new DoFlowOnTableDriver(context);
	
	final Table table = 
			table().with(
					row().with(cell(),cell())
			).mock(context,"storytest1");
	
	@Test
	public void runWithPlainSuiteFixture() {
		final SuiteEvaluator suiteEvaluator = context.mock(SuiteEvaluator.class);
		context.checking(new Expectations() {{
			allowing(suiteEvaluator).getSystemUnderTest();  will(returnValue(null));
			oneOf(suiteEvaluator).setRuntimeContext(driver.getRuntime());
		}});
		verifyWithEvaluator(suiteEvaluator);
	}

	private void verifyWithEvaluator(final SuiteEvaluator suiteEvaluator) {
		driver.startingOnTable(table);
		driver.startingOnRow();
		driver.interpretingRowReturning(table.at(0), suiteEvaluator);
		driver.setSuite(suiteEvaluator);
		driver.callingSuiteSetUpOn(suiteEvaluator,table.at(0));
		driver.pushingObjectOnScopeStack(suiteEvaluator);
		driver.callingSetUpOn(suiteEvaluator, table.at(0));
		
		driver.runTable(table);
		
	}
}
