/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.diff;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestStringDifferencing {
	final StringDifferencingStandard diff = new StringDifferencingStandard();
	
	@Test public void same() {
		assertThat(diff.differences("abc", "abc"),is(""));
	}
	@Test public void sameButForOneChar() {
		assertThat(diff.differences("abc", "abcd"),is(""));
	}
	@Test public void lowMatching() {
		assertThat(diff.differences("b-@{b}", "b-value"),is(""));
	}
}
