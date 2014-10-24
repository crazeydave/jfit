/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.FlowEvaluator;
import fitlibrary.typed.TypedObject;

@RunWith(JMock.class)
public class TestDoFlowWithSelect {
	final Mockery context = new Mockery();
	final Row row = context.mock(Row.class);
	final FlowEvaluator flowEvaluator = context.mock(FlowEvaluator.class);
	final IScopeStack scopeStack = context.mock(IScopeStack.class);
	final Object something = "something";
	final TypedObject someTypedObject = context.mock(TypedObject.class);
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final SetUpTearDown setUpTearDown = context.mock(SetUpTearDown.class);
	final DoFlow doFlow = new DoFlow(flowEvaluator,scopeStack,runtime,setUpTearDown);

	@Test(expected=FitLibraryException.class)
	public void selectUnknown() {
		context.checking(new Expectations() {{
			oneOf(scopeStack).select("unknown"); will(throwException(new FitLibraryException("")));
		}});
		doFlow.select("unknown");
	}
	@Test
	public void addNamedObjectAndSelect() {
		final TestResults testResults = TestResultsFactory.testResults();
		context.checking(new Expectations() {{
			oneOf(someTypedObject).getSubject(); will(returnValue(something));
			oneOf(someTypedObject).injectRuntime(runtime);
			oneOf(setUpTearDown).callSetUpOnSutChain(something, row, testResults);
			oneOf(scopeStack).addNamedObject("x",someTypedObject);
			oneOf(scopeStack).select("x");
		}});
		doFlow.addNamedObject("x",someTypedObject,row,testResults);
		doFlow.select("x");
	}
}
