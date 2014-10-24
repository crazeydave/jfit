/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.dynamicVariable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import fitlibrary.matcher.TablesMatcher;
import static fitlibrary.table.TableFactory.*;
import fitlibrary.table.Tables;
import fitlibrary.utility.Pair;

public class TestDynamicVariablesMap {
	DynamicVariablesMap vars = new GlobalDynamicVariables();
	DynamicVariablesMap emptyVars = new GlobalDynamicVariables();
	
	@Test public void noSubstitution() {
		verify(vars.resolve("ab"), "ab");
	}
	@Test public void oneStringSubstitution() {
		vars.put("ab", "AB");
		verify(vars.resolve("@{ab}"), "AB");
	}
	@Test public void twoStringSubstitutions() {
		vars.put("ab", "AB");
		vars.put("cd", "CD");
		verify(vars.resolve("@{ab}+@{cd}"), "AB+CD");
	}
	@Test public void xmlSubstitutions() {
		vars.put("ab", "<a>b</a>");
		vars.put("cd", "<c a='b'/>");
		verify(vars.resolve("@{ab}+@{cd}"), "&lt;a>b&lt;/a>+&lt;c a='b'/>");
	}

	@Test public void oneTablesSubstitutions() {
		Tables tables = tables(table(row("a","b","c")));
		vars.put("ab", tables);
		verify(vars.resolve("x@{ab}y"), "xy", tables);
	}
	@Test public void twoTablesSubstitutions() {
		Tables tables1 = tables(table(row("a","b","c")));
		vars.put("ab", tables1);
		Tables tables2 = tables(table(row("c","d")));
		vars.put("cd", tables2);
		Tables expectedTables = tables(table(row("a","b","c")));
		expectedTables.addTables(tables2);
		verify(vars.resolve("x@{ab}y@{cd}z"), "xyz", expectedTables);
	}

	private void verify(Pair<String, Tables> resolve, String s) {
		verify(resolve,s,tables());
	}
	private void verify(Pair<String, Tables> resolve, String s, Tables tables) {
		assertThat(resolve.first,is(s));
		assertThat(resolve.second,new TablesMatcher(tables,emptyVars));
	}
}
