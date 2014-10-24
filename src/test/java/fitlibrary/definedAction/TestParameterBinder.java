/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.definedAction;

import static fitlibrary.table.TableFactory.row;
import static fitlibrary.table.TableFactory.table;
import static fitlibrary.table.TableFactory.tables;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.dynamicVariable.DynamicVariables;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.matcher.TablesMatcher;
import fitlibrary.table.Tables;
import fitlibrary.utility.CollectionUtility;
import fitlibrary.utility.StringTablesPair;

@RunWith(JMock.class)
public class TestParameterBinder {
	Mockery context = new Mockery();
	protected DynamicVariables resolver = context.mock(DynamicVariables.class);
	List<String> formals = new ArrayList<String>();
	Tables tables = tables();
	ParameterBinder binder = new ParameterBinder("name",formals, tables, "fileName");

	@Test
	public void fileName() {
		assertThat(binder.getPageName(),is("fileName"));
		assertThat(binder.getCopyOfBody(),isTables(tables));
	}
	private Matcher<Tables> isTables(Tables expected) {
		return new TablesMatcher(expected, resolver);
	}
	@Test(expected=FitLibraryException.class)
	public void unknownParameter() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
		}});
		formals.add("A");
		formals.add("B");
		binder.verifyHeaderAgainstFormalParameters(row("a","b"),resolver);
	}
	@Test(expected=FitLibraryException.class)
	public void duplicateParameter() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
		}});
		formals.add("a");
		formals.add("B");
		binder.verifyHeaderAgainstFormalParameters(row("a","a"),resolver);
	}
	@Test(expected=FitLibraryException.class)
	public void wrongNumberOfFormalParameter() {
		formals.add("a");
		binder.verifyHeaderAgainstFormalParameters(row("a","a"),resolver);
	}
	@Test
	public void validParameterNames() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
			allowing(resolver).resolve("b"); will(returnValue(new StringTablesPair("b")));
		}});
		formals.add("a");
		formals.add("b");
		binder.verifyHeaderAgainstFormalParameters(row("a","b"),resolver);
	}
	@Test
	public void validParameterNamesInReverse() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
			allowing(resolver).resolve("b"); will(returnValue(new StringTablesPair("b")));
		}});
		formals.add("b");
		formals.add("a");
		binder.verifyHeaderAgainstFormalParameters(row("a","b"),resolver);
	}

	@Test(expected=FitLibraryException.class)
	public void wrongNumberOfActualParameters() {
		formals.add("a");
		binder.bindMulti(row("a","b"),row("1","extra"),resolver);
	}
	@Test
	public void bindMultiWorks() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
			allowing(resolver).resolve("b"); will(returnValue(new StringTablesPair("b")));
			allowing(resolver).resolve("1"); will(returnValue(new StringTablesPair("1")));
			allowing(resolver).resolve("2"); will(returnValue(new StringTablesPair("2")));
			one(resolver).putParameter("a", "1");
			one(resolver).putParameter("b", "2");
		}});
		formals.add("a");
		formals.add("b");
		binder.bindMulti(row("a","b"),row("1","2"),resolver);
	}
	@Test
	public void bindMultiWorksWithNestedTable() {
		final Tables inner = tables(table(row("x")));
		context.checking(new Expectations() {{
			allowing(resolver).resolve("a"); will(returnValue(new StringTablesPair("a")));
			allowing(resolver).resolve("1"); will(returnValue(new StringTablesPair("22",inner)));
			allowing(resolver).resolve("x"); will(returnValue(new StringTablesPair("x")));
			one(resolver).putParameter(with("a"), with(new TablesMatcher(inner, resolver)));
		}});
		formals.add("a");
		binder.bindMulti(row("a","b"),row("1"),resolver);
	}

	@Test
	public void bindUniWorks() {
		context.checking(new Expectations() {{
			allowing(resolver).resolve("1"); will(returnValue(new StringTablesPair("1")));
			allowing(resolver).resolve("2"); will(returnValue(new StringTablesPair("2")));
			one(resolver).putParameter("a", "1");
			one(resolver).putParameter("b", "2");
		}});
		formals.add("a");
		formals.add("b");
		binder.bindUni(actuals("1","2"),resolver);
	}
	@Test
	public void bindUniWorksWithNestedTable() {
		final Tables inner = tables(table(row("x")));
		context.checking(new Expectations() {{
			allowing(resolver).resolve("x"); will(returnValue(new StringTablesPair("x")));
			one(resolver).putParameter(with("a"), with(new TablesMatcher(inner, resolver)));
		}});
		formals.add("a");
		binder.bindUni(actuals(inner),resolver);
	}
	
	private List<Object> actuals(Object...objects) {
		return CollectionUtility.list(objects);
	}
}
