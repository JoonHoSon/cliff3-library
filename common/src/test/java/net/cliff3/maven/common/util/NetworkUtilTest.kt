package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * NetworkUtil
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-08-30 최초 작성
 * @since 1.0.0
 */
class NetworkUtilTest {
    private val logger: Logger = LoggerFactory.getLogger(NetworkUtilTest::class.java)

    @Test
    fun ipCheckTest() {
        val validIp = "192.168.2.5"
        val wrongIp = "128.278.33.5"

        assertTrue(NetworkUtil.checkValidIpV4Address(validIp), "유효성 검사 실패 [$validIp]")
        assertFalse(NetworkUtil.checkValidIpV4Address(wrongIp), "유효성 검사 실패 [$wrongIp]")
        assertFalse(NetworkUtil.checkValidIpV4Address(" "), "유효성 검사 실패 [빈값]")
        assertFalse(NetworkUtil.checkValidIpV4Address(null), "유효성 검사 실패 [null]")
    }
}