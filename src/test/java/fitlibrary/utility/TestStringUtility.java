/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.utility;

import junit.framework.TestCase;

public class TestStringUtility extends TestCase {
	public void testSpecials() {
		assertEquals("a","\\".replaceAll("\\\\","a"));
	}
	public void testExpansion() {
		assertEquals("aaaaaa","aaa".replaceAll("a","aa"));
	}
}
