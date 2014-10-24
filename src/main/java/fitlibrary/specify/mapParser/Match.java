/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.specify.mapParser;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Match {
	@SuppressWarnings("unchecked")
	public Map getMapOfStringAbc() {
		Map map = new HashMap();
		map.put("a","b");
		map.put("b","c");
		map.put("c","a");
		return map;
	}
	@SuppressWarnings("unchecked")
	public Map getMapOf123() {
		Map map = new HashMap();
		map.put(Integer.valueOf(1),Integer.valueOf(2));
		map.put(Integer.valueOf(2),Integer.valueOf(3));
		map.put(Integer.valueOf(3),Integer.valueOf(4));
		return map;
	}
	public Map getMapEmpty() {
		return new HashMap();
	}
}
