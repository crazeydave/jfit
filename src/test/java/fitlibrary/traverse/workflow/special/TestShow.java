/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.traverse.workflow.special;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.IgnoredException;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.special.DoAction;
import fitlibrary.specify.Point;

@RunWith(JMock.class)
public class TestShow {
	Mockery context = new Mockery();
	DoAction action = context.mock(DoAction.class);
	GlobalActionScope globalActionScope = new GlobalActionScope();
	
	@Test
	public void textIsShown() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue("nz"));
			one(action).showResult("nz");
		}});
		globalActionScope.show(action);
	}
	@Test
	public void objectIsShown() throws Exception {
		final Point point = new Point();
		context.checking(new Expectations() {{
			one(action).run();
			will(returnValue(point));
			one(action).showResult(point);
		}});
		globalActionScope.show(action);
	}
	@Test
	public void nothingShownWithNullResult() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(returnValue(null));
		}});
		globalActionScope.show(action);
	}
	@Test(expected=Exception.class)
	public void exceptionIsPassedOn() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(throwException(new IgnoredException()));
		}});
		globalActionScope.show(action);
	}
}
