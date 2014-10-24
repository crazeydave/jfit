/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.IgnoredException;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.special.DoAction;

@RunWith(JMock.class)
public class TestShowEscaped {
	Mockery context = new Mockery();
	DoAction action = context.mock(DoAction.class);
	GlobalActionScope globalActionScope = new GlobalActionScope();
	
	@Test
	public void textIsShown() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue("nz"));
			one(action).show("<pre>nz</pre>");
		}});
		globalActionScope.showEscaped(action);
	}
	@Test
	public void textWithTagIsShownEscaped() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue("<nz>"));
			one(action).show("<pre>&lt;nz></pre>");
		}});
		globalActionScope.showEscaped(action);
	}
	@Test
	public void objectIsShown() throws Exception {
		final BigDecimal number = new BigDecimal(4444444);
		context.checking(new Expectations() {{
			one(action).run();
			will(returnValue(number));
			one(action).show("<pre>4444444</pre>");
		}});
		globalActionScope.showEscaped(action);
	}
	@Test
	public void nothingShownWithNullResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue(null));
		}});
		globalActionScope.showEscaped(action);
	}
	@Test(expected=Exception.class)
	public void exceptionIsPassedOn() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(throwException(new IgnoredException()));
		}});
		globalActionScope.showEscaped(action);
	}
}
