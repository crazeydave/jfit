/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.traverse.workflow.caller.TwoStageSpecial;

@RunWith(JMock.class)
public class TestSetSymbolNamed extends SpecialActionTest {
	class SetExpectations extends Expectations {
		public SetExpectations(String eq) throws Exception {
			allowing(initialRow).size(); will(returnValue(4));
			allowing(initialRow).text(2,actionContext); will(returnValue(eq));
			allowing(initialRow).at(0); will(returnValue(firstCell));
			allowing(initialRow).text(1,actionContext); will(returnValue("2nd"));
			allowing(initialRow).fromAt(3);will(returnValue(subRow));
		}
	}
	@Test
	public void setWithValueFromInnerMethod() throws Exception {
		context.checking(new SetExpectations("") {{
			one(actionContext).findMethodFromRow(initialRow,2,0);
			   will(returnValue(target));
			one(target).invokeForSpecial(subRow,testResults,true,firstCell);
			  will(returnValue("234"));
			one(actionContext).setFitVariable("2nd","234");
		}});
		TwoStageSpecial lazySpecial = special.setSymbolNamed(initialRow);
		lazySpecial.run(testResults);
	}
	@Test
	public void innerMethodThrowsException() throws Exception {
		final Exception exception = new RuntimeException();
		context.checking(new SetExpectations("") {{
			one(actionContext).findMethodFromRow(initialRow,2,0);
			   will(returnValue(target));
			one(target).invokeForSpecial(subRow,testResults,true,firstCell);
			   will(throwException(exception));
			one(initialRow).error(testResults,exception);
		}});
		TwoStageSpecial lazySpecial = special.setSymbolNamed(initialRow);
		lazySpecial.run(testResults);
	}
	@Test
	public void innerMethodThrowsIgnoredException() throws Exception {
		final Exception exception = new IgnoredException();
		context.checking(new SetExpectations("") {{
			one(actionContext).findMethodFromRow(initialRow,2,0);
			   will(returnValue(target));
			one(target).invokeForSpecial(subRow,testResults,true,firstCell);
			   will(throwException(exception));
		}});
		TwoStageSpecial lazySpecial = special.setSymbolNamed(initialRow);
		lazySpecial.run(testResults);
	}
	@Test
	public void setWithValueFromOfOgnl() throws Exception {
		context.checking(new SetExpectations("=") {{
			one(initialRow).text(3,actionContext); will(returnValue("1+2"));
			one(actionContext).setFitVariable("2nd",3);
		}});
		TwoStageSpecial lazySpecial = special.setSymbolNamed(initialRow);
		lazySpecial.run(testResults);
	}
	@Test(expected=RuntimeException.class)
	public void hasMissingMethod() throws Exception {
		context.checking(new SetExpectations("") {{
			one(actionContext).findMethodFromRow(initialRow,2,0);
			  will(throwException(new RuntimeException()));
		}});
		special.setSymbolNamed(initialRow);
	}
	@Test(expected=MissingCellsException.class)
	public void rowIsTooSmall() throws Exception {
		context.checking(new Expectations() {{
			allowing(initialRow).size();will(returnValue(1));
		}});
		special.setSymbolNamed(initialRow);
	}
}
