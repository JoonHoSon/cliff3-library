package net.cliff3.maven.common.util

import net.cliff3.maven.common.logger
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
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

    @Test
    @Order(4)
    @DisplayName("이메일 유효성 검사")
    fun isValidEmailTest() {
        var validEmail = "test@test.com"

        assertTrue(isValidEmail(validEmail, true))

        validEmail = "test.man@test.co.kr"

        assert(isValidEmail(validEmail))

        var invalidEmail = "test@test"

        assertFalse(isValidEmail(invalidEmail))

        invalidEmail = "test-man@test."

        assertFalse(isValidEmail(invalidEmail))
    }

    @Test
    @Order(5)
    @DisplayName("URL 유효성 검사")
    fun isValidURLTest() {
        var validURL = "http://www.daum.net"

        assertTrue(isValidURL(validURL))

        validURL = "http://www.test.xy"

        assertTrue(isValidURL(validURL))

        validURL = "https://www.test.com/?q=한글"

        assertTrue(isValidURL(validURL))

        var invalidURL = "ftp://daum.net"

        assertFalse(isValidURL(invalidURL))

        invalidURL = "htp://daum.net"

        assertFalse(isValidURL(invalidURL))

        invalidURL = "http//daum.net"

        assertFalse(isValidURL(invalidURL))
    }

    @Test
    @Order(6)
    @DisplayName("화폐단위 출력 테스트")
    fun formatCurrencyTest() {
        val source = "127598000"
        val compare1 = "127,598,000"
        val compare2 = "127,598,000.000"

        assertEquals(compare1, source.formatCurrency())

        assertThrows(NumberFormatException::class.java, {
            "abc".formatCurrency()
        }, "예외 오류 반환 실패")

        assertEquals(compare2, source.formatCurrency(3))

        assertThrows(NumberFormatException::class.java, {
            "abc".formatCurrency(precision = 2)
        }, "예외 오류 반환 실패")
    }
}