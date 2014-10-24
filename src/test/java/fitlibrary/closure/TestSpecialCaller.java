/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.closure;

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
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.workflow.caller.SpecialCaller;
import fitlibrary.traverse.workflow.caller.TwoStageSpecial;

@RunWith(JMock.class)
public class TestSpecialCaller {
	Mockery context = new Mockery();
	Evaluator evaluator = context.mock(Evaluator.class);
	LookupMethodTarget lookupMethodTarget  = context.mock(LookupMethodTarget.class);
	Row row = context.mock(Row.class);
	TwoStageSpecial lazySpecial = context.mock(TwoStageSpecial.class);
	ICalledMethodTarget specialMethod = context.mock(ICalledMethodTarget.class);
	TestResults testResults = TestResultsFactory.testResults();
	
	@Test
	public void invalidAsMethodUnknown() {
		context.checking(new Expectations() {{
			allowing(row).text(0,evaluator);will(returnValue("a"));
			one(lookupMethodTarget).findSpecialMethod(evaluator,"a");will(returnValue(null));
		}});
		SpecialCaller specialCaller = new SpecialCaller(row,evaluator,lookupMethodTarget);
		assertThat(specialCaller.isValid(), is(false));
	}
	@Test
	public void invalidAsSpecialMethodThrowsException() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).text(0,evaluator);will(returnValue("a"));
			one(lookupMethodTarget).findSpecialMethod(evaluator,"a");will(returnValue(specialMethod));
			allowing(specialMethod).getReturnType();will(returnValue(TwoStageSpecial.class));
			one(specialMethod).invoke(with(any(Object[].class)));will(throwException(new RuntimeException()));
		}});
		SpecialCaller specialCaller = new SpecialCaller(row,evaluator,lookupMethodTarget);
		assertThat(specialCaller.isValid(), is(false));
	}
	@Test
	public void validButNotLazy() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).text(0,evaluator);will(returnValue("a"));
			one(lookupMethodTarget).findSpecialMethod(evaluator,"a");will(returnValue(specialMethod));
			allowing(specialMethod).getReturnType();will(returnValue(Void.class));
			one(specialMethod).invoke(with(any(Object[].class)));will(returnValue("result"));
		}});
		SpecialCaller specialCaller = new SpecialCaller(row,evaluator,lookupMethodTarget);
		assertThat(specialCaller.isValid(), is(true));
		assertThat(specialCaller.run(row,testResults).getSubject(), is((Object)"result"));
	}
	@Test
	public void validButLazy() throws Exception {
		context.checking(new Expectations() {{
			allowing(row).text(0,evaluator);will(returnValue("a"));
			one(lookupMethodTarget).findSpecialMethod(evaluator,"a");will(returnValue(specialMethod));
			allowing(specialMethod).getReturnType();will(returnValue(TwoStageSpecial.class));
			one(specialMethod).invoke(with(any(Object[].class)));will(returnValue(lazySpecial));
			one(lazySpecial).run(testResults);
		}});
		SpecialCaller specialCaller = new SpecialCaller(row,evaluator,lookupMethodTarget);
		assertThat(specialCaller.isValid(), is(true));
		assertThat(specialCaller.run(row,testResults).getSubject(), is((Object)null));
	}
}
