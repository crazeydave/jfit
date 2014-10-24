/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.traverse.workflow.caller.TwoStageSpecial;

@RunWith(JMock.class)
public class TestCheck extends SpecialActionTest {
	@Test
	public void worksOK() throws Exception {
		context.checking(new Expectations() {{
			allowing(initialRow).size();will(returnValue(3));
			one(actionContext).findMethodFromRow(initialRow,1,1);will(returnValue(target));
			allowing(initialRow).last();will(returnValue(expectedCell));
			allowing(initialRow).fromAt(2);will(returnValue(subRow));
			allowing(initialRow).at(0);will(returnValue(firstCell));
			one(target).invokeAndCheckForSpecial(subRow,expectedCell,testResults,initialRow,firstCell);
		}});
		TwoStageSpecial lazySpecial = special.check(initialRow);
		lazySpecial.run(testResults);
	}
	@Test(expected=RuntimeException.class)
	public void hasMissingMethod() throws Exception {
		context.checking(new Expectations() {{
			allowing(initialRow).size();will(returnValue(3));
			one(actionContext).findMethodFromRow(initialRow,1,1);will(throwException(new RuntimeException()));
		}});
		special.check(initialRow);
	}
	@Test(expected=MissingCellsException.class)
	public void rowIsTooSmall() throws Exception {
		context.checking(new Expectations() {{
			allowing(initialRow).size();will(returnValue(2));
		}});
		special.check(initialRow);
	}
}
