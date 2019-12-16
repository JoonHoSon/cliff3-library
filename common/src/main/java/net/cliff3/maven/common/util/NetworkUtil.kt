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
    /**
     * Companion object
     */
    companion object {
        /**
         * [Logger]
         */
        private val logger: Logger = LoggerFactory.getLogger(NetworkUtil::class.java)

        /**
         * IP4 클래스 단위 패턴
         */
        private const val IP_PATTERN: String = "([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-4])"

        /**
         * IP4 아이피 전체 패턴
         */
        private val IP_FULL_PATTERN: String = String.format("%s\\.%s\\.%s\\.%s",
                                                            IP_PATTERN,
                                                            IP_PATTERN,
                                                            IP_PATTERN,
                                                            IP_PATTERN)

        /**
         * [IP_FULL_PATTERN] [Pattern]
         */
        private val IP_REGEXP: Pattern = Pattern.compile(IP_FULL_PATTERN)

        /**
         * 대상 아이피 주소 문자열의 유효성 여부를 확인한다.
         *
         * @param ip 아이피 주소 문자열
         *
         * @return 유효성 여부
         */
        @JvmStatic
        fun checkValidIpV4Address(ip: String?): Boolean {
            logger.debug("target ip : {}", ip)

            ip?.apply {
                return IP_REGEXP.matcher(ip).matches()
            }

            return false
        }
    }
}