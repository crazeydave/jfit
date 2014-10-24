/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.typed;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.awt.Point;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.closure.Closure;
import fitlibrary.closure.LookupClosure;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.traverse.DomainAdapter;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.RuntimeContextual;
import fitlibraryGeneric.typed.GenericTypedObject;
import fitlibraryGeneric.typed.GenericTypedObject.MethodTargetFactory;

@RunWith(JMock.class)
public class TestGenericTypedObject {
	final Mockery context = new Mockery();
	final Subject subject = context.mock(Subject.class);
	final Evaluator evaluator = context.mock(Evaluator.class);
	final LookupClosure lookupClosure = context.mock(LookupClosure.class);
	final Closure closure = context.mock(Closure.class);
	final MethodTargetFactory methodTargetFactory = context.mock(MethodTargetFactory.class);
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final RuntimeContextual runtimeContextual = context.mock(RuntimeContextual.class,"contextual1");
	final GenericTypedObject typedObject = new GenericTypedObject(subject,lookupClosure,methodTargetFactory);

	@Test
	public void methodDoesNotExist() throws Exception {
		context.checking(new Expectations() {{
			oneOf(lookupClosure).findMethodClosure(typedObject,"unknown",0); will(returnValue(null));
		}});
		assertThat(typedObject.new_findSpecificMethod("unknown",0,evaluator).isNone(),
				is(true));
	}
	@Test
	public void methodExistsOnEvaluator() throws Exception {
		verifyMethodOnEvaluator("m");
	}
	@Test
	public void hasNoSutWhenTypedSystemUnderTestIsNull() throws Exception {
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut(null));
		assertThat(typedObject2.hasTypedSystemUnderTest(),is(false));
	}
	@Test
	public void hasSutWhenTypedSystemUnderTestIsNotNull() throws Exception {
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut("s"));
		assertThat(typedObject2.hasTypedSystemUnderTest(),is(true));
		assertThat(typedObject2.getTypedSystemUnderTest().getSubject(),is((Object)"s"));
	}
	@Test
	public void doesNoInjectWhenTypedSystemUnderTestIsNull() throws Exception {
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut(null));
		typedObject2.injectRuntime(runtime);
	}
	@Test
	public void doesNoInjectWhenTypedSystemUnderTestIsNotRuntimeContextual() throws Exception {
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut("s"));
		typedObject2.injectRuntime(runtime);
	}
	@Test
	public void injectsWhenTypedSystemUnderTestIsRuntimeContextual() throws Exception {
		context.checking(new Expectations() {{
			oneOf(runtimeContextual).setRuntimeContext(runtime);
			allowing(runtimeContextual).getSystemUnderTest(); will(returnValue(null));
		}});
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut(runtimeContextual));
		typedObject2.injectRuntime(runtime);
	}
	@Test
	public void injectsTwiceWhenChainOfTypedSystemUnderTestIsRuntimeContextual() throws Exception {
		final RuntimeContextual runtimeContextual2 = context.mock(RuntimeContextual.class,"contextual2");
		context.checking(new Expectations() {{
			oneOf(runtimeContextual).setRuntimeContext(runtime);
			allowing(runtimeContextual).getSystemUnderTest(); will(returnValue(runtimeContextual2));
			oneOf(runtimeContextual2).setRuntimeContext(runtime);
			allowing(runtimeContextual2).getSystemUnderTest(); will(returnValue(null));
		}});
		GenericTypedObject typedObject2 = new GenericTypedObject(new WithSut(runtimeContextual));
		typedObject2.injectRuntime(runtime);
	}
	@Test
	public void isNullWhenNullSubject() {
		assertThat(new GenericTypedObject(null).isNull(),is(true));
	}
	@Test
	public void isNotNullWhenNotNullSubject() {
		assertThat(new GenericTypedObject("s").isNull(),is(false));
	}
	@Test
	public void classOfSubjectIsString() {
		assertThat(new GenericTypedObject("s").classType(),is((Object)String.class));
	}
	@Test
	public void classOfSubjectIsPoint() {
		assertThat(new GenericTypedObject(new Point()).classType(),is((Object)Point.class));
	}

	
	
	private void verifyMethodOnEvaluator(final String methodName) throws Exception {
		context.checking(new Expectations() {{
			oneOf(methodTargetFactory).createCalledMethodTarget(closure, evaluator);
			oneOf(lookupClosure).findMethodClosure(typedObject, methodName, 0); will(returnValue(closure));
		}});
		assertThat(typedObject.new_findSpecificMethod(methodName,0,evaluator).isSome(),
				is(true));
	}
	
	interface Subject extends Evaluator {
		void m();
	}
	interface Sut {
		void m();
	}
	static class WithSut implements DomainAdapter {
		private final Object sut;
		
		public WithSut(Object sut) {
			this.sut = sut;
		}
		@Override
		public Object getSystemUnderTest() {
			return sut;
		}
	}
}
