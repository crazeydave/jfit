/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.runner;

import java.io.IOException;
import java.text.ParseException;

public class FolderRunnerTest {
    public static void main(String[] argsIgnore) throws ParseException, IOException {
    	String DIRY = "folderRunner/";
        run(new String[]{ DIRY+"tests", DIRY+"reports" });
//
//        run(new String[]{ DIRY+"spreadsheetTests", DIRY+"spreadsheetReports" });
//
//        String SUITE_DIRY = DIRY+"/suiteTests/";
//        run(new String[]{ "-s", SUITE_DIRY+"SuiteFixtureExample.html", 
//        		SUITE_DIRY+"tests", SUITE_DIRY+"reports" });
//        run(new String[]{ "-s", SUITE_DIRY+"AnotherSuiteFixtureExample.html", 
//        		SUITE_DIRY+"tests", SUITE_DIRY+"otherReports" });
    }

	private static void run(String[] args) throws ParseException, IOException {
		FolderRunner folderRunner = new FolderRunner(args);
		final StringBuilder sbOut = new StringBuilder();
		final StringBuilder sbErr = new StringBuilder();
		folderRunner.addTestListener(new StoryTestListener() {
			@Override
			public void testComplete(boolean failing, String pageCounts, String assertionCounts) {
				//
			}
			@Override
			public void suiteComplete() {
				//
			}
			@Override
			public void reportOutput(String name, String out, String output) {
				if (output.isEmpty())
					return;
				if (out.equals("out"))
					sbOut.append(name+": "+": "+output); // sysout is temporarily redirected
				else
					sbErr.append(name+": "+": "+output); // syserr is temporarily redirected
			}
		});
		Report report = folderRunner.run();
        System.out.print(sbOut);
        System.err.print(sbErr);
        report.displayCounts();
	}
}
