package net.cliff3.maven.common.util;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * 로케일 관련 유틸리티 클래스
 *
 * @author JoonHo Son
 * @version 1.0.0 2020-07-02 최초 작성
 * @since 1.0.0
 */
@Slf4j
public class LocaleUtil {
    /**
     * 해당 {@link Locale} 정보의 유효성 검사. 인자로 전달되는 {@link Locale} 인스턴스는 <strong>languageCode</strong>와
     * <strong>countryCode</strong>가 모두 포함되어 있어야 한다.
     *
     * @param locale 대상 Locale
     *
     * @return 유효성 여부
     */
    public static boolean isValidLocale(Locale locale) {
        if (locale == null) {
            return false;
        }

        Optional<Locale> result = Stream.of(Locale.getAvailableLocales())
                                        .filter(t -> t.getCountry().equals(locale.getCountry()) && t.getLanguage()
                                                                                                    .equals(locale.getLanguage()))
                                        .findFirst();

        return result.isPresent();
    }

    /**
     * {@link #isValidLocale(Locale)} 참고
     *
     * @param languageCode 언어 코드
     * @param countryCode  나라 코드
     *
     * @return 유효성 여부
     */
    public static boolean isValidLocale(final String languageCode, final String countryCode) {
        return isValidLocale(new Locale(languageCode, countryCode));
    }
}
