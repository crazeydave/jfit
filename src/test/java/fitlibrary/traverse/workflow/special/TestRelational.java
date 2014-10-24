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

import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.parse.BadNumberException;
import fitlibrary.flow.GlobalActionScope;
import fitlibrary.special.DoAction;
import fitlibrary.tableProxy.CellProxy;

@RunWith(JMock.class)
public class TestRelational {
	Mockery context = new Mockery();
	DoAction action = context.mock(DoAction.class);
	CellProxy cellProxy = context.mock(CellProxy.class);
	GlobalActionScope globalActionScope = new GlobalActionScope();
	
	public void actual(final Object value) throws Exception {
		context.checking(new Expectations() {{
			oneOf(action).run(); will(returnValue(value));
			allowing(action).cellAt(1); will(returnValue(cellProxy));
		}});
	}
	@Test
	public void lessThanPasses() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual(0);
		globalActionScope.lessThan(action,1);
	}
	@Test
	public void lessThanFails() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("100");
		}});
		actual(100L);
		globalActionScope.lessThan(action,-1L);
	}
	@Test
	public void lessThanFailsOnEquality() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("100");
		}});
		actual(100L);
		globalActionScope.lessThan(action,100L);
	}

	@Test
	public void lessThanEqualsPasses() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual(0);
		globalActionScope.lessThanEquals(action,1);
	}
	@Test
	public void lessThanEqualsPassesOnEquality() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual((byte)5);
		globalActionScope.lessThanEquals(action,(byte)5);
	}
	@Test
	public void lessThanEqualsThanFails() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("100");
		}});
		actual(100L);
		globalActionScope.lessThanEquals(action,-1L);
	}

	@Test
	public void greaterThanPasses() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual("ab");
		globalActionScope.greaterThan(action,"a");
	}
	@Test
	public void greaterThanFails() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("5");
		}});
		actual(new Short("5"));
		globalActionScope.greaterThan(action,new Short("10"));
	}
	@Test
	public void greaterThanFailsOnEquality() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("true");
		}});
		actual(true);
		globalActionScope.greaterThan(action,true);
	}

	@Test
	public void greaterThanEqualsPasses() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual(0);
		globalActionScope.greaterThanEquals(action,-1);
	}
	@Test
	public void greaterThanEqualsPassesOnEquality() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).pass();
		}});
		actual((byte)5);
		globalActionScope.greaterThanEquals(action,(byte)5);
	}
	@Test
	public void greaterThanEqualsThanFails() throws Exception {
		context.checking(new Expectations() {{
			one(cellProxy).fail("100");
		}});
		actual(100L);
		globalActionScope.greaterThanEquals(action,200L);
	}

	@Test(expected=ClassCastException.class)
	public void valueTypeIncompatibility() throws Exception {
		actual(100L);
		globalActionScope.lessThan(action,-1);
	}
	@Test(expected=FitLibraryException.class)
	public void nullResult() throws Exception {
		actual(null);
		globalActionScope.lessThan(action,-1);
	}
	@Test(expected=FitLibraryException.class)
	public void nullExpected() throws Exception {
		globalActionScope.lessThan(action,null);
	}
	@Test(expected=FitLibraryException.class)
	public void expectedNotComparable() throws Exception {
		globalActionScope.lessThan(action,new NotComparable());
	}
	@Test(expected=FitLibraryException.class)
	public void resultNotComparable() throws Exception {
		actual(new NotComparable());
		globalActionScope.lessThan(action,0);
	}
	static class NotComparable {
		//
	}
	@Test(expected=BadNumberException.class)
	public void errorWithException() throws Exception {
		context.checking(new Expectations() {{
			one(action).run(); will(throwException(new BadNumberException()));
		}});
		globalActionScope.lessThan(action,-1);
	}
}
