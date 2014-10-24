/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 5/09/2006
*/

package fitlibrary.utility;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;
import fitlibrary.utility.CollectionUtility;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestCollectionUtility extends TestCase {
	public void testNoneAndNone() {
		Iterator iterator = none().iterator();
		equ(iterator, iterator);
		equ(iterator, none().iterator());
	}
	public void testOneAndOne() {
		ArrayList one = one();
		Iterator iterator = one.iterator();
		equ(iterator, iterator);
		equ(iterator, new ArrayList(one).iterator());
	}
	public void testOneAndNone() {
		notEqu(one().iterator(), none().iterator());
	}
	public void testTwoAndTwo() {
		ArrayList two = two();
		Iterator iterator = two.iterator();
		equ(iterator, iterator);
		equ(iterator, new ArrayList(two).iterator());
	}
	public void testTwoAndOne() {
		notEqu(two().iterator(), one().iterator());
	}
	public void testTwoAndNone() {
		notEqu(two().iterator(), none().iterator());
	}

	private ArrayList none() {
		return new ArrayList();
	}
	private ArrayList one() {
		ArrayList list = none();
		list.add("one");
		return list;
	}
	private ArrayList two() {
		ArrayList list = one();
		list.add("two");
		return list;
	}
	private void equ(Iterator iterator2, Iterator iterator) {
		assertTrue(eq(iterator, iterator2));
		assertTrue(eq(iterator2, iterator2));
	}
	private void notEqu(Iterator iterator2, Iterator iterator) {
		assertFalse(eq(iterator, iterator2));
		assertFalse(eq(iterator2, iterator));
	}
	private boolean eq(Iterator iterator, Iterator iterator2) {
		return CollectionUtility.equalsIterator(iterator,iterator2);
	}
}
