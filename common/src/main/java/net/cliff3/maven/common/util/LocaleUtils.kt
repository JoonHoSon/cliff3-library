package net.cliff3.maven.common.util

import net.cliff3.maven.common.topLogger
import java.util.*

/**
 * 해당 [Locale] 정보의 유효성 검사
 *
 * @param locale 대상 Locale
 * @return 유효성 검사 결과
 */
fun isValidLocale(locale: Locale?): Boolean {
    if (locale == null) {
        return false
    }

    val upperCountryCode = (locale.country ?: "").uppercase()

    return Locale.getAvailableLocales().any {
        it.country.equals(upperCountryCode) && it.language.equals(locale.language)
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
    topLogger.debug("target country code : {}", (countryCode ?: "").uppercase())
    topLogger.debug("target language code : {}", languageCode.lowercase())

    val upperCountryCode = (countryCode ?: "").uppercase()


    return Locale.getAvailableLocales().any() {
        topLogger.debug("loop language code : {}, country code : {}", it.language, it.country)

        it.country.equals(upperCountryCode) && it.language.equals(languageCode.lowercase())
    }
}