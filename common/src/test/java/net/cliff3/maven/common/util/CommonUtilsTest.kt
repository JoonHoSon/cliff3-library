package net.cliff3.maven.common.util

import net.cliff3.maven.common.logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.util.Locale

@TestMethodOrder(MethodOrderer.MethodName::class)
class CommonUtilsTest {
    private val log = logger()

    @Test
    @Order(1)
    @DisplayName("Locale 유효성 테스트")
    fun isValidLocaleTest() {
        var locale: Locale? = Locale.KOREA
        var result = isValidLocale(locale)

        log.debug("target locale : {}", locale)

        assertTrue(result, "$locale 유효성 검사 실패")
        assertTrue(isValidLocale(Locale("ko")))

        locale = Locale("help", "what?")
        result = isValidLocale(locale)

        assertFalse(result, "$locale 유효성 검사 실패")
    }

    @Test
    @Order(2)
    @DisplayName("언어 코드 및 국가 코드를 이용한 Locale 유효성 검사")
    fun isValidLocaleWithLanguageCountryTest() {
        assertTrue(isValidLocale(languageCode = "ko", countryCode = "kr"))
        assertFalse(isValidLocale(languageCode = "", countryCode = "kr"))
        assertTrue(isValidLocale(languageCode = "ko", countryCode = null))
    }

    @Test
    @Order(3)
    @DisplayName("범위내 무작위 정수 추출 테스트")
    fun getRandomIntegerTest() {
        var min: Int
        var max: Int
        var result: Int

        for (i in 1..37) {
            min = i
            max = 37 + i
            result = getRandomInteger(min, max)

            assertTrue(result in min..max)
        }
    }
}