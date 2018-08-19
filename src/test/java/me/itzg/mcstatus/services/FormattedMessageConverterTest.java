package me.itzg.mcstatus.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Geoff Bourne
 * @since Aug 2018
 */
public class FormattedMessageConverterTest {
    private FormattedMessageConverter converter;

    @Before
    public void setUp() {
        converter = new FormattedMessageConverter();
    }

    @Test
    public void testNoCodes() {
        final String result = converter.convertToHtml("Just some text");
        assertEquals("Just some text", result);
    }

    @Test
    public void testHypixel() {
        final String result = converter.convertToHtml("               §eHypixel Network §c[1.8-1.13]\n" +
            "     §6§lBED WARS CASTLE V2 §7- §2§lMM BUG FIXES");
        assertEquals("               <span style=\"color:#FFFF55;\">Hypixel Network </span>" +
                "<span style=\"color:#FF5555;\">[1.8-1.13]<br>     </span>" +
                "<span style=\"color:#FFAA00;\"><span style=\"font-weight:bold;\">BED WARS CASTLE V2 </span></span>" +
                "<span style=\"color:#AAAAAA;\">- </span>" +
                "<span style=\"color:#00AA00;\"><span style=\"font-weight:bold;\">MM BUG FIXES</span></span>",
            result);
    }

    @Test
    public void testStripNoCodes() {
        final String result = converter.stripCodes("Just some text");

        assertEquals("Just some text", result);
    }

    @Test
    public void testStripHypixel() {
        final String result = converter.stripCodes("               §eHypixel Network §c[1.8-1.13]\n" +
            "     §6§lBED WARS CASTLE V2 §7- §2§lMM BUG FIXES");

        assertEquals("Hypixel Network [1.8-1.13] BED WARS CASTLE V2 - MM BUG FIXES", result);
    }
}