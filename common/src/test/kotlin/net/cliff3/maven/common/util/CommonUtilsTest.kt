package net.cliff3.maven.common.util

import net.cliff3.maven.common.topLogger
import org.apache.commons.io.FilenameUtils
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 공용 함수 테스트
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
class CommonUtilsTest {
    @Test
    @Order(1)
    @DisplayName("Locale 유효성 테스트")
    fun isvalidLocaleTest() {
        topLogger.debug("Start locale test")

        var locale: Locale? = Locale("ko", "abc")

        assertFalse(isValidLocale(locale), "국가 코드가 포함되지 않은 locale 검증 실패")

        locale = Locale("ko", "kr")

        assertTrue(isValidLocale(locale), "유효성 검증 실패")

        assertFalse(isValidLocale(null), "null 검증 실패")

        var languageCode: String = "ko"
        var countryCode: String? = "kr"

        assertTrue(isValidLocale(languageCode, countryCode), "언어코드 및 국가코드를 이용한 locale 검증 실패")

        countryCode = ""

        assertTrue(isValidLocale(languageCode, countryCode), "국가코드가 생략된 locale 검증 실패")

        countryCode = "한국"

        assertFalse(isValidLocale(languageCode, countryCode), "잘못된 국가코드를 이용한 locale 검증 실패")

        languageCode = "abc"
        countryCode = "kr"

        assertFalse(isValidLocale(languageCode, countryCode), "잘못된 언어코드를 이용한 locale 검증 실패")
    }

    @Test
    @Order(2)
    @DisplayName("범위내 무작위 정수 추출 테스트")
    fun getRandomIntegerTest() {
        var min: Int
        var max: Int
        var result: Int

        for (i in 1..40) {
            min = i
            max = 40 + i
            result = getRandomInteger(min, max)

            assertTrue(result in min..max)
        }
    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(5)
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

    @Test
    @Order(6)
    @DisplayName("경로명 생성 테스트")
    fun generateDirNameByDatePolicyTest() {
        val date: Calendar = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DATE)
        var result = generateDirNameByDatePolicy(DirPathPolicy.DATE_POLICY_YYYY)

        assertEquals(year.toString(), result, "yyyy 형태 경로 불일치")

        result = generateDirNameByDatePolicy(DirPathPolicy.DATE_POLICY_YYYY_MM)

        assertEquals(year.toString() + File.separator + month.toString().padStart(2, '0'), result, "yyyy/mm 형태 경로 불일치")

        result = generateDirNameByDatePolicy(DirPathPolicy.DATE_POLICY_YYYY_MM_DD)

        assertEquals(
            year.toString() + File.separator + month.toString().padStart(2, '0') + File.separator + day.toString()
                .padStart(2, '0'),
            result,
            "yyyy/mm/dd 형태 경로 불일치"
        )

        topLogger.debug("yyyy/mm/dd => {}", result)

        result = generateDirNameByDatePolicy(DirPathPolicy.DATE_POLICY_YYYY_MM, false)

        assertEquals(year.toString() + month.toString().padStart(2, '0'), result, "yyyymm 형태 경로 불일치")

        topLogger.debug("yyyymm => {}", result)
    }

    @Test
    @Order(7)
    @DisplayName("유일한 파일 생성 테스트")
    fun getUniqueFileTest() {
        try {
            var filePath = CommonUtilsTest::class.java.getResource("/sample.ico")?.toURI()

            assertNotNull(filePath, "샘플 파일 없음")

            topLogger.debug("path : {}", filePath)

            val targetFile = File(filePath!!)
            val resultFile = getUniqueFile(targetFile)

//        assertTrue(resultFile.exists(), "신규 파일 생성되지 않음")

            val resultName = FilenameUtils.getName(resultFile.name)

            assertEquals("sample_1_.ico", resultName, "파일명 불일치")

            val reader = FileInputStream(targetFile)
            val write = FileOutputStream(resultFile)

            write.write(reader.readAllBytes())

            write.close()
            reader.close()

            filePath = CommonUtilsTest::class.java.getResource("/$resultName")?.toURI()

            assertNotNull(filePath, "신규 파일 없음")

            topLogger.debug("unique file : {}", filePath)
        } catch (e: IOException) {
            topLogger.error("File write failed.")
        }
    }
}