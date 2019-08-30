package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

/**
 * 네트워크 관련 유틸리티
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-08-30 최초 작성
 * @since 1.0.0
 */
class NetworkUtil {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NetworkUtil::class.java)

        private const val IP_PATTERN: String = "([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-4])"

        private val IP_FULL_PATTERN: String = String.format("%s\\.%s\\.%s\\.%s",
                                                            IP_PATTERN,
                                                            IP_PATTERN,
                                                            IP_PATTERN,
                                                            IP_PATTERN)

        private val IP_REGEXP = Pattern.compile(IP_FULL_PATTERN)

        fun checkValidIpV4Address(ip: String?): Boolean {
            logger.debug("target ip : {}", ip)

            ip?.apply {
                return IP_REGEXP.matcher(ip).matches()
            }

            return false
        }
    }
}