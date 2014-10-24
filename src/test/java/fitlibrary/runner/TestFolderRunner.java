/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.runner;

import junit.framework.TestCase;

public class TestFolderRunner extends TestCase {
    public void testSpecialSetUp() {
        assertSpecialHtml("SETUP.HTML");
        assertSpecialHtml("SETUp.HTMl");
        assertSpecialHtml("Setup.htmL");
        assertSpecialHtml("sETup.hTml");
        assertSpecialHtml("setup.html");
        
        assertSpecialHtml("SETUP.HTM");
        assertSpecialHtml("SETUp.HTM");
        assertSpecialHtml("Setup.htm");
        assertSpecialHtml("sETup.hTm");
        assertSpecialHtml("setup.htm");
    }
    public void testSpecialTearDown() {
        assertSpecialHtml("TEARDOWN.HTML");
        assertSpecialHtml("tEARDOWn.HTMl");
        assertSpecialHtml("TeardowN.htmL");
        assertSpecialHtml("teaRDown.hTml");
        assertSpecialHtml("teardown.html");
        
        assertSpecialHtml("TEARDOWN.HTM");
        assertSpecialHtml("tEARDOWn.HTM");
        assertSpecialHtml("TeardowN.htm");
        assertSpecialHtml("teaRDown.hTm");
        assertSpecialHtml("teardown.htm");
    }
    public void testSpecialSetUpXls() {
        assertSpecialXls("SETUP.XLS");
        assertSpecialXls("SETUp.XLs");
        assertSpecialXls("Setup.xlS");
        assertSpecialXls("sETup.xLs");
        assertSpecialXls("setup.xls");
    }
    public void testSpecialTearDownXls() {
        assertSpecialXls("TEARDOWN.XLS");
        assertSpecialXls("tEARDOWn.XLs");
        assertSpecialXls("TeardowN.xlS");
        assertSpecialXls("teaRDown.xLs");
        assertSpecialXls("teardown.xls");
    }
    
    private void assertSpecialHtml(String name) {
        assertSpecial(name);
        assertTrue(FileParseUtilities.isHtmlFileName(name));
        assertFalse(FileParseUtilities.isXlsFileName(name));
    }
    private void assertSpecialXls(String name) {
        assertSpecial(name);
        assertTrue(FileParseUtilities.isXlsFileName(name));
        assertFalse(FileParseUtilities.isHtmlFileName(name));
    }
    private void assertSpecial(String name) {
        assertTrue(CollectSetUpTearDown.specialFileName(name));
    }
}
