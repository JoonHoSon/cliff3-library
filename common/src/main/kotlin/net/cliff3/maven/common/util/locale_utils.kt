package net.cliff3.maven.common.util

import net.cliff3.maven.common.topLogger
import java.util.*

/**
 * 언어 설정 관련 함수 모음
 *
 * @author JoonHo Son
 * @since 0.3.0
 */

/**
 * 해당 [Locale] 정보의 유효성 검사
 *
 * @param locale 대상 [Locale] 정보
 * @return 유효성 검사 결과
 */
fun isValidLocale(locale: Locale?): Boolean {
    if (locale == null) {
        return false
    }

    val upperCountryCode = (locale.country ?: "").uppercase()

    return Locale.getAvailableLocales().any {
        it.country.uppercase() == upperCountryCode && it.language.lowercase() == locale.language
    }
}

/**
 * [isValidLocale] 참고
 *
 * @param languageCode 언어 코드
 * @param countryCode 국가 코드
 *
 * @return 유효성 검사 결과
 */
fun isValidLocale(languageCode: String, countryCode: String?): Boolean {
    topLogger.debug("------------------------------------------------------------------------")
    topLogger.debug("Start compare language code and country code")
    topLogger.debug("Target country code : {}", (countryCode ?: "").uppercase())
    topLogger.debug("Target language code : {}", languageCode.lowercase())

    val upperCountryCode = (countryCode ?: "").uppercase()

    return Locale.getAvailableLocales().any {
        topLogger.debug("Loop language code : {}, country code : {}", it.language.lowercase(), it.country.lowercase())

        it.country.uppercase() == upperCountryCode && it.language.lowercase() == languageCode.lowercase()
    }
}