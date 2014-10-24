/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.NotRejectedException;
import fitlibrary.exception.parse.BadNumberException;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.special.DoAction;

@RunWith(JMock.class)
public class TestNotTrue {
	Mockery context = new Mockery();
	DoAction action = context.mock(DoAction.class);
	GlobalActionScope globalActionScope = new GlobalActionScope();
	
	@Test
	public void trueWithFalseResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue(false));
		}});
		assertThat(globalActionScope.notTrue(action),is(true));
	}
	@Test(expected=BadNumberException.class)
	public void failsWithException() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(throwException(new BadNumberException()));
		}});
		globalActionScope.notTrue(action);
	}
	@Test
	public void falseWithTrueResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue(true));
		}});
		assertThat(globalActionScope.notTrue(action),is(false));
	}
	@Test(expected=NotRejectedException.class)
	public void exceptionWithNullResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue(null));
		}});
		globalActionScope.notTrue(action);
	}
}
