/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.special;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.closure.LookupClosure;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.flow.IScope;
import fitlibrary.parser.Parser;
import fitlibrary.runResults.TestResults;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.table.Row;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.Pair;
import fitlibraryGeneric.typed.GenericTypedObject;
import fitlibraryGeneric.typed.GenericTypedObject.MethodTargetFactory;

@RunWith(JMock.class)
public class TestPositionedTargetWasFound {
	final Mockery context = new Mockery();
	final Subject subject = context.mock(Subject.class);
	final Evaluator evaluator = context.mock(Evaluator.class);
	final LookupClosure lookupClosure = context.mock(LookupClosure.class);
	final MethodTargetFactory methodTargetFactory = context.mock(MethodTargetFactory.class);
	final LookupMethodTarget lookupMethodTarget = context.mock(LookupMethodTarget.class);
	final ICalledMethodTarget innerMethodTarget = context.mock(ICalledMethodTarget.class,"inner");
	final IScope scope = context.mock(IScope.class);
	final TestResults testResults = context.mock(TestResults.class);
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final Parser intParser = context.mock(Parser.class);
	final GenericTypedObject typedObject = new GenericTypedObject(subject,lookupClosure,methodTargetFactory);

	@Before
	public void allowingExpectations() {
		final List<TypedObject> objects = new ArrayList<TypedObject>();
		objects.add(typedObject);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			allowing(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
			allowing(scope).objectsForLookup(); will(returnValue(objects));
			allowing(innerMethodTarget).getParameterTypes(); will(returnValue(new Class<?>[0]));
			allowing(innerMethodTarget).getOwningClass(); will(returnValue(Subject.class));
			allowing(evaluator).getRuntimeContext(); will(returnValue(runtime));
			allowing(runtime).extendedCamel("find String"); will(returnValue("findString"));
			allowing(runtime).extendedCamel("show String"); will(returnValue("showString"));
			allowing(runtime).extendedCamel("find DoAction"); will(returnValue("findDoAction"));
			allowing(runtime).extendedCamel("show DoAction"); will(returnValue("showDoAction"));
			allowing(runtime).extendedCamel("find boolean"); will(returnValue("findBoolean"));
			allowing(runtime).extendedCamel("show boolean"); will(returnValue("showBoolean"));
			allowing(runtime).extendedCamel("find void"); will(returnValue("findVoid"));
			allowing(runtime).extendedCamel("show void"); will(returnValue("showVoid"));
			allowing(runtime).extendedCamel("find Object"); will(returnValue("findObject"));
			allowing(runtime).extendedCamel("show Object"); will(returnValue("showObject"));
			allowing(runtime).extendedCamel(" m"); will(returnValue("m"));
			allowing(runtime).extendedCamel("m"); will(returnValue("m"));
			allowing(runtime).extendedCamel(" n"); will(returnValue("n"));
		}});
	}
	@Test
	public void binaryPrefixInnerTargetNotFound() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("m", 1, evaluator);
			will(returnValue(null));
		}});
		String[] cells = {"binary", "1", "preSpecial", "2", "m","a"};
		Method method = getMethod("binaryPreSpecial", String.class, String.class, DoAction.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,4,6,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(false));
	}
	@Test
	public void binaryPrefixInnerRunsReturningTrue() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("m", 0, evaluator);
			  will(returnValue(innerMethodTarget));
			allowing(evaluator).resolve("1"); will(resolveTo("1"));
			allowing(evaluator).resolve("2"); will(resolveTo("2"));
			oneOf(subject).binaryPreSpecial((String)with(is("1")),(String)with(is("2")),with(any(DoAction.class)));
			  will(returnValue(true));
			oneOf(testResults).pass();
		}});
		String[] cells = {"binary", "1", "preSpecial", "2", "m"};
		Method method = getMethod("binaryPreSpecial", String.class, String.class, DoAction.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,4,5,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(true));
		assertThat(positionedTarget.ambiguityErrorMessage(),startsWith("Special binaryPreSpecial(String,String,DoAction) + m()"));
		Row row = TableFactory.row(cells);
		positionedTarget.run(row,testResults,runtime);
	}
	@Test
	public void binaryPostfixInnerTargetNotFound() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("m", 1, evaluator);
			  will(returnValue(null));
		}});
		String[] cells = {"m","a","binary", "1", "postSpecial", "2"};
		Method method = getMethod("binaryPostSpecial", DoAction.class, String.class, String.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,0,2,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(false));
	}
	@Test
	public void binaryPostfixInnerRunsReturningFalse() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("m", 0, evaluator);
			  will(returnValue(innerMethodTarget));
			allowing(evaluator).resolve("1"); will(resolveTo("1"));
			allowing(evaluator).resolve("2"); will(resolveTo("2"));
			oneOf(subject).binaryPostSpecial(with(any(DoAction.class)),(String)with(is("1")),(String)with(is("2")));
			  will(returnValue(false));
			oneOf(testResults).fail();
		}});
		String[] cells = {"m", "binary", "1", "postSpecial", "2"};
		Method method = getMethod("binaryPostSpecial", DoAction.class, String.class, String.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,0,1,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(true));
		assertThat(positionedTarget.ambiguityErrorMessage(),startsWith("Special binaryPostSpecial(DoAction,String,String) + m()"));
		Row row = TableFactory.row(cells);
		positionedTarget.run(row,testResults,runtime);
	}
	@Test
	public void nullaryInnerRunsReturningNothing() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("n", 1, evaluator);
			  will(returnValue(innerMethodTarget));
			oneOf(subject).nullarySpecial(with(any(DoAction.class)));
		}});
		String[] cells = {"nullarySpecial", "n", "z"};
		Method method = getMethod("nullarySpecial", DoAction.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,1,3,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(true));
		assertThat(positionedTarget.ambiguityErrorMessage(),startsWith("Special nullarySpecial(DoAction) + n()"));
		Row row = TableFactory.row(cells);
		positionedTarget.run(row,testResults,runtime);
	}
	@Test
	public void unaryPostfixTakesObject() throws Exception {
		String[] cells = {"m", "unaryPostSpecial", "2"};
		final Row row = TableFactory.row(cells);
		context.checking(new Expectations() {{
			oneOf(lookupMethodTarget).findTheMethodMapped("m", 0, evaluator);
			  will(returnValue(innerMethodTarget));
			oneOf(innerMethodTarget).getResultParser(); will(returnValue(intParser));
			oneOf(intParser).parseTyped(row.at(2), testResults); will(returnValue(new GenericTypedObject(2)));
			oneOf(subject).unaryPostSpecial(with(any(DoAction.class)),with(is(2)));
		}});
		Method method = getMethod("unaryPostSpecial", DoAction.class, Object.class);
		PositionedTarget positionedTarget = 
			new PositionedTargetWasFound(evaluator,cells,typedObject,method,0,1,false,lookupMethodTarget);
		assertThat(positionedTarget.isFound(),is(true));
		assertThat(positionedTarget.ambiguityErrorMessage(),startsWith("Special unaryPostSpecial(DoAction,Object) + m()"));
		positionedTarget.run(row,testResults,runtime);
	}
	private Method getMethod(String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Method method = subject.getClass().getMethod(methodName, parameterTypes);
		return method;
	}
	protected Action resolveTo(String s) {
		return Expectations.returnValue(new Pair<String,Tables>(s,TableFactory.tables()));
	}
	interface Subject {
		int m();
		void n(String s);
		void nullarySpecial(DoAction action);
		void unaryPostSpecial(DoAction action, Object any);
		boolean binaryPreSpecial(String i, String j, DoAction action);
		boolean binaryPostSpecial(DoAction action, String i, String j);
	}
}
