package fitlibrary.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class TestTable {
	@Test
	public void sizeWithEmptyTable() {
		assertThat(TableFactory.table().size(),is(0));
	}
	@Test
	public void evenUpRowsChangesLastCellColumnSpanToMatchOtherRowsColumnSpan() throws Exception {
		Table table = TableFactory.table();
		Row row1 = table.newRow();
		row1.addCell("1_1", 4);
		Row row2 = table.newRow();
		row2.addCell("2_1", 1);
		row2.addCell("2_2", 1);
		table.evenUpRows();
		assertThat(row1.at(0).getColumnSpan(),is(4));
		assertThat(row2.at(0).getColumnSpan(),is(1));
		assertThat(row2.at(1).getColumnSpan(),is(3));
	}
}