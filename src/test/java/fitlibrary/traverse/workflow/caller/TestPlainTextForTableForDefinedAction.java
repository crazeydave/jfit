/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow.caller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import fitlibrary.traverse.workflow.caller.ValidCall;

public class TestPlainTextForTableForDefinedAction extends TestCase {
	public void testNone() {
		String[] expected = {""};
		suceeds("", "", expected);
	}
	public void testNoParameter() {
		String[] expected = {"<i>enters</i>"};
		suceeds("enters", "enters", expected);
	}
	public void testNoParameterAsSpecialChar() {
		String[] expected = {"<i>+*</i>"};
		suceeds("+*", "+*", expected);
	}
	public void testNoParameterAsSpecialChars() {
		String[] expected = {"<i>+ *</i>"};
		suceeds("+ *", "+ *", expected);
	}
	public void testNoParameterFailsAsMismatch() {
		fails("leaves", "enters");
	}
	public void testNoParameterFailsAsArgAtStart() {
		fails("anna enters", "enters");
	}
	public void testNoParameterButSeveralKeywords() {
		String[] expected = {"<i>enters the chat room</i>"};
		suceeds("enters the chat room", "enters the chat room", expected);
	}
	public void testOneArgAtEnd() {
		String[] expected = {"<i>enters</i>", "anna"};
		suceeds("enters anna", "enters|", expected);
	}
	public void testOneArgAtStart() {
		String[] expected = {"","anna","<i>enters</i>"};
		suceeds("anna enters", "|enters", expected);
	}
	public void testOneArgAtStartWithSpecialChar() {
		String[] expected = {"","anna","<i>%</i>"};
		suceeds("anna %", "|%", expected);
	}
	public void testOneArgAtStartWithMissingArgs() {
		String[] expected = {"","anna","<i>enters</i>",""};
		suceeds("anna enters", "|enters|", expected);
	}
	public void testOneArgAtEndWithMissingArgs() {
		String[] expected = {"<i>enters</i>", "anna","",""};
		suceeds("enters anna", "enters|||", expected);
	}
	public void testOneArgAtEndFailsAsExtraArgs() {
		fails("enters anna","enters");
	}
	public void testOneKeywordWithTwoArgs() {
		String[] expected = {"","anna","<i>leaves</i>","lotr"};
		suceeds("anna leaves lotr", "|leaves|", expected);
	}
	public void testOneSpecialKeywordWithTwoArgs() {
		String[] expected = {"", "anna", "<i>*</i>", "lotr"};
		suceeds("anna * lotr", "|*|", expected);
	}
	public void testOneSpecialKeywordWithTwoArgsButOneMissingAfter() {
		String[] expected = {"","anna", "<i>*</i>", ""};
		suceeds("anna *", "|*|", expected);
	}
	public void testOneSpecialKeywordWithTwoArgsButOneMissing() {
		String[] expected = {"<i>*</i>", "anna", "", ""};
		suceeds("* anna", "*|||", expected);
	}
	public void testSeveralArgsFromStart() {
		String[] expected = {"","anna", "<i>enters the</i>","lotr","<i>chat room</i>"};
		suceeds("anna enters the lotr chat room", "|enters the|chat room", expected);
	}
	public void testSeveralArgsFromStartWithUpperCase() {
		String[] expected = {"","anna", "<i>enters The</i>", "lotr", "<i>chat Room</i>"};
		suceeds("anna enters The lotr chat Room", "|enters The|chat Room", expected);
	}
	public void testSeveralArgsNotFromStart() {
		String[] expected = {"<i>enters</i>","anna", "<i>the</i>", "lotr", "<i>chat room</i>"};
		suceeds("enters anna the lotr chat room", "enters|the|chat room", expected);
	}
	public void testSeveralArgsWhereSomeArgsAreSeveralWords() {
		String[] expected = {"","anna", "<i>enters the</i>", "lord of the rings","<i>chat room</i>"};
		suceeds("anna enters the lord of the rings chat room", "|enters the|chat room", expected);
	}
	public void testSeveralArgsWithNumber() {
		String[] expected = {"","anna", "<i>enters the</i>", "lord of 4 rings","<i>chat room</i>"};
		suceeds("anna enters the lord of 4 rings chat room", "|enters the|chat room", expected);
	}
	public void testSeveralArgsWithNumbers() {
		String[] expected = {"","5", "<i>enters the</i>","lord of 4 5 rings","<i>chat room</i>"};
		suceeds("5 enters the lord of 4 5 rings chat room", "|enters the|chat room", expected);
	}
	public void testSeveralArgsWithLastMissing() {
		String[] expected = {"", "anna", "<i>enters the</i>", "", "<i>chat room</i>"};
		suceeds("anna enters the chat room", "|enters the|chat room", expected);
	}
	public void testSeveralArgsWithSeveralMissing() {
		String[] expected = {"","anna", "<i>enters the</i>","","<i>chat room</i>","","",""};
		suceeds("anna enters the chat room", "|enters the|chat room|||", expected);
	}
	public void testKeywordsFailToMatch() {
		fails("room","entersTheChatRoom");
	}
	public void testSingleParameterFailsToMatchTwoArgs() {
		String method = "entersTheChatRoom";
		fails("anna enters the lotr chat room",method);
	}
	public void testWaimauku() {
		String[] expected = {"<i>address is at</i>","Waimauku"};
		suceeds("address is at Waimauku", "address is at|", expected);
	}

	private void suceeds(String in, String method, String[] expectedArgs) {
		assertEquals(Arrays.asList(expectedArgs),parse(in,method));
	}
	private void fails(String in, String method) {
		assertEquals(null,parse(in,method));
	}
	private List<String> parse(String in, String method) {
		List<ValidCall> results = new ArrayList<ValidCall>();
		ValidCall.parseDefinedAction(in, method, results);
		if (results.isEmpty())
			return null;
		return results.get(0).getList();
	}
}
