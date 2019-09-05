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

    @Test
    fun hexTest() {
        val value1: String = "한글"
        val value2: String = "cliff3.net"

        assertEquals("D55CAE00", StringUtil.stringToHex(value1, toUpperCase = true), "문자열 16진수 변환 실패 [$value1]")
        assertEquals("d55cae00", StringUtil.stringToHex(value1), "문자열 16진수 변환 실패 [$value1]")
        assertEquals("0XD55CAE00",
                     StringUtil.stringToHex(value1, hasPrefix = true, toUpperCase = true),
                     "문자열 16진수 변환 실패 [$value1]")
        assertEquals("636C696666332E6E6574",
                     StringUtil.stringToHex(value2, toUpperCase = true),
                     "문자열 16진수 변환 실패 [$value2]")
        assertEquals("0x636c696666332e6e6574",
                     StringUtil.stringToHex(value2, hasPrefix = true),
                     "문자열 16진수 변환 실패 [$value2]")
    }

    @Test
    fun randomStringTest() {
        assertTrue(StringUtil.makeRandomString(10).length == 10, "무작위 문자열 출력 실패")
    }

    @Test
    fun checkDefaultValueTest() {
        val targets: Array<String> = arrayOf("abc", "bcd", "Efg")
        val compare: String = "efg"
        val default: String = "defaultString"

        assertEquals(default,
                     StringUtil.checkValueInDefaultList(targets = targets, compare = compare, default = default),
                     "기본값 확인 실패 [대상 : $targets.forEach(System.out::println), 비교 : $compare]")
        assertEquals(compare.capitalize(),
                     StringUtil.checkValueInDefaultList(targets = targets,
                                                        compare = compare,
                                                        default = default,
                                                        isCaseSensitive = false),
                     "기본값 확인 실패 [대상 : $targets.forEach(System.out::println), 비교 : $compare]")
    }

    @Test
    fun maskingTest() {
        val target: String = "abcdefg"
        val result1: String = "abc****"
        val result2: String = "*******"

        assertEquals(result1, StringUtil.masking(target, 3), "마스킹 처리 실패 [$target]")
        assertEquals(result2, StringUtil.masking(target, 100), "마스킹 처리 실패 [$target]")
    }
}