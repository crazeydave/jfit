/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.batch;

import static fitlibrary.batch.FitLibraryRunner.RunParameters.ValidParameters.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.security.InvalidParameterException;

import org.junit.Test;

import fitlibrary.batch.FitLibraryRunner.RunParameters;

public class TestFitLibraryRunner {
	@Test
	public void validRunParametersWithDefaults() {
		String[] args = {"-suiteName", "Suite.Name"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("."));
		assertThat(runParameters.get(RESULTS_DIRY),is("runnerResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("false"));
		assertThat(runParameters.get(PORT),is("80"));
		assertThat(runParameters.get(RETRIES),is("0"));
		assertThat(runParameters.get(JUNIT_XML_OUTPUT),is("false"));
		
	}
	@Test(expected=InvalidParameterException.class)
	public void missingSuiteName() {
		String[] args = {};
		FitLibraryRunner.getRunParameters(args);
	}
	@Test
	public void fitNesseDiryIsAlsoGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-fitNesseDiry", "../.."};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("../.."));
		assertThat(runParameters.get(RESULTS_DIRY),is("runnerResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("false"));
		assertThat(runParameters.get(PORT),is("80"));
	}
	@Test
	public void resultsDiryIsAlsoGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-resultsDiry", "TheResults"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("."));
		assertThat(runParameters.get(RESULTS_DIRY),is("TheResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("false"));
		assertThat(runParameters.get(PORT),is("80"));
	}
	@Test
	public void showPassesIsAlsoGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-showPasses", "true"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("."));
		assertThat(runParameters.get(RESULTS_DIRY),is("runnerResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("true"));
		assertThat(runParameters.getInt(PORT),is(80));
		assertThat(runParameters.getInt(RETRIES),is(0));
	}
	@Test
	public void retriesIsAlsoGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-retries", "2"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("."));
		assertThat(runParameters.get(RESULTS_DIRY),is("runnerResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("false"));
		assertThat(runParameters.getInt(PORT),is(80));
		assertThat(runParameters.getInt(RETRIES),is(2));
	}
	@Test
	public void portIsAlsoGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-port", "8990"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("."));
		assertThat(runParameters.get(RESULTS_DIRY),is("runnerResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("false"));
		assertThat(runParameters.get(PORT),is("8990"));
	}
	@Test(expected=NumberFormatException.class)
	public void portIsGivenIncorrectly() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-port", "abc"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		runParameters.getInt(PORT);
	}
	@Test
	public void allAreGiven() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-fitNesseDiry", "../..",
				"-resultsDiry", "TheResults",
				"-showPasses", "true",
				"-port", "8990",
				"-retries", "11",
				"-junitXmlOutput", "true"};
		RunParameters runParameters = FitLibraryRunner.getRunParameters(args);
		assertThat(runParameters.get(SUITE_NAME),is("Suite.Name"));
		assertThat(runParameters.get(FIT_NESSE_DIRY),is("../.."));
		assertThat(runParameters.get(RESULTS_DIRY),is("TheResults"));
		assertThat(runParameters.get(SHOW_PASSES),is("true"));
		assertThat(runParameters.get(PORT),is("8990"));
		assertThat(runParameters.get(RETRIES),is("11"));
		assertThat(runParameters.get(JUNIT_XML_OUTPUT),is("true"));
	}
	@Test
	public void unknownParameter() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-portly", "8990"};
		try {
			FitLibraryRunner.getRunParameters(args);
			fail();
		} catch (InvalidParameterException ipe) {
			assertThat(ipe.getMessage(), containsString("portly"));
		}
	}
	@Test
	public void missingValue() {
		String[] args = {
				"-suiteName", "Suite.Name",
				"-port" };
		try {
			FitLibraryRunner.getRunParameters(args);
			fail();
		} catch (InvalidParameterException ipe) {
			assertThat(ipe.getMessage(), containsString("port"));
		}
	}
	@Test
	public void formatTimeWhenLessThanOneSecond() {
		assertThat(FitLibraryRunner.formatTime(678), is("678 milliseconds."));
	}
	@Test
	public void formatTimeWhenLessThanMinute() {
		assertThat(FitLibraryRunner.formatTime(11678), is("11 seconds (11678 milliseconds)."));
	}
	@Test
	public void formatTimeWhenLessThanHour() {
		assertThat(FitLibraryRunner.formatTime(101678), is("1 minutes 41 seconds (101678 milliseconds)."));
	}
	@Test
	public void formatTimeWhenGreaterThanHour() {
		assertThat(FitLibraryRunner.formatTime(3*60*60*1000+2345), is("3 hours 0 minutes 2 seconds (10802345 milliseconds)."));
	}
}
