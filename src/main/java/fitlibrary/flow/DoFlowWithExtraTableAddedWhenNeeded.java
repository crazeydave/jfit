/*
 * Copyright (c) 2011 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 */
package fitlibrary.flow;

import org.apache.log4j.Logger;

import fitlibrary.log.FitLibraryLogger;
import fitlibrary.runResults.ITableListener;
import fitlibrary.runResults.TestResults;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Table;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;
import fitlibrary.traverse.workflow.FlowEvaluator;

//This handles the last table differently
public class DoFlowWithExtraTableAddedWhenNeeded extends DoFlow {
	private static Logger logger = FitLibraryLogger.getLogger(DoFlowWithExtraTableAddedWhenNeeded.class);

	public DoFlowWithExtraTableAddedWhenNeeded(FlowEvaluator flowEvaluator, IScopeStack scopeStack, RuntimeContextInternal runtime, SetUpTearDown setUpTearDown) {
		super(flowEvaluator, scopeStack, runtime, setUpTearDown);
	}

	@Override
	public void runStorytest(Tables tables, ITableListener tableListener) {
		logger.trace("Running storytest");
		TestResults testResults = tableListener.getTestResults();
		resetToStartStorytest();
		for (int t = 0; t < tables.size(); t++) {
			Table table = tables.at(t);
			runSingleTable(testResults, table);
			finishTable(table, testResults);
			addAccumulatedFoldingText(table);
			tableListener.tableFinished(table);
		}
		Table errorTable = TableFactory.table(TableFactory.row("<i>Error in storytest tear down: </i>"));
		finishLastTable(errorTable, testResults);
		addAccumulatedFoldingText(errorTable);
		if (errorTable.size() > 1 || errorTable.at(0).size() > 1 || errorTable.at(0).at(0).hadError() || !errorTable.getTrailer().isEmpty()) {
			errorTable.setLeader("\n<br/>");
			tables.add(errorTable); // Needed just for embedded tables, as with SpecifyFixture.
			tableListener.tableFinished(errorTable);
		}
		logger.trace("Finished storytest");
		tableListener.storytestFinished();
	}
}
