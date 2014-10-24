/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.utility;
import fitlibrary.utility.ExtendedCamelCase;
import junit.framework.TestCase;

public class TestExtendedCamelCase extends TestCase {
    public void testJustCamel() {
        check("two words","twoWords");
        check("three wee words","threeWeeWords");
    }
    public void testExtendedCamel() {
        check("\" hi \"","quoteHiQuote");
        check("!#$%age","bangHashDollarPercentAge");
        check("&'()*","ampersandSingleQuoteLeftParenthesisRightParenthesisStar");
        check("+,-./:","plusCommaMinusDotSlashColon");
        check(";=?","semicolonEqualsQuestion");
        check("@[]\\","atLeftSquareBracketRightSquareBracketBackslash");
        check("^`{}~","caretBackquoteLeftBraceRightBraceTilde");
        check("cost $","costDollar");
        check("cost$","costDollar");
        check("!","bang");
        check("!!","bangBang");
        check("meet @","meetAt");
        check("rick@mugridge.com","rickAtMugridgeDotCom");
        check("","blank");
    }
    public void testNewlineSeparator() {
        check("two\nwords","twoWords");
        check("two\r\nwords","twoWords");
        check("two \nwords","twoWords");
        check("two \r\nwords","twoWords");
    }
    public void testLeadingDigit() {
        check("2 words","twoWords");
    }
    public void testLeadingCapital() {
        check("Two words","twoWords");
    }
    public void testJavaKeyword() {
        check("static","static_");
        check("assert","assert_");
        check("return","return_");
        check("null","null_");
    }
    public void testUnicode() {
        check("\u216C","u216C");
        check("\u216D\uFFFE","u216DuFFFE");
        check("\uFFFF","uFFFF");
        check("\u0041b","ab");
    }
//    public void testUnicodeRetained() {
//        checkUni("Ελληνικό","ελληνικό");
//        checkUni("█Ελληνικό","u2588Ελληνικό");
//        checkClassUni("Ελληνικό","Ελληνικό");
//    }
    public void testCamelClass() {
        checkClass("do fixture","DoFixture");
        checkClass("do   Fixture","DoFixture");
        checkClass("fitlibrary.DoFixture","fitlibrary.DoFixture");
    }
    private void check(String in, String out) {
        assertEquals(out,ExtendedCamelCase.camel(in,false));
    }
//    private void checkUni(String in, String out) {
//        assertEquals(out,ExtendedCamelCase.camel(in,true));
//    }
    private void checkClass(String in, String out) {
        assertEquals(out,ExtendedCamelCase.camelClassName(in,false));
    }
//    private void checkClassUni(String in, String out) {
//        assertEquals(out,ExtendedCamelCase.camelClassName(in,true));
//    }
}
