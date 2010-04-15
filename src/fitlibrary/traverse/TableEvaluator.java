/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse;

import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ITableListener;
import fitlibrary.utility.TestResults;

public interface TableEvaluator {
    void runTable(Table table, ITableListener tableListener);
	void addNamedObject(String text, TypedObject typedObject, Row row, TestResults testResults);
	void select(String name);
}
