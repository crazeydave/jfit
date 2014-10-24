/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;

import fit.Parse;
import fit.exception.FitParseException;
import fitlibrary.config.Configuration;
import fitlibrary.dynamicVariable.GlobalDynamicVariables;
import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.flow.DoFlowOnTable.DoFlower;
import fitlibrary.flow.TestDoFlowOnTableWithFixture.MockFixture;
import fitlibrary.object.DomainFixtured;
import fitlibrary.runResults.ITableListener;
import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.suite.SuiteEvaluator;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.FitHandler;
import fitlibrary.traverse.workflow.FlowEvaluator;
import fitlibraryGeneric.typed.GenericTypedObject;

public class DoFlowOnTableDriver {
	private final Mockery context;
	final FlowEvaluator flowEvaluator;
	final IScopeStack scopeStack;
	final ITableListener tableListener;
	final SetUpTearDown setUpTearDown;
	final RuntimeContextInternal runtime;
	final DoFlower doFlower;
	final TestResults testResults;
	final DoFlowOnTable doFlowOnTable;
	final Configuration config;

	final VariableResolver resolver = new GlobalDynamicVariables();

	final States state;
	final static String BEGIN_STATE = "begin";
	String currentState = BEGIN_STATE;
	int rowNo = 0;

	public DoFlowOnTableDriver(Mockery context) {
		this.context = context;
		flowEvaluator = context.mock(FlowEvaluator.class);
		scopeStack = context.mock(IScopeStack.class);
		tableListener = context.mock(ITableListener.class);
		setUpTearDown = context.mock(SetUpTearDown.class);
		runtime = context.mock(RuntimeContextInternal.class,"runtime");
		doFlower = context.mock(DoFlower.class);
		config = context.mock(Configuration.class);
		testResults = TestResultsFactory.testResults();
		doFlowOnTable = new DoFlowOnTable(flowEvaluator, scopeStack, setUpTearDown, doFlower);
		state = context.states("doFlowOnTable").startsAs(BEGIN_STATE);
		startTable();
	}
	public void runTable(Table table) {
		poppingScopeStackAtEndOfTable();
		doFlowOnTable.runTable(table, testResults, runtime);
	}
	public void startingOnTable(final Table table) {
		final String endState = endState("startingOnTable");
		context.checking(new Expectations() {{
			oneOf(runtime).setCurrentTable(table);                        when(state.is(currentState));
			allowing(tableListener).getTestResults();
			   will(returnValue(testResults));                            when(state.is(currentState));
			oneOf(runtime).pushTestResults(with(any(TestResults.class))); when(state.is(currentState));
			                                                              then(state.is(endState));
		}});
		currentState = endState;
	}
	public void startingOnRow() {
		rowNo++;
		final String endState = endState("startingOnRow");
		context.checking(new Expectations() {{
			allowing(runtime).isAbandoned(with(any(TestResults.class)));
			  will(returnValue(false));                           when(state.is(currentState));
			allowing(doFlower).hasDomainCheck();
			  will(returnValue(false));                           when(state.is(currentState));
			                                                      then(state.is(endState));
		}});
		currentState = endState;
	}
	public void startingOnRowWithDomainCheck() {
		rowNo++;
		final String endState = endState("startingOnRow");
		context.checking(new Expectations() {{
			allowing(runtime).isAbandoned(with(any(TestResults.class)));
			  will(returnValue(false));                           when(state.is(currentState));
			allowing(doFlower).hasDomainCheck();
			  will(returnValue(true));                           when(state.is(currentState));
			                                                      then(state.is(endState));
		}});
		currentState = endState;
	}
	public void interpretingRowReturning(final Row row, final Object object) {
		final String endState = endState("interpretingRowReturning");
		context.checking(new Expectations() {{
			allowing(runtime).isAbandoned(with(any(TestResults.class)));
			  will(returnValue(false));                           when(state.is(currentState));
			allowing(doFlower).hasDomainCheck();
			  will(returnValue(false));                           when(state.is(currentState));
			oneOf(runtime).setCurrentRow(row);                    when(state.is(currentState));
			oneOf(flowEvaluator).interpretRow(row,testResults);
			  will(returnValue(new GenericTypedObject(object)));  when(state.is(currentState));
			                                                      then(state.is(endState));
		}});
		currentState = endState;
	}
	public void interpretingFixture(final MockFixture mockFixture, final Table table) throws FitParseException {
		final Parse parse = new Parse("<table><tr><td>1</td></tr></table>");
		final String endState = endState("interpretingFixture");
		context.checking(new Expectations() {{
			oneOf(table).asTableOnParse(); will(returnValue(table)); when(state.is(currentState));
			oneOf(flowEvaluator).fitHandler();
			  will(returnValue(new FitHandler()));                   when(state.is(currentState));
			oneOf(table).asParse(); will(returnValue(parse));        when(state.is(currentState));
			oneOf(mockFixture).doTable(parse);                       when(state.is(currentState));
			oneOf(table).replaceAt(0, table.at(0));                  when(state.is(currentState));
			oneOf(table).replaceAt(1, table.at(1));                  when(state.is(currentState));
			                                                         then(state.is(endState));
		}});
		currentState = endState;
	}
	private void poppingScopeStackAtEndOfTable() {
		final String endState = endState("poppingScopeStackAtEndOfTable");
		context.checking(new Expectations() {{
			oneOf(runtime).popTestResults();  when(state.is(currentState));
			                                  then(state.is(endState));
		}});
		currentState = endState;
	}
	public void pushingObjectOnScopeStack(final Object sut) {
		final String endState = endState("pushingObjectOnScopeStack");
		context.checking(new Expectations() {{
			oneOf(scopeStack).push(new GenericTypedObject(sut)); when(state.is(currentState));
			                                                     then(state.is(endState));
		}});
		currentState = endState;
	}
	public void callingSetUpOn(final Object sut, final Row row) {
		final String endState = endState("callingSetUpOn");
		context.checking(new Expectations() {{
			oneOf(setUpTearDown).callSetUpOnSutChain(sut, row, testResults); when(state.is(currentState));
			                                                                 then(state.is(endState));
		}});
		currentState = endState;
	}
	public void interpretingEvaluator(final Evaluator mockEvaluator, final Table table) {
		final String endState = endState("interpretingEvaluator");
		context.checking(new Expectations() {{
			oneOf(mockEvaluator).interpretAfterFirstRow(table, testResults); 
			                                                     when(state.is(currentState));
			                                                     then(state.is(endState));
		}});
		currentState = endState;
	}
	public void runInnerTables(final Tables embeddedTables) {
		final String endState = endState("runInnerTables");
		context.checking(new Expectations() {{
			allowing(runtime).isAbandoned(with(any(TestResults.class)));
			  will(returnValue(false));                          when(state.is(currentState));
			oneOf(doFlower).runInnerTables(embeddedTables, testResults);
			                                                     when(state.is(currentState));
			                                                     then(state.is(endState));
		}});
		currentState = endState;
	}
	private void startTable() {
		context.checking(new Expectations() {{
			allowing(tableListener).getTestResults(); will(returnValue(testResults));
			allowing(runtime).getResolver(); will(returnValue(resolver));
			allowing(runtime).getConfiguration(); will(returnValue(config));
			allowing(config).isAddTimings(); will(returnValue(false));
		}});
	}
	private String endState(String name) {
		return name+"."+rowNo;
	}
	public RuntimeContextInternal getRuntime() {
		return runtime;
	}
	public void setSuite(final SuiteEvaluator suiteEvaluator) {
		final String endState = endState("setSuite");
		context.checking(new Expectations() {{
			oneOf(doFlower).setSuite(suiteEvaluator);   when(state.is(currentState));
			                                            then(state.is(endState));
		}});
		currentState = endState;
	}
	public void callingSuiteSetUpOn(final Object sut, final Row row) {
		final String endState = endState("callingSuiteSetUpOn");
		context.checking(new Expectations() {{
			oneOf(setUpTearDown).callSuiteSetUp(sut, row, testResults); when(state.is(currentState));
			                                                               then(state.is(endState));
		}});
		currentState = endState;
	}
	public void callingSuiteTearDownOn(final Object sut) {
		final String endState = endState("callingSuiteTearDownOn");
		context.checking(new Expectations() {{
			oneOf(setUpTearDown).callSuiteTearDown(with(sut), with(any(TestResults.class))); when(state.is(currentState));
			                                                          then(state.is(endState));
		}});
		currentState = endState;
	}
	public void settingDomainToCheck() {
		final String endState = endState("settingDomainToCheck");
		context.checking(new Expectations() {{
			oneOf(doFlower).setDomainToCheck();  when(state.is(currentState));
			                                     then(state.is(endState));
		}});
		currentState = endState;
	}
	public void settingDomainFixture(final DomainFixtured domainFixtured) {
		final String endState = endState("settingDomainFixture");
		context.checking(new Expectations() {{
			oneOf(doFlower).setDomainFixture(new GenericTypedObject(domainFixtured));
			                                     when(state.is(currentState));
			                                     then(state.is(endState));
		}});
		currentState = endState;
	}
}
