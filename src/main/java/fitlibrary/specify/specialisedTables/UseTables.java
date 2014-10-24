package fitlibrary.specify.specialisedTables;

import fitlibrary.runResults.TestResultsOnCounts;
import fitlibrary.table.Tables;

public class UseTables {
  public boolean useTables(Tables tables) {
	  tables.at(0).at(0).at(0).pass(new TestResultsOnCounts());
	  return true;
  }
  public Tables returnTables(Tables tables) {
	  tables.at(0).at(0).at(0).pass(new TestResultsOnCounts());
	  return tables;
  }
}
