/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.dynamicVariable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TestLocalDynamicVariables {
	Mockery context = new Mockery();
	protected DynamicVariables vars = context.mock(DynamicVariables.class);

	@Test
	public void global() {
		context.checking(new Expectations() {{
			one(vars).get("k"); will(returnValue("v"));
		}});
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		assertThat(locals.get("k").toString(),is("v"));
	}
	@Test
	public void put() {
		context.checking(new Expectations() {{
			one(vars).put("k","v");
			one(vars).get("k"); will(returnValue("v"));
		}});
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		locals.put("k", "v");
		assertThat(locals.get("k").toString(),is("v"));
	}
	@Test
	public void parameterOverrides() {
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		locals.putParameter("k", "v");
		assertThat(locals.get("k").toString(),is("v"));
	}
	@Test
	public void canChangeParameterLocally() {
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		locals.putParameter("k", "v");
		locals.put("k", "VV");
		assertThat(locals.get("k").toString(),is("VV"));
	}
	@Test
	public void addPropertiesFromFile() {
		context.checking(new Expectations() {{
			one(vars).addFromPropertiesFile("k"); will(returnValue(true));
		}});
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		assertThat(locals.addFromPropertiesFile("k"),is(true));
	}
	@Test
	public void addPropertiesFromUnicodeFile() throws IOException {
		context.checking(new Expectations() {{
			one(vars).addFromUnicodePropertyFile("k");
		}});
		LocalDynamicVariables locals = new LocalDynamicVariables(vars);
		locals.addFromUnicodePropertyFile("k");
	}
}
