package net.cliff3.maven.common.util

import org.slf4j.LoggerFactory

/**
 * 숫자 관련 유틸리티 클래스
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-08-28 최초 작성
 * @since 1.0.0
 */
class NumberUtil {
    companion object {
        private val logger = LoggerFactory.getLogger(NumberUtil::class.java)

        /**
         * 지정된 범위([min], [max]) 내에서 무작위 숫자를 반환한다.
         *
         * @param min 범위 최소값
         * @param max 범위 최대값
         * @return 범위내의 무작위 숫자
         */
        @JvmStatic
        fun getRandomInteger(min: Int, max: Int): Int {
            logger.debug("min : {}, max : {}", min, max);

            return (min..max).shuffled().first()
        }
    }
}