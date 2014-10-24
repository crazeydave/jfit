/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.runtime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class TestRuntimeContext {
	@Test(expected=RuntimeException.class)
	public void cannotPutParameterWithGlobal() {
		new RuntimeContextContainer().getDynamicVariables().putParameter("k", "v");
	}
	@Test
	public void canPutParameterOncePushedLocal() {
		RuntimeContextInternal runtimeContext = new RuntimeContextContainer();
		runtimeContext.pushLocalDynamicVariables();
		runtimeContext.getDynamicVariables().putParameter("k", "v");
		assertThat(runtimeContext.getDynamicVariables().get("k"),is((Object)"v"));
	}
	@Test
	public void canPopLocalOncePushedLocal() {
		RuntimeContextInternal runtimeContext = new RuntimeContextContainer();
		runtimeContext.pushLocalDynamicVariables();
		runtimeContext.popLocalDynamicVariables();
	}
	@Test(expected=RuntimeException.class)
	public void cannotPopLocalIfNotPushedLocal() {
		RuntimeContextInternal runtimeContext = new RuntimeContextContainer();
		runtimeContext.popLocalDynamicVariables();
	}
	@Test
	public void parameterBindingsAreDiscardedOnPop() {
		RuntimeContextInternal runtimeContext = new RuntimeContextContainer();
		runtimeContext.pushLocalDynamicVariables();
		runtimeContext.getDynamicVariables().putParameter("k", "v");
		assertThat(runtimeContext.getDynamicVariables().get("k"),is((Object)"v"));
		runtimeContext.popLocalDynamicVariables();
		assertThat(runtimeContext.getDynamicVariables().get("k") == null,is(true));
	}
	@Test
	public void previousParameterBindingsAreAvailableOnPop() {
		RuntimeContextInternal runtimeContext = new RuntimeContextContainer();
		runtimeContext.pushLocalDynamicVariables();
		runtimeContext.getDynamicVariables().putParameter("k", "v");
		runtimeContext.pushLocalDynamicVariables();
		assertThat(runtimeContext.getDynamicVariables().get("k"),is((Object)"v"));
		runtimeContext.getDynamicVariables().putParameter("k", "V");
		assertThat(runtimeContext.getDynamicVariables().get("k"),is((Object)"V"));
		runtimeContext.popLocalDynamicVariables();
		assertThat(runtimeContext.getDynamicVariables().get("k"),is((Object)"v"));
		runtimeContext.popLocalDynamicVariables();
		assertThat(runtimeContext.getDynamicVariables().get("k") == null,is(true));
	}
}
