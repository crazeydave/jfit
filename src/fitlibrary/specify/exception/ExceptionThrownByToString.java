/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 19/08/2006
*/

package fitlibrary.specify.exception;

import fitlibrary.object.DomainFixtured;

public class ExceptionThrownByToString implements DomainFixtured {
	public Value value() {
		return new Value();
	}
	public static class Value {
		public static Object parse(@SuppressWarnings("unused") String s) {
			return new Value();
		}
		@Override
		public String toString() {
			throw new ForcedException();
		}
	}
}
