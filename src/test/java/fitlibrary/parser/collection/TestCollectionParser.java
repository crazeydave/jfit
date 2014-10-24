/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.parser.collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import fitlibrary.parser.Parser;
import fitlibrary.parser.ParserTestCase;
import fitlibrary.parser.lookup.ResultParser;
import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.table.Cell;
import fitlibrary.table.TableFactory;
import fitlibrary.traverse.Traverse;
import fitlibrary.traverse.workflow.DoEvaluator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestCollectionParser {
	DoEvaluator evaluator = ParserTestCase.evaluatorWithRuntime();
	Collection list;
	public Collection aProp;

	@Before
	public void setUp() {
        list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        aProp = new ArrayList();
        aProp.add("4");
        aProp.add("5");
        aProp.add("6");
    }
    @Test
	public void parserAlone() throws Exception {
		Parser parser = Traverse.asTyped(list).parser(evaluator);
		String cellText = "1, 2, 3";
		Cell cell = TableFactory.cell(cellText);
		TestResults testResults = TestResultsFactory.testResults();
		assertThat(parser.parseTyped(cell,testResults).getSubject(), is((Object)list));
		assertThat(parser.matches(cell, list,testResults),is(true));
		assertThat(parser.show(list),is(cellText));
	}
    @Test
	public void parserWithMethod() throws Exception {
		Method method = getClass().getMethod("aMethod", new Class[] {});
		ResultParser adapter = Traverse.asTypedObject(this).resultParser(evaluator, method);
		adapter.setTarget(this);
		assertThat(adapter.getResult(),is((Object)aProp));
		assertThat(adapter.show(adapter.getResult()),is("4, 5, 6"));
	}
	public Collection aMethod() {
		return aProp;
	}
}
