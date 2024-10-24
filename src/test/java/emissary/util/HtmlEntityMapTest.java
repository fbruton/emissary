package emissary.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import emissary.test.core.junit5.UnitTest;
import org.junit.jupiter.api.Test;

class HtmlEntityMapTest extends UnitTest {
    @Test
    void testEntityMap() {
        HtmlEntityMap h = new HtmlEntityMap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream myOut = new PrintStream(baos);
        h.dumpTestPage(myOut);
        myOut.close();
        String report = baos.toString();
        assertTrue(report.contains("&Uarr;"), "HtmlEntityMap must contain escaped output");
        assertTrue(report.contains("\u219F"), "HtmlEntityMap must contain utf chars");
    }
}
