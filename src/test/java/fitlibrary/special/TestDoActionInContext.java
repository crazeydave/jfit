/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.special;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.parser.Parser;
import fitlibrary.runResults.TestResults;
import fitlibrary.runtime.RuntimeContext;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Row;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;
import fitlibrary.utility.Pair;
import fitlibraryGeneric.typed.GenericTypedObject;

@RunWith(JMock.class)
public class TestDoActionInContext {
	final Mockery context = new Mockery();
	final ICalledMethodTarget target = context.mock(ICalledMethodTarget.class,"inner");
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final TestResults testResults = context.mock(TestResults.class);
	final Parser parser1 = context.mock(Parser.class,"parser1");
	final Parser parser2 = context.mock(Parser.class,"parser2");
	final VariableResolver resolver = context.mock(VariableResolver.class);

	@Before
	public void allowingExpectations() {
		context.checking(new Expectations() {{
			allowing(runtime).getTestResults(); will(returnValue(testResults));
		}});
	}
	@Test(expected=RuntimeException.class)
	public void runWithNoArgsCausesException() throws Exception {
		context.checking(new Expectations() {{
			oneOf(target).getParameterParsers(); will(returnValue(new Parser[0]));
			oneOf(target).invoke(new Object[0]); will(throwException(new RuntimeException("failed")));
		}});
		Row row = TableFactory.row();
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 1, false,runtime);
		doActionInContext.run();
	}
	@Test
	public void runWithNoArgsReturnsString() throws Exception {
		context.checking(new Expectations() {{
			oneOf(target).getParameterParsers(); will(returnValue(new Parser[0]));
			oneOf(target).invoke(new Object[0]); will(returnValue("result"));
		}});
		Row row = TableFactory.row("m");
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 1, false,runtime);
		assertThat(doActionInContext.run(),is((Object)"result"));
	}
	@Test
	public void runWith1ArgReturnsString() throws Exception {
		final Row row = TableFactory.row("m","22");
		final Parser[] parsers = { parser1 };
		context.checking(new Expectations() {{
			oneOf(target).getParameterParsers();
			will(returnValue(parsers));
			oneOf(parser1).parseTyped(row.at(1), testResults);
			  will(returnValue(new GenericTypedObject("22")));
			oneOf(target).invoke(new Object[]{"22"});  will(returnValue("result2"));
		}});
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		assertThat(doActionInContext.run(),is((Object)"result2"));
	}
	@Test
	public void runWith2ArgsReturnsInt() throws Exception {
		final Row row = TableFactory.row("m","22","n","44");
		final Parser[] parsers = { parser1, parser2 };
		context.checking(new Expectations() {{
			oneOf(target).getParameterParsers();
			will(returnValue(parsers));
			oneOf(parser1).parseTyped(row.at(1), testResults);
			  will(returnValue(new GenericTypedObject("22")));
			oneOf(parser2).parseTyped(row.at(3), testResults);
			  will(returnValue(new GenericTypedObject("44")));
			oneOf(target).invoke(new Object[]{"22","44"});  will(returnValue("result2"));
		}});
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		assertThat(doActionInContext.run(),is((Object)"result2"));
	}
	@Test
	public void CanAccessRuntime() throws Exception {
		final Row row = TableFactory.row("any");
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		assertThat(doActionInContext.getRuntime(),is((RuntimeContext)runtime));
	}
	@Test
	public void CanAccessSpecialCellsToPass() throws Exception {
		final Row row = TableFactory.row("m","22","n","44");
		context.checking(new Expectations() {{
			oneOf(testResults).pass();
		}});
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		doActionInContext.cellAt(0).pass();
		assertThat(row.at(2).didPass(),is(true));
	}
	@Test
	public void CanAccessSpecialCellsToFail() throws Exception {
		final Row row = TableFactory.row("m","22","n","44");
		context.checking(new Expectations() {{
			oneOf(runtime).getResolver(); will(returnValue(resolver));
			oneOf(resolver).resolve("44"); will(resolveTo("44"));
			oneOf(testResults).fail();
		}});
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		doActionInContext.cellAt(1).fail("error");
		assertThat(row.at(3).didFail(),is(true));
	}
	@Test
	public void CanAccessSpecialCellsToError() throws Exception {
		final Row row = TableFactory.row("m","22","n","44");
		context.checking(new Expectations() {{
			oneOf(testResults).exception();
		}});
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		doActionInContext.cellAt(1).error(new RuntimeException());
		assertThat(row.at(3).hadError(),is(true));
	}
	
	@Test(expected=FitLibraryException.class)
	public void CannotAccessSpecialCellsOutside() throws Exception {
		final Row row = TableFactory.row("m","22","n","44");
		DoActionInContext doActionInContext = new DoActionInContext(target, row, 0, 2, false,runtime);
		doActionInContext.cellAt(2).fail("error");
	}
	protected Action resolveTo(String s) {
		return Expectations.returnValue(new Pair<String,Tables>(s,TableFactory.tables()));
	}
}
