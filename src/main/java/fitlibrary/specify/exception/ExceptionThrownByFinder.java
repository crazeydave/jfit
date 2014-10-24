/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 19/08/2006
*/

package fitlibrary.specify.exception;

import fitlibrary.traverse.DomainAdapter;

public class ExceptionThrownByFinder implements DomainAdapter  {
	public void chargeWith(@SuppressWarnings("unused") User user, @SuppressWarnings("unused") double amount) {
		//
	}
	public User findUser(@SuppressWarnings("unused") String s) {
		throw new ForcedException();
	}
	public static class User {
		//
	}
	@Override
	public Object getSystemUnderTest() {
		return null;
	}
}
