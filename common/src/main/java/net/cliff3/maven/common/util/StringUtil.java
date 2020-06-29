package net.cliff3.maven.common.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 문자열 관련 utility class.
 * 한글 초/중/종성 분리 관련 소스의 출처는 <a href="https://gs.saro.me/#!m=elec&jn836">가사시니</a> 입니다.
 *
 * @author JoonHo Son
 * @version 1.0.0 2020-06-26 최초 작성
 * @since 1.0.0
 */
@Slf4j
public class StringUtil {
    private static final String[] RANDOM_SOURCE;

    private static final String APPLY_MASK = "*";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    /**
     * 한글 자음
     */
    private static final char[] KO_CONSONANTS = {
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    /**
     * 한글 모음
     */
    private static final char[] KO_VOWELS = {
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    };

    /**
     * 한글 받침
     */
    private static final char[] KO_FINAL_CONSONANTS = {
        0, 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
        'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    /**
     * 자음 분해(된소리 포함)
     */
    private static final char[][] KO_SEPARATION_CONSONANTS = {
        {'ㄱ'}, {'ㄱ', 'ㄱ'}, {'ㄴ'}, {'ㄷ'}, {'ㄷ', 'ㄷ'}, {'ㄹ'}, {'ㅁ'}, {'ㅂ'}, {'ㅂ', 'ㅂ'}, {'ㅅ'}, {'ㅅ', 'ㅅ'}, {'ㅇ'},
        {'ㅈ'}, {'ㅈ', 'ㅈ'}, {'ㅊ'}, {'ㅋ'}, {'ㅌ'}, {'ㅍ'}, {'ㅎ'}
    };

    /**
     * 모음 분해
     */
    private static final char[][] KO_SEPARATION_VOWELS = {
        {'ㅏ'}, {'ㅐ'}, {'ㅑ'}, {'ㅒ'}, {'ㅓ'}, {'ㅔ'}, {'ㅕ'}, {'ㅖ'}, {'ㅗ'}, {'ㅗ', 'ㅏ'}, {'ㅗ', 'ㅐ'}, {'ㅗ', 'ㅣ'}, {'ㅛ'},
        {'ㅜ'}, {'ㅜ', 'ㅓ'}, {'ㅜ', 'ㅔ'}, {'ㅜ', 'ㅣ'}, {'ㅠ'}, {'ㅡ'}, {'ㅡ', 'ㅣ'}, {'ㅣ'}
    };

    /**
     * 한글 받침 분해
     */
    private static final char[][] KO_SEPARATION_FINAL_CONSONANTS = {
        {}, {'ㄱ'}, {'ㄱ', 'ㄱ'}, {'ㄱ', 'ㅅ'}, {'ㄴ'}, {'ㄴ', 'ㅈ'}, {'ㄴ', 'ㅎ'}, {'ㄷ'}, {'ㄹ'}, {'ㄹ', 'ㄱ'}, {'ㄹ', 'ㅁ'},
        {'ㄹ', 'ㅂ'}, {'ㄹ', 'ㅅ'}, {'ㄹ', 'ㅌ'}, {'ㄹ', 'ㅍ'}, {'ㄹ', 'ㅎ'}, {'ㅁ'}, {'ㅂ'}, {'ㅂ', 'ㅅ'}, {'ㅅ'}, {'ㅅ', 'ㅅ'},
        {'ㅇ'}, {'ㅈ'}, {'ㅊ'}, {'ㅋ'}, {'ㅌ'}, {'ㅍ'}, {'ㅎ'}
    };

    /**
     * 한글 쌍자음/이중 모음 분해
     */
    private static final char[][] KO_SEPARATION_FORTES_VOWELS = {
        {'ㄱ'}, {'ㄱ', 'ㄱ'}, {'ㄱ', 'ㅅ'}, {'ㄴ'}, {'ㄴ', 'ㅈ'}, {'ㄴ', 'ㅎ'}, {'ㄷ'}, {'ㄸ'}, {'ㄹ'}, {'ㄹ', 'ㄱ'},
        {'ㄹ', 'ㅁ'}, {'ㄹ', 'ㅂ'}, {'ㄹ', 'ㅅ'}, {'ㄹ', 'ㄷ'}, {'ㄹ', 'ㅍ'}, {'ㄹ', 'ㅎ'}, {'ㅁ'}, {'ㅂ'}, {'ㅂ', 'ㅂ'},
        {'ㅂ', 'ㅅ'}, {'ㅅ'}, {'ㅅ', 'ㅅ'}, {'ㅇ'}, {'ㅈ'}, {'ㅈ', 'ㅈ'}, {'ㅊ'}, {'ㅋ'}, {'ㅌ'}, {'ㅍ'}, {'ㅎ'}, {'ㅏ'}, {'ㅐ'},
        {'ㅑ'}, {'ㅒ'}, {'ㅓ'}, {'ㅔ'}, {'ㅕ'}, {'ㅖ'}, {'ㅗ'}, {'ㅗ', 'ㅏ'}, {'ㅗ', 'ㅐ'}, {'ㅗ', 'ㅣ'}, {'ㅛ'}, {'ㅜ'},
        {'ㅜ', 'ㅓ'}, {'ㅜ', 'ㅔ'}, {'ㅜ', 'ㅣ'}, {'ㅠ'}, {'ㅡ'}, {'ㅡ', 'ㅣ'}, {'ㅣ'}
    };

    static {
        RANDOM_SOURCE = "0,1,2,3,4,5,6,7,8,9,0,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z"
            .split(
                ",");
    }

    /**
     * 이메일 문자열의 유효성 검사 결과를 반한다.
     *
     * @param target          대상 문자열
     * @param isCaseSensitive 대소문자 구분 여부
     *
     * @return 유효성 검사 결과
     * @throws IllegalArgumentException 대상 문자열이 없을 경우 발생
     */
    public static boolean isValidEmail(String target, boolean isCaseSensitive) {
        if (StringUtils.isEmpty(target)) {
            throw new IllegalArgumentException("이메일 유효성 검사 실패 : 대상 문자열 없음");
        }

        final String expression = "^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        final Pattern pattern = (isCaseSensitive)? Pattern.compile(expression): Pattern.compile(expression,
                                                                                                Pattern.CASE_INSENSITIVE);

        return pattern.matcher(target).matches();
    }

    /**
     * URL 문자열의 유효성 검사 결과를 반환한다.
     *
     * @param target 대상 문자열
     *
     * @return 유효성 검사 결과
     * @throws IllegalArgumentException 대상 문자열이 없을 경우 발생
     */
    public static boolean isValidURL(String target) {
        if (StringUtils.isEmpty(target)) {
            throw new IllegalArgumentException("URl 유효성 검사 실패 : 대상 문자열 없음");
        }
        final String expression = "(http(s?)://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?+%/.\\w가-힣ㄱ-ㅎㅏ]*)?";
        final Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);

        return pattern.matcher(target).matches();
    }

    /**
     * 대상 문자열을 세 자리마다 쉼표를 추가하여 반환. {@link DecimalFormat#format(Object)}에서 {@link IllegalArgumentException}이
     * 발생하거나 대상 문자열이 없을 경우({@code null} 혹인 빈 문자열) {@link Optional#empty()} 반환.
     *
     * <pre>
     *     {@code
     *     final String v1 = ""; // 혹은 null
     *     Optional<String> result = StringUtil.addThousandSeparator(v1);
     *     // result는 Optional.empty()
     *
     *     final String v2 = "abc"; // IllegalArgumentException 발생
     *     result = StringUtil.addThousandSeparator(v2);
     *     // result는 Optional.empty()
     *     }
     * </pre>
     *
     * @param target 대상 문자열
     *
     * @return 세 자리마다 쉼표가 찍히는 숫자 형태의 문자열
     */
    public static Optional<String> addThousandSeparator(String target) {
        log.debug("세 자리 처리 대상 : {}", target);

        try {
            return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(DECIMAL_FORMAT::format);
        } catch (Exception e) {
            log.error("세자리 쉼표 추가 오류", e);

            return Optional.empty();
        }
    }

    /**
     * 대상 문자열을 16진수(Hex) 형태로 변환하여 반환. 대상 문자열이 없을 경우({@code null} 혹은 빈 문자열) {@link Optional#empty()}
     * 반환.
     *
     * @param target 대상 문자열
     *
     * @return 16진수로 변환된 문자열
     */
    private static Optional<String> stringToHex(String target) {
        log.debug("16진수 출력 대상 : {}", target);

        try {
            return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
                StringBuilder builder = new StringBuilder();
                int length = t.length();

                for (int i = 0; i < length; i++) {
                    builder.append(String.format("%02x", (int)t.charAt(i)));
                }

                return builder.toString();
            });
        } catch (Exception e) {
            log.error("16진수 변환 실패", e);

            return Optional.empty();
        }
    }

    /**
     * 대상 문자열을 16진수(Hex) 형태로 변환하여 반환한다. {@code hasPrefix} 인자에 따라 접두어 <strong>0x</strong>를 함께 반환한다.
     *
     * @param target 대상 문자열
     *
     * @return 16진수로 변환된 문자열
     * @throws IllegalArgumentException 대상 문자열이 없을 경우 발생
     * @see #stringToHex(String)
     */
    public static Optional<String> stringToHex(String target, boolean hasPrefix) {
        Optional<String> temp = stringToHex(target);

        return temp.map(s -> (hasPrefix)? "0x" + s: s);
    }

    /**
     * 지정된 길이만큼의 무작위 문자열을 생성하여 반환한다. 생성 대상 문자는 숫자(0~9) 및 알파벳(대소문자 모두)이며, 인자로 전달된 길이가
     * 잘못된 경우({@code null} 혹은 1보다 작은 경우) {@link Optional#empty()} 반환.
     *
     * @param length 무작위 문자열 길이
     *
     * @return 생성된 임의의 문자열
     */
    public static Optional<String> generateRandomString(Integer length) {
        log.debug("무작위 문자열 생성 길이 : {}", length);

        return Optional.ofNullable(length).filter(t -> t > 0).map(t -> {
            Random random = new Random();
            int randomSourceLength = RANDOM_SOURCE.length;
            int count = 0;
            StringBuilder builder = new StringBuilder();

            while (count < t) {
                builder.append(RANDOM_SOURCE[random.nextInt(randomSourceLength)]);

                count++;
            }

            return builder.toString();
        });
    }

    /**
     * 기준 문자열 배열에서 두 번째 인자로 전달된 값과 일치할 경우 반환하며, 일치하지 않을 경우 지정된 기본 문자열을 반환한다.
     *
     * <pre>
     *     {@code
     *     String defaultList = {"a", "B", "c"};
     *     Optional<String> result = StringUtil.checkValueInDefaultList(defaultList, "a", "not found", true); // "a" 반환
     *     Optional<String> result = StringUtil.checkValueInDefaultList(defaultList, "b", "not found", true); // "not found" 반환
     *     Optional<String> result = StringUtil.checkValueInDefaultList(defaultList, "f", "not found", true); // "not found" 반환
     *     Optional<String> result = StringUtil.checkValueInDefaultList(null, "c", "not found", true); // "not found" 반환
     *     Optional<String> result = StringUtil.checkValueInDefaultList(defaultList, null, "not found", true); // "not found" 반환
     *     Optional<String> result = StringUtil.checkValueInDefaultList(defaultList, "a", null, false); // Optional.empty() 반환
     *     }
     * </pre>
     *
     * @param defaultList           기준 문자열 배열
     * @param targetValue           비교 대상 문자열
     * @param defaultValue          일치하는 값이 없을 경우 반환할 문자열
     * @param isIgnoreCaseSensitive 대소문자 구분 여부
     *
     * @return 일치되는 문자열 혹은 기본 문자열
     * @throws IllegalArgumentException {@code defaultList}, {@code targetValue} 및 {@code defaultValue}중 누락된 값이 존재할 경우 발생
     */
    public static Optional<String> checkValueInDefaultList(String[] defaultList,
                                                           String targetValue,
                                                           String defaultValue,
                                                           boolean isIgnoreCaseSensitive) {
        Optional<String[]> _defaultList = Optional.ofNullable(defaultList).filter(t -> t.length > 0);
        Optional<String> _targetValue = Optional.ofNullable(targetValue).filter(StringUtils::isNotEmpty);
        Optional<String> _defaultValue = Optional.ofNullable(defaultValue).filter(StringUtils::isNotEmpty);
        boolean isEmpty = false;

        if (!_defaultList.isPresent()) {
            log.warn("기준 문자열 배열이 지정되지 않음");

            return _defaultValue;
        }

        if (!_targetValue.isPresent()) {
            log.warn("비교 대상 문자열이 지정되지 않음");

            return _defaultValue;
        }

        if (!_defaultValue.isPresent()) {
            log.warn("기본 문자열이 지정되지 않음");

            return Optional.empty();
        }

        Optional<String> result;

        if (!isIgnoreCaseSensitive) {
            result = Arrays.stream(_defaultList.get())
                           .filter(t -> t.equals(_targetValue.get()))
                           .findFirst();
        } else {
            result = Arrays.stream(_defaultList.get())
                           .filter(t -> t.toLowerCase().equals(_targetValue.get().toLowerCase()))
                           .findFirst();
        }

        return (result.isPresent())? result: _defaultValue;
    }

    /**
     * 지정된 문자열 마스킹 처리. 시작 인덱스({@code startMaskingIndex})를 기준으로 전달된 mask 문자열로 치환하여 반환한다.
     * 대상 문자열의 값이 없거나({@code null} 또는 빈 문자열) 또는 마스킹 시작 인덱스가 문자열의 길이보다 크거나 0보다 작을 경우
     * {@link Optional#empty()}를 반환한다. 마스킹 문자({@code mask})가 없을 경우({@code null} 또는 빈 문자열)
     * {@link #APPLY_MASK}로 처리한다.
     *
     * @param target            대상 문자열
     * @param startMaskingIndex 마스킹 시작 인덱스(화면 노출 문자열 개수)
     * @param mask              마스크 문자
     *
     * @return 마스킹 처리된 문자열
     * @see #appendMaskingData(String, String, int, int, int)
     */
    public static Optional<String> masking(String target, int startMaskingIndex, String mask) {
        return Optional.ofNullable(target)
                       .filter(t -> StringUtils.isNotEmpty(t) &&
                           startMaskingIndex >= 0 &&
                           startMaskingIndex < target.length() - 1)
                       .map(t -> {
                           int length = t.length();
                           StringBuilder builder = appendMaskingData(t,
                                                                     mask,
                                                                     startMaskingIndex,
                                                                     startMaskingIndex,
                                                                     length);

                           final String result = builder.toString();

                           if (log.isDebugEnabled()) {
                               log.debug("마스킹 처리 결과 : {}", result);
                           }

                           return result;
                       });
    }

    /**
     * 지정된 문자열 마스킹 처리. 시작 인덱스({@code startMaskingIndex})를 기준으로 지정된 길이({@code maskingLength})만큼만
     * mask 문자열로 치환하여 반환한다. 대상 문자열의 값이 없을 경우({@code null} 또는 빈 문자열), 마스킹 시작 인덱스가 문자열의 길이보다
     * 크거나 0보다 작을 경우, 변환할 길이가 0보다 작을 경우 또는 시작 인덱스와 변환 길이의 합이 문자열보다 클 경우
     * {@link Optional#empty()}를 반환한다. 마스킹 문자({@code mask})가 없을 경우({@code null} 또는 빈 문자열)
     * {@link #APPLY_MASK}로 처리한다.
     *
     * @param target            대상 문자열
     * @param startMaskingIndex 마스킹 시작 인덱스
     * @param maskingLength     마스킹 처리할 길이
     * @param mask              마스크 문자
     *
     * @return 마스킹 처리된 문자열
     * @see #appendMaskingData(String, String, int, int, int)
     */
    public static Optional<String> masking(String target, int startMaskingIndex, int maskingLength, String mask) {
        return Optional.ofNullable(target)
                       .filter(t -> StringUtils.isNotEmpty(t) &&
                           startMaskingIndex >= 0 &&
                           startMaskingIndex < target.length() &&
                           target.length() > startMaskingIndex + maskingLength)
                       .map(t -> {
                           int length = t.length();
                           StringBuilder builder = appendMaskingData(t, mask, startMaskingIndex, 0, maskingLength);

                           builder.append(t, startMaskingIndex + maskingLength, length);

                           final String result = builder.toString();

                           if (log.isDebugEnabled()) {
                               log.debug("마스킹 처리 결과 : {}", result);
                           }

                           return result;
                       });
    }

    /**
     * {@link #masking(String, int, String)} 함수와 {@link #masking(String, int, int, String)} 함수의 실제 마스킹 처리
     *
     * @param target     대상 문자열
     * @param mask       마스킹 문자
     * @param startIndex 마스킹 시작 인덱스
     * @param loopStart  마스킹 처리 시작 인덱스
     * @param limit      마스킹 처리 길이
     *
     * @return 처리 결과
     */
    private static StringBuilder appendMaskingData(final String target,
                                                   final String mask,
                                                   final int startIndex,
                                                   final int loopStart,
                                                   final int limit) {
        StringBuilder builder = new StringBuilder();
        final String applyMask = (StringUtils.isNotEmpty(mask))? mask: APPLY_MASK;
        int length = target.length();

        builder.append(target, 0, startIndex);

        for (int i = loopStart; i < limit; i++) {
            builder.append(applyMask);
        }

        return builder;
    }

    /**
     * 한글을 포함하는 문자열에서 초성만을 추출하여 문자열로 반환. 대상 문자열이 없을 경우({@code null} 또는 빈 문자열)
     * {@link Optional#empty()} 반환.
     *
     * <pre>
     *     {@code
     *     Optional<String> result = StringUtil.extractInitialConsonants("세종대왕"); // "ㅅㅈㄷㅇ" 반환
     *     Optional<String> result = StringUtil.extractInitialConsonants("한a글b영c어"); // "ㅎaㄱbㅇcㅇ" 반환
     *     Optional<String> result = StringUtil.extractInitialConsonants(""); // Optional.Empty() 반환
     *     }
     * </pre>
     *
     * @param target 추출 대상 문자열
     *
     * @return 추출 결과
     */
    public static Optional<String> extractInitialConsonants(String target) {
        log.debug("초성 추출 대상 문자열 : {}", target);

        return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
            char[] characters = t.toCharArray();
            int length = characters.length;
            char ch;

            for (int i = 0; i < length; i++) {
                ch = characters[i];

                if (ch >= '가' && ch <= '힣') {
                    characters[i] = KO_CONSONANTS[(ch - '가') / 588];
                }
            }

            return new String(characters);
        });
    }

    /**
     * 한글을 포함하는 문자열에서 초/중/종성으로 분리된 문자열을 반환. 단, 쌍자음, 이중모음은 분리하지 않는다. 쌍자음 및 이중모음까지
     * 모두 분리가 필요할 경우 {@link #separateKoreanConsonantVowelCompletely(String)} 사용.
     * 대상 문자열이 없을 경우({@code null} 또는 빈 문자열) {@link Optional#empty()} 반환.
     *
     * <pre>
     *     {@code
     *     Optional<String> result = StringUtil.separateKoreanConsonantsVowel("고맙습니다");
     *     // "ㄱㅗㅁㅏㅂㅅㅡㅂㄴㅣㄷㅏ" 반환
     *     Optional<String> result = StringUtil.separateKoreanConsonantsVowel("많다");
     *     // "ㅁㅏㄶㄷㅏ" 반환
     *     Optional<String> result = StringUtil.separateKoreanConsonantsVowel("고맙thanks습니다");
     *     // "ㄱㅗㅁㅏㅂthanksㅅㅡㅂㄴㅣㄷㅏ" 반환
     *     }
     * </pre>
     *
     * @param target 대상 문자열
     *
     * @return 초/중/종성이 분리된 문자열
     */
    public static Optional<String> separateKoreanConsonantVowel(String target) {
        log.debug("초/중/종성 분리 대상 문자열 : {}", target);

        return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
            char[] characters = target.toCharArray();
            StringBuilder builder = new StringBuilder();
            int koreanConsonants;

            for (char ch : characters) {
                if (ch >= '가' && ch <= '힣') {
                    koreanConsonants = ch - '가';

                    builder.append(KO_CONSONANTS[koreanConsonants / 588]);
                    builder.append(KO_VOWELS[(koreanConsonants = koreanConsonants % 588) / 28]);

                    if ((koreanConsonants = koreanConsonants % 28) != 0) {
                        builder.append(KO_FINAL_CONSONANTS[koreanConsonants]);
                    }
                } else {
                    builder.append(ch);
                }
            }

            return builder.toString();
        });
    }

    /**
     * 한글을 포함하는 문자열에서 초/중/종성으로 분리된 문자열을 반환. {@link #separateKoreanConsonantVowel(String)}과 달리
     * 이중모음(ㅙ, ㅢ 등)과 쌍자음(ㄶ 등)을 모두 분리한다. 대상 문자열이 없을 경우({@code null} 또는 빈 문자열) {@link Optional#empty()} 반환.
     *
     * <pre>
     *     {@code
     *     Optional<String> result = StringUtil.separateKoreanConsonantVowelCompletely("고맙습니다");
     *     // "ㄱㅗㅁㅏㅂㅅㅡㅂㄴㅣㄷㅏ" 반환
     *     Optional<String> result = StringUtil.separateKoreanConsonantVowelCompletely("많다");
     *     // "ㅁㅏㄴㅎㄷㅏ" 반환
     *     Optional<String> result = StringUtil.separateKoreanConsonantsVowel("고맙thanks습니다");
     *     // "ㄱㅗㅁㅏㅂthanksㅅㅡㅂㄴㅣㄷㅏ" 반환
     *     }
     * </pre>
     *
     * @param target 대상 문자열
     *
     * @return 초/중/종성이 분리된 문자열
     */
    public static Optional<String> separateKoreanConsonantVowelCompletely(String target) {
        return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
            char[] characters = target.toCharArray();
            StringBuilder builder = new StringBuilder();
            int koreanConsonant;

            for (char ch : characters) {
                if (ch >= '가' && ch <= '힣') {
                    koreanConsonant = ch - '가';

                    builder.append(KO_SEPARATION_CONSONANTS[koreanConsonant / 588]);
                    builder.append(KO_SEPARATION_VOWELS[(koreanConsonant = koreanConsonant % 588) / 28]);

                    if ((koreanConsonant = koreanConsonant % 28) != 0) {
                        builder.append(KO_SEPARATION_FINAL_CONSONANTS[koreanConsonant]);
                    }
                } else if (ch >= 'ㄱ' && ch <= 'ㅣ') {
                    builder.append(KO_SEPARATION_FORTES_VOWELS[ch - 'ㄱ']);
                } else {
                    builder.append(ch);
                }
            }

            return builder.toString();
        });
    }
}
