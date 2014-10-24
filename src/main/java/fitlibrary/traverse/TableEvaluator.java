/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse;

import fitlibrary.runResults.TestResults;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.typed.TypedObject;

public interface TableEvaluator {
    void runTable(Table table, TestResults testResults);
	void addNamedObject(String text, TypedObject typedObject, Row row, TestResults testResults);
	void select(String name);
	void runInnerTables(Tables definedActionBody, TestResults testResults);
}
