/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.closure;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.NoSystemUnderTestException;
import fitlibrary.exception.method.MissingMethodException;
import fitlibrary.flow.IScope;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.TypedObject;
import fitlibraryGeneric.typed.GenericTypedObject;
import fitlibraryGeneric.typed.GenericTypedObject.MethodTargetFactory;

@RunWith(JMock.class)
public class TestLookupMethodTarget {
	final LookupMethodTargetStandard lookup = new LookupMethodTargetStandard();
	final Mockery context = new Mockery();
	final Evaluator evaluator = context.mock(Evaluator.class);
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final IScope scope = context.mock(IScope.class);
	final LookupClosure lookupClosure = context.mock(LookupClosure.class);
	final Closure closure = context.mock(Closure.class);
	final MethodTargetFactory methodTargetFactory = context.mock(MethodTargetFactory.class);
	final TypedObject typedObjectS = new GenericTypedObject("S",lookupClosure,methodTargetFactory);
	final TypedObject typedObjectT = new GenericTypedObject("T",lookupClosure,methodTargetFactory);

	@Before
	public void allowingExpectations() {
		context.checking(new Expectations() {{
			allowing(evaluator).getRuntimeContext(); will(returnValue(runtime));
			allowing(runtime).extendedCamel("get unknown"); will(returnValue("getUnknown"));
			allowing(runtime).extendedCamel("is unknown"); will(returnValue("isUnknown"));
			allowing(runtime).extendedCamel("get m"); will(returnValue("getM"));
			allowing(runtime).extendedCamel("is m"); will(returnValue("isM"));
			allowing(runtime).extendedCamel("set m"); will(returnValue("setM"));
			allowing(runtime).extendedCamel("m"); will(returnValue("m"));
			allowing(runtime).extendedCamel("set unknown"); will(returnValue("setUnknown"));
			allowing(runtime).extendedCamel("unknown"); will(returnValue("unknown"));
		}});
	}
	@Test(expected=MissingMethodException.class)
	public void methodOrGetterMissingWithEmptyStack() throws Exception {
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(new ArrayList<TypedObject>()));
			oneOf(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
		}});
		lookup.findMethodOrGetter("unknown", new ArrayList<String>(), "void", evaluator);
	}
	@Test(expected=MissingMethodException.class)
	public void methodOrGetterMissing() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(list));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "unknown", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "getUnknown", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "isUnknown", 0); will(returnValue(null));
			oneOf(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
		}});
		lookup.findMethodOrGetter("unknown", new ArrayList<String>(), "void", evaluator);
	}
	@Test
	public void methodOrGetterExists() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(list));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "m", 0); will(returnValue(closure));
			oneOf(methodTargetFactory).createCalledMethodTarget(closure, evaluator);
		}});
		lookup.findMethodOrGetter("m", new ArrayList<String>(), "void", evaluator);
	}
	@Test
	public void methodOrGetterExistsOnSecondTypedObject() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		list.add(typedObjectT);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(list));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "m", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "getM", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "isM", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectT, "m", 0); will(returnValue(closure));
			oneOf(methodTargetFactory).createCalledMethodTarget(closure, evaluator);
		}});
		lookup.findMethodOrGetter("m", new ArrayList<String>(), "void", evaluator);
	}
	@Test(expected=MissingMethodException.class)
	public void methodMissingWithEmptyStack() throws Exception {
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(new ArrayList<TypedObject>()));
			oneOf(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
		}});
		lookup.findMethod("unknown", new ArrayList<String>(), "void", evaluator);
	}
	@Test(expected=MissingMethodException.class)
	public void methodMissing() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(list));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "unknown", 0); will(returnValue(null));
			oneOf(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
		}});
		lookup.findMethod("unknown", new ArrayList<String>(), "void", evaluator);
	}
	@Test
	public void methodExistsOnSecondTypedObject() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		list.add(typedObjectT);
		context.checking(new Expectations() {{
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).objectsForLookup(); will(returnValue(list));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "m", 0); will(returnValue(null));
			oneOf(lookupClosure).findMethodClosure(typedObjectT, "m", 0); will(returnValue(closure));
			oneOf(methodTargetFactory).createCalledMethodTarget(closure, evaluator);
		}});
		lookup.findMethod("m", new ArrayList<String>(), "void", evaluator);
	}
	@Test(expected=NoSystemUnderTestException.class)
	public void setterMissingAsNoSut() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		context.checking(new Expectations() {{
			allowing(evaluator).getTypedSystemUnderTest(); will(returnValue(new GenericTypedObject(null)));
		}});
		lookup.findSetterOnSut("m",evaluator);
	}
	@Test(expected=MissingMethodException.class)
	public void setterMissing() throws Exception {
		final ArrayList<TypedObject> list = new ArrayList<TypedObject>();
		list.add(typedObjectS);
		context.checking(new Expectations() {{
			allowing(evaluator).getTypedSystemUnderTest(); will(returnValue(typedObjectS));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "setUnknown", 1); will(returnValue(null));
			allowing(evaluator).getScope(); will(returnValue(scope));
			oneOf(scope).possibleClasses(); will(returnValue(new ArrayList<Class<?>>()));
		}});
		lookup.findSetterOnSut("unknown", evaluator);
	}
	@Test
	public void setterExistsOnTypedObject() throws Exception {
		context.checking(new Expectations() {{
			allowing(evaluator).getTypedSystemUnderTest(); will(returnValue(typedObjectS));
			oneOf(lookupClosure).findMethodClosure(typedObjectS, "setM", 1); will(returnValue(closure));
			oneOf(methodTargetFactory).createCalledMethodTarget(closure, evaluator);
		}});
		lookup.findSetterOnSut("m",evaluator);
	}
}
