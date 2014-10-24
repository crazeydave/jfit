/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 8/11/2006
*/

package fitlibrary.debug;

import java.io.IOException;

import fit.FitServerBridge;
import fit.exception.FitParseException;
import fitlibrary.batch.fitnesseIn.ParallelFitNesseRepository;
import fitlibrary.differences.FitNesseLocalFile;
import fitlibrary.log.ConfigureLoggingThroughFiles;
import fitlibrary.runResults.TableListener;
import fitlibrary.runResults.TestResults;
import fitlibrary.suite.BatchFitLibrary;
import fitlibrary.suite.ReportListener;
import fitlibrary.table.Table;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;

public class DebugPage {
	private final String fitNesseDiry;
	private final int port;
	protected int tablesFinished = 0;
	protected int storytestsFinished = 0;
	protected int expectedTablesFinished = 0;
	
	protected ReportListener reportListener = new ReportListener() {
		@Override
		public void tableFinished(Table table) {
			tablesFinished++;
		}
		@Override
		public void tablesFinished(TestResults testResults) {
			storytestsFinished++;
		}
	};
	BatchFitLibrary batchFitLibrary = new BatchFitLibrary(new TableListener(reportListener));

	public DebugPage(String fitNesseDiry, int port) {
		this.fitNesseDiry = fitNesseDiry;
		this.port = port;
	}	
	
	public void debugPages(String[] pageNames) throws FitParseException, IOException {
		ConfigureLoggingThroughFiles.configure(fitNesseDiry+"/");
		FitNesseLocalFile.fitNessePrefix(fitNesseDiry);
		tablesFinished = 0;
		storytestsFinished = 0;
		for (int i = 0; i < pageNames.length; i++) {
			runPage(pageNames[i]);
			if (storytestsFinished != i+1)
				throw new RuntimeException("Wrong # of FixtureListener events fired for "+pageNames[i]+
						": "+storytestsFinished+" instead of "+(i+1));
		}
		if (tablesFinished != expectedTablesFinished)
			throw new RuntimeException("Expected FixtureListener events for "+expectedTablesFinished+
					" tables but instead got "+tablesFinished);
	}
	public void runPage(String pageName) throws IOException, FitParseException {
		String html = new ParallelFitNesseRepository(fitNesseDiry,port).getTest(pageName).getContent();
		System.out.println("\n----------\nHTML for "+pageName+"\n----------\n"+html);
		Tables tables = TableFactory.tables(html);
		expectedTablesFinished += tables.size();
		FitServerBridge.setFitNesseUrl("http://localhost:"+port); // Yuck passing important info through a global. See method for links.
		TestResults testResults = batchFitLibrary.doStorytest(tables);
		System.out.println("\n----------\nHTML Report for "+pageName+"\n----------\n"+tables.report());
		System.out.println(testResults);
	}

	public static void main(String[] args) throws Exception {
		String[] fullPageNames = new String[] {
				"FitLibrary.SpecifiCations.DoWorkflow.TestActions"
		};
//		final String FITNESSE_FOR_WEB_DIRY = "../fitlibraryweb/fitnesse";
		final String FITNESSE_DIRY = "fitnesse";
		final int PORT = 8990; // This determines the value of ${FITNESSE_PORT}
		run(fullPageNames,FITNESSE_DIRY,PORT);
	}
	
	// This is the best way to run this, from another class. For an eg of using it, see the main() above. 
	public static void run(String[] fullPageNames, String fitNesseDiry, int port) throws Exception {
		DebugPage debugPage = new DebugPage(fitNesseDiry,port);
		debugPage.debugPages(fullPageNames);
	}
}
