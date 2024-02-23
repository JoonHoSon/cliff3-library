package net.cliff3.maven.web.common

import org.apache.commons.lang3.StringUtils

/**
 * 쿠키 기본 유지 시간(하루)
 */
const val COOKIE_DEFAUL_MAX_AGE = 86_400

/**
 * 쿠키 기본 설정 경로
 */
const val COOKIE_DEFAULT_PATH = "/"

private fun isValidNameValue(name: String?, value: String?): Boolean {
    if (name == "null" || value == "null") {
        return false
    }

    return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value);
}