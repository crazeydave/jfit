/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.runResults.TestResults;
import fitlibrary.runResults.TestResultsFactory;
import fitlibrary.table.Row;
import fitlibrary.traverse.DomainAdapter;

@RunWith(JMock.class)
public class TestSetUpTearDownCache {
	Mockery context = new Mockery();
	SetUpTearDownCache setUpTearDown = new SetUpTearDownCache();
	TestInterface object = context.mock(TestInterface.class,"object");
	TestInterface sut = context.mock(TestInterface.class,"sut");
	TestResults testResults = TestResultsFactory.testResults();
	Row row = context.mock(Row.class);
	
	@Test
	public void suiteSetUp() {
		context.checking(new Expectations() {{
			oneOf(object).suiteSetUp();
		}});
		setUpTearDown.callSuiteSetUp(object, row, testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void suiteSetUpWithException() {
		context.checking(new Expectations() {{
			oneOf(object).suiteSetUp(); will(throwException(new RuntimeException("error")));
			oneOf(row).error(with(testResults), with(any(Exception.class)));
		}});
		setUpTearDown.callSuiteSetUp(object, row, testResults);
	}
	@Test
	public void suiteTearDown() {
		context.checking(new Expectations() {{
			oneOf(object).suiteTearDown();
		}});
		setUpTearDown.callSuiteTearDown(object,testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void suiteTearDownWithException() {
		context.checking(new Expectations() {{
			oneOf(object).suiteTearDown(); will(throwException(new RuntimeException("error")));
		}});
		setUpTearDown.callSuiteTearDown(object,testResults);
		assertThat(testResults.problems(),is(true));
	}
	@Test
	public void setUp() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(null));
			oneOf(object).setUp();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void setUpWithSut() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(sut));
			oneOf(object).setUp();
			allowing(sut).getSystemUnderTest(); will(returnValue(null));
			oneOf(sut).setUp();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void setUpWithException() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(sut));
			oneOf(object).setUp(); will(throwException(new RuntimeException("error")));
			oneOf(row).error(with(testResults), with(any(Exception.class)));
			allowing(sut).getSystemUnderTest(); will(returnValue(null));
			oneOf(sut).setUp();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
	}
	@Test
	public void setUpTearDown() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(null));
			oneOf(object).setUp();
			oneOf(object).tearDown();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		setUpTearDown.callTearDownOnSutChain(object, row, testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void setUpTearDownWithSut() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(sut));
			oneOf(object).setUp();
			allowing(sut).getSystemUnderTest(); will(returnValue(null));
			oneOf(sut).setUp();
			oneOf(object).tearDown();
			oneOf(sut).tearDown();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		setUpTearDown.callTearDownOnSutChain(object, row, testResults);
		assertThat(testResults.problems(),is(false));
	}
	@Test
	public void setUpTearDownWithException() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(null));
			oneOf(object).setUp();
			oneOf(object).tearDown(); will(throwException(new RuntimeException("error")));
			oneOf(row).error(with(testResults), with(any(Exception.class)));
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		setUpTearDown.callTearDownOnSutChain(object, row, testResults);
	}
	@Test
	public void onFailure() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(null));
			oneOf(object).setUp();
			oneOf(object).onFailure();
			oneOf(object).tearDown();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		testResults.fail();
		setUpTearDown.callTearDownOnSutChain(object, row, testResults);
	}
	@Test
	public void onFailureThrowsException() {
		context.checking(new Expectations() {{
			allowing(object).getSystemUnderTest(); will(returnValue(null));
			oneOf(object).setUp();
			oneOf(object).onFailure(); will(throwException(new RuntimeException("error")));
			oneOf(row).error(with(testResults), with(any(Exception.class)));
			oneOf(object).tearDown();
		}});
		setUpTearDown.callSetUpOnSutChain(object, row, testResults);
		testResults.fail();
		setUpTearDown.callTearDownOnSutChain(object, row, testResults);
	}

	interface TestInterface extends DomainAdapter {
		void suiteSetUp();
		void suiteTearDown();
		void setUp();
		void tearDown();
		void onFailure();
	}
}
