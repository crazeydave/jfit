/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.spec;

import static fitlibrary.matcher.TableBuilderForTests.cell;
import static fitlibrary.matcher.TableBuilderForTests.row;
import static fitlibrary.matcher.TableBuilderForTests.table;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.matcher.TableBuilderForTests.CellBuilder;
import fitlibrary.matcher.TableBuilderForTests.TableBuilder;
import fitlibrary.runResults.TestResults;
import fitlibrary.suite.StorytestRunner;
import fitlibrary.table.Cell;
import fitlibrary.table.Table;
import fitlibrary.table.TableFactory;
import fitlibrary.table.Tables;

@RunWith(JMock.class)
public class TestSpecifyFixture {
	final Mockery context = new Mockery();
	final StorytestRunner runner = context.mock(StorytestRunner.class);
	final SpecifyErrorReport errorReport = context.mock(SpecifyErrorReport.class);
	final SpecifyFixture specifyFixture = new SpecifyFixture(runner, errorReport);
	final TestResults testResults = context.mock(TestResults.class);
	Tables actual = TableFactory.tables();
	Tables expected = TableFactory.tables();

	@Test
	public void anExceptionIsThrownIfNoTablesForActual() {
		final Table table = specifyingTable(cell(), cell());
		context.checking(new Expectations() {{
			oneOf(table).error(with(testResults),with(any(FitLibraryException.class)));
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void singleTableAndTextMatches() {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell("abc"),cell("de"),cell("fg"))),
				cell("").with(singleRowTable(cell("abc"),cell("de"),cell("fg")))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(expectedCell(table)).pass(testResults);
			oneOf(testResults).addRights(2);
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void textOfCellDoesNotMatch() {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell("ab"))),
				cell("").with(singleRowTable(cell("cd")))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(errorReport).cellTextWrong("Table[0].Row[0].Cell[0]","ab","cd");
			oneOf(expectedCell(table)).fail(testResults);
			oneOf(errorReport).actualResult(actualCell(table));
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void leaderOfTableDoesNotMatch() {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell("ab")).withLeader("lead1")),
				cell("").with(singleRowTable(cell("ab")).withLeader("lead2"))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(errorReport).leaderWrong("Table[0]","lead1","lead2");
			oneOf(expectedCell(table)).fail(testResults);
			oneOf(errorReport).actualResult(actualCell(table));
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void trailerOfTableDoesNotMatch() {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell("ab")).withTrailer("lead1")),
				cell("").with(singleRowTable(cell("ab")).withTrailer("lead2"))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(errorReport).trailerWrong("Table[0]","lead1","lead2");
			oneOf(expectedCell(table)).fail(testResults);
			oneOf(errorReport).actualResult(actualCell(table));
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void tagLineOfCellDoesNotMatch() {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell("ab").withTagLine("pass"))),
				cell("").with(singleRowTable(cell("ab").withTagLine("fail")))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(errorReport).tagLineWrong("Table[0].Row[0].Cell[0]","pass","fail");
			oneOf(expectedCell(table)).fail(testResults);
			oneOf(errorReport).actualResult(actualCell(table));
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}
	@Test
	public void ignoresCR() {
		theseAreConsideredTheSame("abc\r", "\rabc");
	}
	@Test
	public void ignoresLeadingAndTrailingSpaces() {
		theseAreConsideredTheSame("abc  ", " abc");
	}
	@Test
	public void treatsTabAsASingleSpace() {
		theseAreConsideredTheSame("a b\tc", " a\tb c");
	}
	@Test
	public void treatsTheSameTheTwoFormsOfHR() {
		theseAreConsideredTheSame("a <hr><hr/>\n b", "a <hr/>\n<hr> b");
	}
	@Test
	public void treatsTheSameTheTwoFormsOfBR() {
		theseAreConsideredTheSame("a <br><br/> b", "a <br/><br> b");
	}
	@Test
	public void treatsTheSameWhenExpectedIsIGNORE() {
		theseAreConsideredTheSame("a <br><br/> b", "IGNORE");
	}
	@Test
	public void treatsTheSameWhenTextAfterStacktraceIsIgnored() {
		theseAreConsideredTheSame("ab class=\"fit_stacktrace\"> XYZ", "ab class=\"fit_stacktrace\">");
	}

	
	private void theseAreConsideredTheSame(String cellText1,
			String cellText2) {
		final Table table = specifyingTable(
				cell("").with(singleRowTable(cell(cellText1))),
				cell("").with(singleRowTable(cell(cellText2)))
		);
		storytestRunnerIsCalled(table);
		context.checking(new Expectations() {{
			oneOf(expectedCell(table)).pass(testResults);
			oneOf(testResults).addRights(0);
		}});
		specifyFixture.interpretAfterFirstRow(table,testResults);
	}

	
	
	protected Cell actualCell(Table table) {
		return table.at(1).at(0);
	}
	protected Cell expectedCell(Table table) {
		return table.at(1).at(1);
	}
	private Table specifyingTable(CellBuilder actualCell, CellBuilder expectedCell) {
		return table().with(row(),row().with(actualCell,expectedCell))
			.mock(context);
	}
	private TableBuilder singleRowTable(CellBuilder... cells) {
		return table().with(row().with(cells));
	}
	private void storytestRunnerIsCalled(final Table table) {
		context.checking(new Expectations() {{
			oneOf(runner).doStorytest(table.at(1).at(0));
		}});
	}
}
