/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.closure;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import fitlibrary.closure.CalledMethodTarget;

public class TestCalledMethodTarget {
	@Test public void ignored() {
		assertThat(matching("", ""), is(""));
	}
	@Test public void partialMatch() {
		assertThat(matching("abcdefg", "abCdefgh"), is("ab<strike>c</strike><b>C</b>defg<b>h</b>"));
		assertThat(matching("aefg", "abdefg"), is("a<b>bd</b>efg"));
	}
	private String matching(String expected, String actual) {
		return CalledMethodTarget.matching(expected,actual);
	}
}
