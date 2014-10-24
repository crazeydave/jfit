/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 19/08/2006
*/

package fitlibrary.utility;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestClassUtility {
	@Test
	public void testAFitLibraryClass() {
		assertIsFitLibraryClass(fitlibrary.FitLibraryFixture.class);
		assertIsFitLibraryClass(fitlibrary.DoFixture.class);
		
		assertIsFitLibraryClass(fitlibrary.traverse.Traverse.class);
		assertIsFitLibraryClass(fitlibrary.traverse.workflow.DoTraverse.class);
		assertIsFitLibraryClass(fitlibrary.collection.array.ArrayTraverse.class);
		
		assertIsFitLibraryClass(fit.Fixture.class);

		assertThat(ClassUtility.aFitLibraryClass(fitlibrary.specify.suite.Simple.class),is(false));
		assertThat(ClassUtility.aFitLibraryClass(fitlibrary.specify.eg.User.class),is(false));
	}
	private void assertIsFitLibraryClass(Class<?> type) {
		assertThat(ClassUtility.aFitLibraryClass(type),is(true));
	}
	@Test
    public void testReplaceString() {
		assertThat(StringUtility.replaceString(" / "," ","%20"),is("%20/%20"));
    }
}
