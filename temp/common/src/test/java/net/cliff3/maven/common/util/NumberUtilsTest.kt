package net.cliff3.maven.common.util

import net.cliff3.maven.common.logger
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertNotEquals

/**
 * NumberUtilsTest
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
class NumberUtilsTest {
    private val log = logger()

    @Test
    @Order(1)
    @DisplayName("Random integer test")
    fun getRandomIntegerTest() {
        val first = getRandomInteger(min = 10, max = 99)
        val second = getRandomInteger(min=10, max = 99)
        val third = getRandomInteger(10, 99)

        assertNotEquals(first, second, "랜덤 생성 실패")
        assertNotEquals(first, third, "랜덤 생성 실패")
        assertNotEquals(second, third, "랜덤 생성 실패")
    }
}