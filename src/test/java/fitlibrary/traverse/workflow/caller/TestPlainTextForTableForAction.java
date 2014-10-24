/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow.caller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import fitlibrary.runtime.RuntimeContextContainer;
import fitlibrary.traverse.workflow.caller.ValidCall;

public class TestPlainTextForTableForAction extends TestCase {
	public void testNone() {
		String[] expected = {""};
		suceeds("", "blank", expected, 0);
	}
	public void testNoParameter() {
		String[] expected = {"<i>enters</i>"};
		suceeds("enters", "enters", expected, 0);
	}
	public void testNoParameterAsSpecialChar() {
		String[] expected = {"<i>+*</i>"};
		suceeds("+*", "plusStar", expected, 0);
	}
	public void testNoParameterAsSpecialChars() {
		String[] expected = {"<i>+ *</i>"};
		suceeds("+ *", "plusStar", expected, 0);
	}
	public void testNoParameterFailsAsMismatch() {
		fails("leaves", "enters", 0);
	}
	public void testNoParameterFailsAsArgAtStart() {
		fails("anna enters", "enters", 0);
	}
	public void testNoParameterFailsAsArgAtEnd() {
		fails("anna enters", "enters", 0);
	}
	public void testNoParameterButSeveralKeywords() {
		String[] expected = {"<i>enters the chat room</i>"};
		suceeds("enters the chat room", "entersTheChatRoom", expected,0);
	}
	public void testNoParameterButSeveralKeywordsButOneArg() {
		fails("enters no chat room","entersTheChatRoom",0);
	}

	public void testOneArgAtStart() {
		String[] expected = {"","anna","<i>enters</i>"};
		suceeds("anna enters", "enters", expected,1);
	}
	public void testOneArgAtStartWithSpecialChar() {
		String[] expected = {"","anna","<i>%</i>"};
		suceeds("anna %", "percent", expected,1);
	}
	public void testOneArgAtStartWithMissingArgs() {
		String[] expected = {"","anna","<i>enters</i>",""};
		suceeds("anna enters", "enters", expected,2);
	}
	public void testOneArgAtStartFailsAsExtraArgs() {
		fails("anna enters","enters",0);
	}
	public void testOneArgAtEnd() {
		String[] expected = {"<i>enters</i>", "anna"};
		suceeds("enters anna", "enters", expected,1);
	}
	public void testOneArgAtEndWithMissingArgs() {
		String[] expected = {"<i>enters</i>", "anna","",""};
		suceeds("enters anna", "enters", expected,2);
	}
	public void testOneArgAtEndFailsAsExtraArgs() {
		fails("enters anna","enters",0);
	}
	public void testOneKeywordWithTwoArgs() {
		String[] expected = {"","anna","<i>leaves</i>","lotr"};
		suceeds("anna leaves lotr", "leaves", expected,2);
	}
	public void testOneSpecialKeywordWithTwoArgs() {
		String[] expected = {"", "anna", "<i>*</i>", "lotr"};
		suceeds("anna * lotr", "star", expected,2);
	}
	public void testOneSpecialKeywordWithTwoArgsButOneMissingAfter() {
		String[] expected = {"","anna", "<i>*</i>", ""};
		suceeds("anna *", "star", expected,2);
	}
	public void testOneSpecialKeywordWithTwoArgsButOneMissing() {
		String[] expected = {"<i>*</i>", "anna", "", ""};
		suceeds("* anna", "star", expected,2);
	}
	public void testSeveralArgsFromStart() {
		String[] expected = {"","anna", "<i>enters the</i>","lotr","<i>chat room</i>"};
		suceeds("anna enters the lotr chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsFromStartWithUpperCase() {
		String[] expected = {"","anna", "<i>enters The</i>", "lotr", "<i>chat Room</i>"};
		suceeds("anna enters The lotr chat Room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsNotFromStart() {
		String[] expected = {"<i>enters</i>","anna", "<i>the</i>", "lotr", "<i>chat room</i>"};
		suceeds("enters anna the lotr chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsWhereSomeArgsAreSeveralWords() {
		String[] expected = {"","anna", "<i>enters the</i>", "lord of the rings","<i>chat room</i>"};
		suceeds("anna enters the lord of the rings chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsWithNumber() {
		String[] expected = {"","anna", "<i>enters the</i>", "lord of 4 rings","<i>chat room</i>"};
		suceeds("anna enters the lord of 4 rings chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsWithNumbers() {
		String[] expected = {"","5", "<i>enters the</i>","lord of 4 5 rings","<i>chat room</i>"};
		suceeds("5 enters the lord of 4 5 rings chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsWithLastMissing() {
		String[] expected = {"","anna", "<i>enters the chat room</i>",""};
		suceeds("anna enters the chat room", "entersTheChatRoom", expected,2);
	}
	public void testSeveralArgsWithSeveralMissing() {
		String[] expected = {"","anna", "<i>enters the chat room</i>","","",""};
		suceeds("anna enters the chat room", "entersTheChatRoom", expected,3);
	}
	public void testNumberInMethodName() {
		String[] expected = {"<i>subtract 123</i>"};
		suceeds("subtract 123", "subtract123", expected,0);
	}
	public void testNumberNotInMethodName() {
		String[] expected = {"<i>subtract</i>","123"};
		suceeds("subtract 123", "subtract", expected,1);
	}
	public void testKeywordsFailToMatch() {
		fails("room","entersTheChatRoom",0);
	}
	public void testSingleParameterFailsToMatchTwoArgs() {
		String method = "entersTheChatRoom";
		fails("anna enters the lotr chat room",method,1);
	}

	private void suceeds(String in, String method, String[] expectedArgs, int argCount) {
		assertEquals(Arrays.asList(expectedArgs),parse(in,method,argCount));
	}
	private void fails(String in, String method, int argCount) {
		assertEquals(null,parse(in,method,argCount));
	}
	private List<String> parse(String in, String method, int argCount) {
		List<ValidCall> results = new ArrayList<ValidCall>();
		ValidCall.parseAction(Arrays.asList(in.split(" ")), method, argCount,results,new RuntimeContextContainer());
		if (results.isEmpty())
			return null;
		return results.get(0).getList();
	}
}
