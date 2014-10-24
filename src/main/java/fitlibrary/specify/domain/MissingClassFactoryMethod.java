/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 20/09/2006
*/

package fitlibrary.specify.domain;

import fitlibrary.object.DomainFixtured;

public class MissingClassFactoryMethod implements DomainFixtured {
	@SuppressWarnings("unused")
	public void setAbstractUser(AbstractUser user) {
		//
	}
	public static abstract class AbstractUser {
		//
	}
}
