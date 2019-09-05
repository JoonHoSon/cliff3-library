package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * StringUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-05 최초 작성
 * @since 1.0.0
 */
class StringUtilTest {
    private val logger: Logger = LoggerFactory.getLogger(StringUtilTest::class.java)

    @Test
    fun emailCheckTest() {
        val validEmail1: String = "joonho.son@me.com"
        val validEmail2: String = "joonho.work@gmail.com"
        val invalidEmail1: String = "joonho.son@me"
        val invalidEmail2: String = "@gmail.com"

        assertTrue(StringUtil.isValidEmail(validEmail1), "이메일 유효성 검사 실패 [$validEmail1]")
        assertTrue(StringUtil.isValidEmail(validEmail2), "이메일 유효성 검사 실패 [$validEmail2]")
        assertFalse(StringUtil.isValidEmail(invalidEmail1), "이메일 유효성 검사 실패 [$invalidEmail1]")
        assertFalse(StringUtil.isValidEmail(invalidEmail2), "이메일 유효성 검사 실패 [$invalidEmail2]")
    }

    @Test
    fun urlCheckTest() {
        val validURL1: String = "https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=이순신"
        val validURL2: String = "https://www.yonhapnewstv.co.kr/search/program?q=조국&"
        val invalidURL1: String = "htts://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=이순신"
        val invalidURL2: String = "https://yonhapnewstv/search/program?q=조국&"
        val invalidURL3: String = "search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&q=이순신"

        assertTrue(StringUtil.isValidURL(validURL1), "URL 유효성 검사 실패 [$validURL1]")
        assertTrue(StringUtil.isValidURL(validURL2), "URL 유효성 검사 실패 [$validURL2]")
        assertFalse(StringUtil.isValidURL(invalidURL1), "URL 유효성 검사 실패 [$invalidURL1]")
        assertFalse(StringUtil.isValidURL(invalidURL2), "URL 유효성 검사 실패 [$invalidURL2]")
        assertFalse(StringUtil.isValidURL(invalidURL3), "URL 유효성 검사 실패 [$invalidURL3]")
    }

    @Test
    fun currencyTest() {
        val currency1: String = "1234500"
        val currency2: String = "123457.23"

        assertEquals("1,234,500", StringUtil.currencyFormat(currency1), "통화 변환 실패 [$currency1]")
        assertEquals("123,457.23", StringUtil.currencyFormat(currency2, true), "통화 변환 실패 [$currency2]")
        assertEquals("123,457", StringUtil.currencyFormat(currency2, false), "통화 변환 실패 [$currency2]")
    }
}