/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryShowException;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.NotRejectedException;
import fitlibrary.exception.FitLibraryShowException.Show;
import fitlibrary.exception.parse.BadNumberException;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.special.DoAction;

@RunWith(JMock.class)
public class TestNot {
	Mockery context = new Mockery();
	DoAction action = context.mock(DoAction.class);
	GlobalActionScope globalActionScope = new GlobalActionScope();
	
	@Test
	public void trueWithFalseResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(returnValue(false));
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
	@Test
	public void trueWithException() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(throwException(new BadNumberException()));
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
	@Test
	public void falseWithTrueResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(returnValue(true));
		}});
		assertThat(globalActionScope.not(action),is(false));
	}
	@Test(expected=NotRejectedException.class)
	public void exceptionWithNullResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(returnValue(null));
		}});
		globalActionScope.not(action);
	}
	@Test
	public void trueWithShowException() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(throwException(new InvocationTargetException(new FitLibraryShowException(new Show("abc")))));
			one(action).show("abc");
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
	@Test
	public void trueWithIgnoredException() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(throwException(new IgnoredException()));
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
	@Test
	public void trueAndShownWithIgnoredExceptionWithEmbedded() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(throwException(new IgnoredException(new BadNumberException())));
			one(action).show("Invalid Number");
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
	@Test
	public void trueWithOtherException() throws Exception {
		context.checking(new Expectations() {{
			one(action).runWithNoColouring(); will(throwException(new RuntimeException()));
		}});
		assertThat(globalActionScope.not(action),is(true));
	}
}
