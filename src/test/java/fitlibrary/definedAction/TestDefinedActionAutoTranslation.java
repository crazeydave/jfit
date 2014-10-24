/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.definedAction;

import static fitlibrary.utility.CollectionUtility.list;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fitlibrary.dynamicVariable.GlobalDynamicVariables;
import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.matcher.TablesMatcher;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;

public class TestDefinedActionAutoTranslation {
	VariableResolver resolver = new GlobalDynamicVariables();
	
	@Before
	public void useListsFactory() {
		TableFactory.useOnLists(true);
	}
	@After
	public void stopUsingListsFactory() {
		TableFactory.pop();
	}
	@Test public void noNeedForAutoTranslationWithNoParameters() {
		Tables body = TableFactory.tables().add(TableFactory.table(TableFactory.row("a","b","c")));
		List<String> list = list();
		assertThat(DefinedActionParameterTranslation.needToTranslateParameters(list,body),is(false));
	}
	@Test public void noNeedForAutoTranslationWithOneParameterAlreadyInAtStyle() {
		Tables body = TableFactory.tables().add(TableFactory.table(TableFactory.row("@{A}","b","c")));
		List<String> list = list("A");
		assertThat(DefinedActionParameterTranslation.needToTranslateParameters(list,body),is(false));
	}
	@Test public void autoTranslationWithOneParameter() {
		Tables body = tables("A","b","A c");
		List<String> list = list("A");
		assertThat(DefinedActionParameterTranslation.needToTranslateParameters(list,body),is(true));
		assertThat(DefinedActionParameterTranslation.translateParameters(list,body),is(list("paRameRer__0")));
		assertThat(body,new TablesMatcher(tables("@{paRameRer__0}","b","@{paRameRer__0} c"),resolver));
	}
	@Test public void autoTranslationWithOneParameterWithRegExp() {
		Tables body = tables("A.* bc","AAB","c");
		assertThat(DefinedActionParameterTranslation.translateParameters(list("A.*"),body),is(list("paRameRer__0")));
		assertThat(body,new TablesMatcher(tables("@{paRameRer__0} bc","AAB","c"),resolver));
	}
	@Test public void autoTranslationWithTwoParametersIsUnnecessary() {
		Tables body = tables("@{A} bc","@{A}@{A}@{B}","c@{B}c");
		List<String> list = list("A","B");
		assertThat(DefinedActionParameterTranslation.needToTranslateParameters(list,body),is(false));
	}
	@Test public void autoTranslationWithTwoParameters() {
		Tables body = tables("A bc","AAB","cBc");
		List<String> list = list("A","B");
		assertThat(DefinedActionParameterTranslation.translateParameters(list,body),is(list("paRameRer__0","paRameRer__1")));
		assertThat(body,new TablesMatcher(tables("@{paRameRer__0} bc","@{paRameRer__0}@{paRameRer__0}@{paRameRer__1}","c@{paRameRer__1}c"),resolver));
	}
	@Test public void autoTranslationWithTwoParametersWithOneASubstringOfTheOther() {
		Tables body = tables("A bc AB","AAB","cBc");
		List<String> list = list("A","AB");
		assertThat(DefinedActionParameterTranslation.translateParameters(list,body),is(list("paRameRer__1","paRameRer__0")));
		assertThat(body,new TablesMatcher(tables("@{paRameRer__1} bc @{paRameRer__0}","@{paRameRer__1}@{paRameRer__0}","cBc"),resolver));
	}
	
	private Tables tables(String... ss) {
		return TableFactory.tables().add(TableFactory.table(TableFactory.row(ss)));
	}
}
