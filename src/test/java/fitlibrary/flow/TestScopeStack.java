/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.suite.SuiteFixture;
import fitlibrary.traverse.RuntimeContextual;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.traverse.workflow.FlowEvaluator;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.CollectionUtility;
import fitlibraryGeneric.typed.GenericTypedObject;

@RunWith(JMock.class)
public class TestScopeStack {
	final Mockery context = new Mockery();
	final Object something = "something";
	final TypedObject someTypedObject = new GenericTypedObject(something);
	final Object other = 12;
	final TypedObject otherTypedObject = new GenericTypedObject(other);
	final DoTraverse doTraverse = new DoTraverse(something);
	final TypedObject doTypedObjectWithSomeSut = new GenericTypedObject(doTraverse);
	final TypedObject suiteTypedObject = new GenericTypedObject(new SuiteFixture());
	final List<TypedObject> emptyList = new ArrayList<TypedObject>();
	final RuntimeContextual global = context.mock(RuntimeContextual.class,"global");
	final TypedObject globalTypedObject = new GenericTypedObject(global);
	final FlowEvaluator flowEvaluator = context.mock(FlowEvaluatorThatIsRuntimeContextual.class);
	final RuntimeContextInternal runtime = context.mock(RuntimeContextInternal.class);
	final ScopeStack scopeStack = new ScopeStack(flowEvaluator,globalTypedObject);

	@Before
	public void generalExpectations() {
		context.checking(new Expectations() {{
			allowing(flowEvaluator).getSystemUnderTest();  will(returnValue(null));
			allowing(global).getSystemUnderTest(); will(returnValue(null));
		}});
	}
	@Test
	public void initiallyObjectsInScope() {
		assertThat(scopeStack.objectsForLookup(),hasSubjects(global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList()));
		assertThat(scopeStack.poppedAtEndOfTable(),is(emptyList));
	}
	@Test
	public void onePushAffectsObjectsInScope() {
		scopeStack.push(someTypedObject);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
		assertThat(scopeStack.poppedAtEndOfTable(),is(emptyList));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(list(someTypedObject)));
	}
	@Test
	public void onePushWithSutAffectsObjectsInScope() {
		scopeStack.push(doTypedObjectWithSomeSut);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(doTraverse,something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
		assertThat(scopeStack.poppedAtEndOfTable(),is(emptyList));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(list(doTypedObjectWithSomeSut)));
	}
	@Test
	public void sameOnePushedTwiceAffectsObjectsInScope() {
		scopeStack.push(someTypedObject);
		scopeStack.push(someTypedObject);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
		assertThat(scopeStack.poppedAtEndOfTable(),is(list(someTypedObject)));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(list(someTypedObject)));
	}
	@Test
	public void onePushAndPopAtEndOfStorytestAffectsObjectsInScope() {
		scopeStack.push(someTypedObject);
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(list(someTypedObject)));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(emptyList));
		assertThat(scopeStack.objectsForLookup(),hasSubjects(global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList()));
	}
	@Test
	public void twoPushesAffectsObjectsInScope() {
		scopeStack.push(someTypedObject);
		scopeStack.push(otherTypedObject);
		assertThat(scopeStack.objectsForLookup(),
				hasSubjects(other,something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(other.getClass(),something.getClass())));
	}
	@Test
	public void twoPushesAndTwoPopsAffectsObjectsInScope() {
		scopeStack.push(someTypedObject);
		scopeStack.push(otherTypedObject);
		assertThat(scopeStack.poppedAtEndOfTable(),is(list(otherTypedObject)));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(list(someTypedObject)));
	}
	@Test
	public void pushSuiteEvaluatorAffectsObjectsInScope() {
		scopeStack.push(suiteTypedObject);
		assertThat(scopeStack.objectsForLookup(),
				hasSubjects(suiteTypedObject.getSubject(),global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList()));
		assertThat(scopeStack.poppedAtEndOfTable(),is(emptyList));
		assertThat(scopeStack.poppedAtEndOfStorytest(),is(emptyList));
	}
	@Test
	public void oneExtraGlobalAffectsObjectsInScope() {
		final RuntimeContextual global2 = context.mock(RuntimeContextual.class,"global2");
		TypedObject globalTypedObject2 = new GenericTypedObject(global2);
		context.checking(new Expectations() {{
			allowing(global2).getSystemUnderTest(); will(returnValue(null));
		}});
		scopeStack.addGlobal(globalTypedObject2);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(global,global2,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(global2.getClass())));
		assertThat(scopeStack.poppedAtEndOfTable(),is(emptyList));
	}
	@Test
	public void restoreStateAfterNoChange() {
		scopeStack.push(someTypedObject);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		IScopeState currentState = scopeStack.currentState();
		assertThat(currentState.restore().isEmpty(),is(true));
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
	}
	@Test
	public void restoreStateAfterChange() {
		scopeStack.push(someTypedObject);
		IScopeState currentState = scopeStack.currentState();
		scopeStack.push(otherTypedObject);
		assertThat(currentState.restore(),is(list(otherTypedObject)));
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
	}
	@Test(expected=FitLibraryException.class)
	public void selectUnknown() {
		scopeStack.select("unknown");
	}
	@Test
	public void addNamedObjectAndSelect() {
		scopeStack.addNamedObject("x",someTypedObject);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));

		scopeStack.select("x");
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass())));
	}
	@Test
	public void addTwoNamedObjectAndSelectBetweenThem() {
		scopeStack.addNamedObject("x",someTypedObject);
		scopeStack.addNamedObject("y",otherTypedObject);
		assertThat(scopeStack.objectsForLookup(),hasSubjects(something,other,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(something.getClass(),other.getClass())));

		scopeStack.select("y");
		assertThat(scopeStack.objectsForLookup(),hasSubjects(other,something,global,flowEvaluator));
		assertThat(scopeStack.possibleClasses(),is(classList(other.getClass(),something.getClass())));
	}
	@Test
	public void switchRuntime() {
		context.checking(new Expectations() {{
			oneOf(flowEvaluator).setRuntimeContext(runtime);
			oneOf(global).setRuntimeContext(runtime);
		}});
		scopeStack.switchRuntime(runtime);
	}
	@Test
	public void switchRuntimeWithExtraGlobal() {
		final RuntimeContextual global2 = context.mock(RuntimeContextual.class,"global2");
		TypedObject globalTypedObject2 = new GenericTypedObject(global2);
		context.checking(new Expectations() {{
			allowing(global2).getSystemUnderTest(); will(returnValue(null));
			oneOf(flowEvaluator).setRuntimeContext(runtime);
			oneOf(global).setRuntimeContext(runtime);
			oneOf(global2).setRuntimeContext(runtime);
		}});
		scopeStack.addGlobal(globalTypedObject2);
		scopeStack.switchRuntime(runtime);
	}
	@Test
	public void initiallyNotAbandoned() {
		assertThat(scopeStack.isAbandon(),is(false));
	}
	@Test
	public void setNotAbandoned() {
		scopeStack.setAbandon(true);
		assertThat(scopeStack.isAbandon(),is(true));
	}
	@Test
	public void initiallyNotStopOnError() {
		assertThat(scopeStack.isStopOnError(),is(false));
	}
	@Test
	public void setNotStopOnError() {
		scopeStack.setStopOnError(true);
		assertThat(scopeStack.isStopOnError(),is(true));
	}

	static Matcher<List<TypedObject>> hasSubjects(Object... expected) {
		return new HasTypedSubjectsMatcher(expected);
	}
	private <T> List<T> list(T... ss) {
		return CollectionUtility.list(ss);
	}
	private List<Class<?>> classList(Class<?>... classes) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		for (Class<?> type : classes)
			results.add(type);
		return results;
	}
	public interface FlowEvaluatorThatIsRuntimeContextual extends FlowEvaluator {
		//
	}
}
