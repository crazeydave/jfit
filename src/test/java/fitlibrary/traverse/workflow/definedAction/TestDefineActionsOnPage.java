/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow.definedAction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fitlibrary.definedAction.DefineActionsOnPage;
import fitlibrary.definedAction.DefinedActionsRepository;
import fitlibrary.global.TemporaryPlugBoardForRuntime;
import fitlibrary.runtime.RuntimeContextContainer;

public class TestDefineActionsOnPage {
	RuntimeContextContainer runtime = new RuntimeContextContainer();
	DefinedActionsRepository definedActions = TemporaryPlugBoardForRuntime.definedActionsRepository();
	protected File fitNesseDir;
	
	@Before
	public void setFitNesseDirectory() {
		fitNesseDir = new File("fitnesse");
		assertThat("This test relies on files in the fitnesse directory - which cannot be found: current directory is "+
				new File(".").getAbsolutePath(), fitNesseDir.exists(), is(true));
	}
	
	@Test public void actionsAreDefinedThroughFileSystem() throws Exception {
		String pageName = ".FitLibrary.SpecifiCations.PlainTextInsteadOfTables.DefinedActions";
		DefineActionsOnPage defineActionsOnPage = new DefineActionsOnPage(pageName,runtime) {
			@Override
			protected File fitNesseDiry() {
				return fitNesseDir;
			}
		};
		defineActionsOnPage.process();
		assertThat(definedActions.lookupByCamel("addressIsAt", 1),is(notNullValue()));
		assertThat(definedActions.lookupByCamel("addressIs", 1),is(nullValue()));
		assertThat(definedActions.lookupByClassByCamel("Person", "addressIs", 1, runtime),is(notNullValue()));
	}
	@Test public void actionsAreDefinedThroughFileSystemExample2() throws Exception {
		String pageName = ".FitLibrary.SpecifiCations.DefinedActions.BasedOnClass.DefinedActions";
		DefineActionsOnPage defineActionsOnPage = new DefineActionsOnPage(pageName,runtime) {
			@Override
			protected File fitNesseDiry() {
				return fitNesseDir;
			}
		};
		defineActionsOnPage.process();
		assertThat(definedActions.lookupByCamel("nameIs", 1),is(notNullValue()));
		assertThat(definedActions.lookupByClassByCamel("Person", "nameIs", 1, runtime),is(notNullValue()));
	}
}
