/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryExceptionInHtml;
import fitlibrary.exception.method.AmbiguousActionException;
import fitlibrary.runResults.TestResults;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Row;
import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.TypedObject;

@RunWith(JMock.class)
public class TestDispatchRowInFlow {
	Mockery context = new Mockery();
	Evaluator evaluator = context.mock(Evaluator.class);
	Row row = context.mock(Row.class);
	TestResults testResults = context.mock(TestResults.class);
	DoCaller doCaller1 = context.mock(DoCaller.class,"DoCaller#1");
	DoCaller doCaller2 = context.mock(DoCaller.class,"DoCaller#2");
	TypedObject typedObject = context.mock(TypedObject.class);
	RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);

	@Test public void oneMatch() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			allowing(doCaller1).isValid(); will(returnValue(true));
			allowing(evaluator).getRuntimeContext(); will(returnValue(runtime));
			allowing(runtime).isAbandoned(testResults); will(returnValue(false));
			oneOf(doCaller1).ambiguityErrorMessage(); will(returnValue("one"));
			oneOf(doCaller1).run(row,testResults); will(returnValue(typedObject));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[] { doCaller1 };
			}
		};
		assertThat(dispatchRowInFlow.interpretRow(row, testResults),is(typedObject));
	}
	@Test public void oneMatchOfSeveral() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			allowing(doCaller1).isValid(); will(returnValue(true));
			allowing(doCaller2).isValid(); will(returnValue(false));
			allowing(doCaller2).isAmbiguous(); will(returnValue(false));
			allowing(evaluator).getRuntimeContext(); will(returnValue(runtime));
			allowing(runtime).isAbandoned(testResults); will(returnValue(false));
			oneOf(doCaller1).ambiguityErrorMessage(); will(returnValue("one"));
			oneOf(doCaller1).run(row,testResults); will(returnValue(typedObject));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[] { doCaller1, doCaller2 };
			}
		};
		assertThat(dispatchRowInFlow.interpretRow(row, testResults),is(typedObject));
	}
	@Test public void noMatch() {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			oneOf(row).error(testResults,new FitLibraryExceptionInHtml("Missing class or"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[0];
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
	@Test public void twoMatchesSoAmbiguous() {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			oneOf(doCaller1).isValid(); will(returnValue(true));
			oneOf(doCaller2).isValid(); will(returnValue(true));
			oneOf(doCaller1).ambiguityErrorMessage(); will(returnValue("one"));
			oneOf(doCaller2).ambiguityErrorMessage(); will(returnValue("two"));
			oneOf(row).error(testResults,new AmbiguousActionException("one AND two"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[]{ doCaller1,doCaller2 };
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
	@Test public void oneMatchWhichIsLocallyAmbiguous() {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			oneOf(doCaller1).isValid(); will(returnValue(false));
			oneOf(doCaller1).isAmbiguous(); will(returnValue(true));
			oneOf(doCaller1).ambiguityErrorMessage(); will(returnValue("one AND two"));
			oneOf(row).error(testResults,new AmbiguousActionException("one AND two"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[]{ doCaller1 };
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
	@Test public void oneMatchOfTwoWhichIsLocallyAmbiguous() {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			allowing(doCaller1).isValid(); will(returnValue(false));
			allowing(doCaller1).isAmbiguous(); will(returnValue(true));
			allowing(doCaller1).ambiguityErrorMessage(); will(returnValue("one AND two"));
			allowing(doCaller2).isValid(); will(returnValue(true));
			allowing(doCaller2).ambiguityErrorMessage(); will(returnValue("three"));
			oneOf(row).error(testResults,new AmbiguousActionException("one AND two AND three"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[]{ doCaller1,doCaller2 };
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
	@Test public void onePartialError() {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			allowing(doCaller1).isValid(); will(returnValue(false));
			allowing(doCaller1).isAmbiguous(); will(returnValue(false));
			allowing(doCaller1).partiallyValid(); will(returnValue(true));
			allowing(doCaller1).getPartialErrorMessage(); will(returnValue("one AND two"));
			oneOf(row).error(testResults,new FitLibraryExceptionInHtml("one AND two"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[]{ doCaller1 };
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
	@Test public void oneMatchWithProblem() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).size(); will(returnValue(2));
			allowing(doCaller1).isValid(); will(returnValue(false));
			allowing(doCaller1).isAmbiguous(); will(returnValue(false));
			allowing(doCaller1).partiallyValid(); will(returnValue(false));
			allowing(doCaller1).isProblem(); will(returnValue(true));
			oneOf(doCaller1).problem(); will(returnValue(new RuntimeException("one")));
			oneOf(row).error(testResults,new FitLibraryExceptionInHtml(
					"Missing class or Missing method. Possibly:<ul><li>one</li></ul>"));
		}});
		DispatchRowInFlow dispatchRowInFlow = new DispatchRowInFlow(evaluator,false) {
			@Override
			protected DoCaller[] createDoCallers(Row row2, boolean sequenced) {
				return new DoCaller[] { doCaller1 };
			}
		};
		dispatchRowInFlow.interpretRow(row, testResults);
	}
}
