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

import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.object.DomainFixtured;
import fitlibrary.table.Table;

@RunWith(JMock.class)
public class TestDoFlowOnTableWithDomainFixture {
	final Mockery context = new Mockery();
	final DoFlowOnTableDriver doFlowDriver = new DoFlowOnTableDriver(context);
	
	@Test
	public void runWithCollectionSetUpTraverse() {
		final Table table = table().with(
				row().with(cell(),cell())
		).mock(context);
		context.checking(new Expectations() {{
			allowing(table).fromAt(0); will(returnValue(table));
		}});
		final DomainFixtured domainFixtured = context.mock(DomainFixtured.class);
		
		doFlowDriver.startingOnTable(table);
		doFlowDriver.startingOnRow();
		doFlowDriver.interpretingRowReturning(table.at(0), domainFixtured);
		doFlowDriver.pushingObjectOnScopeStack(domainFixtured);
		doFlowDriver.callingSetUpOn(domainFixtured,table.at(0));
		doFlowDriver.settingDomainFixture(domainFixtured);
		
		doFlowDriver.runTable(table);
	}
	@Test
	public void domainCheckSwitchesCorrectly() {
		final Table checkTable = table().with(
				row().with(cell("checks"))
		).mock(context);
		context.checking(new Expectations() {{
			allowing(checkTable.at(0)).text(with(0),with(any(VariableResolver.class))); will(returnValue("checks"));
		}});

		doFlowDriver.startingOnTable(checkTable);
		doFlowDriver.startingOnRowWithDomainCheck();
		doFlowDriver.settingDomainToCheck();
		
		doFlowDriver.runTable(checkTable);
	}
}
