/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.xref;

import org.junit.Test;

import fit.exception.FitParseException;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.table.Tables;
import fitlibrary.utility.SimpleWikiTranslator;

public class TestCrossReferenceFixture
{
	@Test
	public void tt() throws FitParseException {
		CrossReferenceFixture xref = new CrossReferenceFixture(".IsBook") {
			@Override
			protected String fitNesseDiry() {
				return "C:/working/FitNesseDocServer";
			}
		};
		String wiki =  "||";
		Tables tables = SimpleWikiTranslator.translateToTables(wiki); 
		xref.interpretAfterFirstRow(tables.at(0), TestResultsFactory.testResults());
	}
}
