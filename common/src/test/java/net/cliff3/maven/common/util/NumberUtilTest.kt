package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import kotlin.test.assertTrue

/**
 * NumberUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-08-30 최초 작성
 * @since 1.0.0
 */
class NumberUtilTest {
    private val logger: Logger = LoggerFactory.getLogger(NumberUtilTest::class.java)

    @Test
    fun getRandomIntegerTest() {
        val min = 10
        val max = 100
        var result:Int

        for (x in 0..100) {
            result = NumberUtil.getRandomInteger(min, max)

            logger.debug("result -> {}", result)

            assertTrue(result >= 10, "최소값 보다 작음")
            assertTrue(result <= 100, "최대값 보다 큼")
        }
    }
}