/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import java.util.List;

import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.TypedObject;

public interface IScope {
	List<TypedObject> objectsForLookup();
	List<Class<?>> possibleClasses();
	void temporarilyAdd(Evaluator evaluator);
	void removeTemporary(Evaluator evaluator);
	void addGlobal(TypedObject typedObject);
}
