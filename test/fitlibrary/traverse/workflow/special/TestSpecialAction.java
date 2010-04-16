/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import org.jmock.Mockery;

import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.utility.TestResults;
import fitlibrary.utility.TestResultsFactory;

public abstract class TestSpecialAction {
	Mockery context = new Mockery();
	SpecialActionContext actionContext = context.mock(SpecialActionContext.class);
	Row initialRow = context.mock(Row.class,"initialRow");
	Row subRow = context.mock(Row.class,"subRow");
	Cell expectedCell = context.mock(Cell.class,"expected cell");
	Cell firstCell = context.mock(Cell.class,"first cell");
	TestResults testResults = TestResultsFactory.testResults();
	PrefixSpecialAction special = new PrefixSpecialAction(actionContext);
	ICalledMethodTarget target = context.mock(ICalledMethodTarget.class);
	
}
