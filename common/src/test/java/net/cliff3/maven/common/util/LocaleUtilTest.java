package net.cliff3.maven.common.util;

import static org.testng.Assert.*;

import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LocaleUtilTest
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
@Test(groups = "LocaleUtilTest")
public class LocaleUtilTest {
    @Test
    public void isValidLocaleTest() {
    	final String validLanguageCode = "KO";
        final String validCountryCode = "KR";
        final String invalidLanguageCode = "1F";
        final String invalidCountryCode = "FR13";

        assertTrue(LocaleUtil.isValidLocale(new Locale(validLanguageCode, validCountryCode)));
        assertFalse(LocaleUtil.isValidLocale(new Locale(invalidLanguageCode, invalidCountryCode)));

        assertTrue(LocaleUtil.isValidLocale("en", "Us"));
    }
}
