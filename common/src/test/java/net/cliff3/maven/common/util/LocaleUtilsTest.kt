package net.cliff3.maven.common.util

import net.cliff3.maven.common.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Locale

class LocaleUtilsTest {
    private val log = logger()

    @Test
    fun isValidLocaleTest() {
        var locale: Locale? = Locale.KOREA
        var result = isValidLocale(locale)

        log.debug("target locale : {}", locale)

        assertTrue(result, "$locale 유효성 검사 실패")
        assertTrue(isValidLocale(Locale("ko")), "")

        locale = Locale("help", "what?")
        result = isValidLocale(locale)

        assertFalse(result, "$locale 유효성 검사 실패")
    }

    @Test
    fun isValidLocaleWithLanguageCountryTest() {
        assertTrue(isValidLocale(languageCode = "ko", countryCode = "kr"))
        assertFalse(isValidLocale(languageCode = "", countryCode = "kr"))
        assertTrue(isValidLocale(languageCode = "ko", countryCode = null))
    }
}