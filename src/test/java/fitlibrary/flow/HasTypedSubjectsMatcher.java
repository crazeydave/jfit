/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ArrayUtility;

public class HasTypedSubjectsMatcher extends TypeSafeMatcher<List<TypedObject>> {
	private final Object[] expected;
	
	public HasTypedSubjectsMatcher(Object... expected) {
		this.expected = expected;
	}
	@Override
	public boolean matchesSafely(List<TypedObject> items) {
		return expected.length == items.size() && holds(items);
	}
	private boolean holds(List<TypedObject> items) {
		for (int i = 0; i < expected.length; i++)
			if (items.get(i).getSubject() != expected[i])
				return false;
		return true;
	}
	@Override
	public void describeTo(Description description) {
		description.appendText("does not contain: "+ArrayUtility.mkString(expected));
	}
}