/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.parser;

import java.lang.reflect.Method;

import fitlibrary.parser.lookup.ResultParser;
import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.table.Cell;
import fitlibrary.table.TableFactory;
import fitlibrary.traverse.Traverse;

public class TestSimpleValueAdapter extends ParserTestCase {
	public int aProp = 567;

	public void testParseAlone() throws Exception {
		Parser parser = Traverse.asTyped(int.class).parser(evaluatorWithRuntime());
		String cellText = "12";
		Cell cell = TableFactory.cell(cellText);
		Integer expectedResult = Integer.valueOf(12);
		TestResults testResults = TestResultsFactory.testResults();
		assertEquals(expectedResult,parser.parseTyped(cell,testResults).getSubject());
		assertTrue(parser.matches(cell, expectedResult,testResults));
		assertEquals(cellText,parser.show(expectedResult));
	}
	public void testParseWithMethod() throws Exception {
		Method method = getClass().getMethod("aMethod", new Class[] {});
		ResultParser adapter = Traverse.asTypedObject(this).resultParser(evaluatorWithRuntime(), method);
		adapter.setTarget(this);
		assertEquals(Integer.valueOf(43),adapter.getResult());
		assertEquals("43",adapter.show(adapter.getResult()));
	}
	public int aMethod() {
		return 43;
	}
}
