/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.typed;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.closure.Closure;
import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.closure.LookupClosure;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.flow.IScope;
import fitlibrary.runtime.RuntimeContextContainer;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.special.DoAction;
import fitlibrary.special.PositionedTarget;
import fitlibrary.special.PositionedTargetFactory;
import fitlibrary.traverse.Evaluator;
import fitlibraryGeneric.typed.GenericTypedObject;
import fitlibraryGeneric.typed.GenericTypedObject.MethodTargetFactory;

@RunWith(JMock.class)
public class TestGenericTypedObjectFurther {
	final Mockery context = new Mockery();
	final Subject subject = context.mock(Subject.class);
	final Evaluator evaluator = context.mock(Evaluator.class);
	final LookupClosure lookupClosure = context.mock(LookupClosure.class);
	final Closure closure = context.mock(Closure.class);
	final MethodTargetFactory methodTargetFactory = context.mock(MethodTargetFactory.class);
	final LookupMethodTarget lookupTarget = context.mock(LookupMethodTarget.class);
	final ICalledMethodTarget calledMethodTarget = context.mock(ICalledMethodTarget.class);
	final PositionedTargetFactory positionedTargetFactory = context.mock(PositionedTargetFactory.class);
	final PositionedTarget positionedTarget = context.mock(PositionedTarget.class);
	final IScope scope = context.mock(IScope.class);
	final GenericTypedObject typedObject = new GenericTypedObject(subject,lookupClosure,methodTargetFactory);
	final RuntimeContextInternal runtime = new RuntimeContextContainer();

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
		void nullarySpecial(DoAction action);
		void unaryPostSpecial(DoAction action, int i);
		void binaryPreSpecial(int i, int j, DoAction action);
		void binaryPostSpecial(DoAction action, int i, int j);
	}
	interface Sut {
		void m();
	}
	
	@Test
	public void doNotFindActionSpecialMethod() throws Exception {
		String[] cells = {"unknown"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,positionedTargetFactory,runtime);
		assertThat(targets.isEmpty(),is(true));
	}
	@Test
	public void findNullaryActionSpecialMethod() throws Exception {
//		context.checking(new Expectations() {{ // -- This fails with a NullPointerException
//			oneOf(positionedTargetFactory).create(with(any(Method.class)),(String)with(equalTo("m")),(Integer)with(equalTo(0)));
//			  will(returnValue(positionedTarget));
//		}});
		PositionedTargetFactory factory = new PositionedTargetFactory(){
			@Override
			public PositionedTarget create(Method method, int from, int upTo) {
				assertThat(method.getName(),is("nullarySpecial"));
				assertThat(from,is(1));
				assertThat(upTo,is(2));
				return positionedTarget;
			}
		};
		String[] cells = {"nullarySpecial","m"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,factory,runtime);
		assertThat(targets.size(),is(1));
	}
	@Test
	public void findNullaryReversedActionSpecialMethod() throws Exception {
		PositionedTargetFactory factory = new PositionedTargetFactory(){
			@Override
			public PositionedTarget create(Method method, int from, int upTo) {
				assertThat(method.getName(),is("nullarySpecial"));
				assertThat(from,is(0));
				assertThat(upTo,is(1));
				return positionedTarget;
			}
		};
		String[] cells = {"m","nullarySpecial"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,factory,runtime);
		assertThat(targets.size(),is(1));
	}
	@Test
	public void findUnaryPostFixActionSpecialMethod() throws Exception {
		PositionedTargetFactory factory = new PositionedTargetFactory(){
			@Override
			public PositionedTarget create(Method method, int from, int upTo) {
				assertThat(method.getName(),is("unaryPostSpecial"));
				assertThat(from,is(0));
				assertThat(upTo,is(2));
				return positionedTarget;
			}
		};
		String[] cells = {"m","2","unaryPostSpecial", "1"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,factory,runtime);
		assertThat(targets.size(),is(1));
	}
	@Test
	public void findBinaryPreFixActionSpecialMethod() throws Exception {
		PositionedTargetFactory factory = new PositionedTargetFactory(){
			@Override
			public PositionedTarget create(Method method, int from, int upTo) {
				assertThat(method.getName(),is("binaryPreSpecial"));
				assertThat(from,is(4));
				assertThat(upTo,is(6));
				return positionedTarget;
			}
		};
		String[] cells = {"binary", "1", "pre special", "2", "m","a"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,factory,runtime);
		assertThat(targets.size(),is(1));
	}
//	@Test
//	public void findBinaryPreFixActionSpecialMethodWithTrailingKeyword() throws Exception {
//		PositionedTargetFactory factory = new PositionedTargetFactory(){
//			@Override
//			public PositionedTarget create(Method method, int from, int upTo) {
//				assertThat(method.getName(),is("binaryPreSpecial"));
//				assertThat(from,is(5));
//				assertThat(upTo,is(7));
//				return positionedTarget;
//			}
//		};
//		String[] cells = {"binary", "1", "pre", "2", "special", "m","a"};
//		assertThat(typedObject.findActionSpecialMethod(cells,factory).isFound(),is(true));
//	}
	@Test
	public void findBinaryPostFixActionSpecialMethod() throws Exception {
		PositionedTargetFactory factory = new PositionedTargetFactory(){
			@Override
			public PositionedTarget create(Method method, int from, int upTo) {
				assertThat(method.getName(),is("binaryPostSpecial"));
				assertThat(from,is(0));
				assertThat(upTo,is(2));
				return positionedTarget;
			}
		};
		String[] cells = {"m","a", "binary", "1", "post special", "2"};
		List<PositionedTarget> targets = typedObject.findActionSpecialMethods(cells,factory,runtime);
		assertThat(targets.size(),is(1));
	}
//	@Test
//	public void findBinaryPostFixActionSpecialMethodWithTrailingKeyword() throws Exception {
//		PositionedTargetFactory factory = new PositionedTargetFactory(){
//			@Override
//			public PositionedTarget create(Method method, int from, int upTo) {
//				assertThat(method.getName(),is("binaryPostSpecial"));
//				assertThat(from,is(0));
//				assertThat(upTo,is(2));
//				return positionedTarget;
//			}
//		};
//		String[] cells = {"m","a", "binary", "1", "post", "2", "special"};
//		assertThat(typedObject.findActionSpecialMethod(cells,factory).isFound(),is(true));
//	}
}
