/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.definedAction;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import fit.exception.FitParseException;
import fitlibrary.definedAction.DefinedActionBodyCollector.DefineActionBodyConsumer;
import fitlibrary.dynamicVariable.GlobalDynamicVariables;
import fitlibrary.matcher.TablesMatcher;
import fitlibrary.table.Tables;
import fitlibrary.utility.SimpleWikiTranslator;

@RunWith(JMock.class)
public class TestDefinedActionBodyCollectorWithList {
	Mockery context = new Mockery();
	DefinedActionBodyCollector collector = new DefinedActionBodyCollector();
	DefineActionBodyConsumer consumer = context.mock(DefineActionBodyConsumer.class);
	
	@Test
	public void oneTableBody() {
		final String wiki = "|a|\n\n|comment|";
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher(wiki)));
		}});
		check(wiki);
	}
	@Test
	public void twoTableBody() {
		final String wiki = "|a|\n\n|comment|\n\n|comment2|\n";
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher(wiki)));
		}});
		check(wiki);
	}
	@Test
	public void zeroTableBody() {
		final String wiki = "|a|\n";
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher(wiki)));
		}});
		check(wiki);
	}
	@Test
	public void twoOneTableBodies() {
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher("|a|\n\n|comment|\n")));
			one(consumer).addAction(with(tablesMatcher("|b|\n\n|comment|\n")));
		}});
		check("|a|\n\n|comment|\n----\n|b|\n\n|comment|\n");
	}
	@Test
	public void threeOneTableBodies() {
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher("|a|\n\n|comment|\n")));
			one(consumer).addAction(with(tablesMatcher("|b|\n\n|comment|\n")));
			one(consumer).addAction(with(tablesMatcher("|c|\n\n|comment|\n")));
		}});
		check("|a|\n\n|comment|\n----\n|b|\n\n|comment|\n----\n|c|\n\n|comment|\n");
	}
	@Test
	public void twoOneTableBodiesWithHRatEnd() {
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher("|a|\n\n|comment|\n")));
			one(consumer).addAction(with(tablesMatcher("|b|\n\n|comment|\n")));
		}});
		check("|a|\n\n|comment|\n----\n|b|\n\n|comment|\n----\n");
	}
	@Test
	public void zeroTableBodyFirst() {
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher("|a|\n")));
			one(consumer).addAction(with(tablesMatcher("|b|\n\n|comment|\n")));
		}});
		check("|a|\n----\n|b|\n\n|comment|\n");
	}
	@Test
	public void zeroTableBodySecond() {
		context.checking(new Expectations() {{
			one(consumer).addAction(with(tablesMatcher("|a|\n")));
			one(consumer).addAction(with(tablesMatcher("|b|\n")));
		}});
		check("|a|\n----\n|b|\n");
	}
	
	protected TablesMatcher tablesMatcher(String wiki) {
		return new TablesMatcher(makeTables(wiki),new GlobalDynamicVariables());
	}
	private void check(String wiki) {
		collector.parseDefinitions(makeTables(wiki), consumer);
	}
	protected Tables makeTables(String wiki) {
		try {
			Tables tables = SimpleWikiTranslator.translateToTablesOnList(wiki);
			tables.at(0).setLeader("");
			tables.last().setTrailer("");
			return tables;
		} catch (FitParseException e) {
			throw new RuntimeException(e.toString());
		}
	}
}
