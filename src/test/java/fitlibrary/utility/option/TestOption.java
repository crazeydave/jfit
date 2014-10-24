/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.utility.option;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class TestOption {
	@Test
	public void none() {
		assertThat(None.none() == None.none(), is(true));
		assertThat(None.none(), is(None.none()));
		Option<String> option = None.none();
		assertThat(option.isNone(), is(true));
		assertThat(option.isSome(), is(false));
	}
	@Test
	public void some() {
		Option<String> option = new Some<String>("aa");
		assertThat(option.isNone(), is(false));
		assertThat(option.isSome(), is(true));
		assertThat(new Some<String>("aa"), is(new Some<String>("aa")));
		assertThat(new Some<String>("aa").hashCode(), is(new Some<String>("aa").hashCode()));
		assertThat(new Some<String>("aa").equals(new Some<String>("bb")), is(false));
		assertThat(new Some<String>("aa").get(), is("aa"));
	}
}
