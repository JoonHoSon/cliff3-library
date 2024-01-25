package net.cliff3.maven.common.util

import net.cliff3.maven.common.logger
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

/**
 * StringUtilsTest
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
class StringUtilsTest {
    private val log = logger();

    @Test
    @Order(1)
    @DisplayName("통화 금액(3자리 쉼표) 테스트")
    fun addThousandSeparatorTest() {
        val target1 = "abc"
        val target2 = "72300"
        val target3 = "-123000"

        assertThrows<NumberFormatException> { target1.formatCurrency() }
        assertEquals("723,010", target2.formatCurrency(), "세 자리 콤마 출력 실패")
    }
}