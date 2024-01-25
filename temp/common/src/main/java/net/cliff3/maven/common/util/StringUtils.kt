package net.cliff3.maven.common.util

import java.text.DecimalFormat
import java.util.regex.Pattern


private val RANDOM: Array<String> = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

private const val APPLY_MASK = "*"

private val DECIMAL_FORMAT = DecimalFormat("#,###")

/**
 * 한글 자음
 */
private val KO_CONSONANTS: Array<Char> = arrayOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

/**
 * 한글 모음
 */
private val KO_VOWELS: Array<Char> = arrayOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ')

/**
 * 한글 받침
 */
private val KO_FINAL_CONSONANTS: Array<Char> = arrayOf(0.toChar(), 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
        'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

/**
 * 자음 분해(된소리 포함)
 */
private val KO_SEPARATION_CONSONANTS: Array<Array<Char>> = arrayOf(arrayOf('ㄱ'), arrayOf('ㄱ', 'ㄱ'), arrayOf('ㄴ'), arrayOf('ㄷ'), arrayOf('ㄷ', 'ㄷ'), arrayOf('ㄹ'), arrayOf('ㅁ'), arrayOf('ㅂ'), arrayOf('ㅂ', 'ㅂ'), arrayOf('ㅅ'), arrayOf('ㅅ', 'ㅅ'), arrayOf('ㅇ'),
        arrayOf('ㅈ'), arrayOf('ㅈ', 'ㅈ'), arrayOf('ㅊ'), arrayOf('ㅋ'), arrayOf('ㅌ'), arrayOf('ㅍ'), arrayOf('ㅎ'))

/**
 * 모음 분해
 */
private val KO_SEPARATION_VOWELS: Array<Array<Char>> = arrayOf(arrayOf('ㅏ'), arrayOf('ㅐ'), arrayOf('ㅑ'), arrayOf('ㅒ'), arrayOf('ㅓ'), arrayOf('ㅔ'), arrayOf('ㅕ'), arrayOf('ㅖ'), arrayOf('ㅗ'), arrayOf('ㅗ', 'ㅏ'), arrayOf('ㅗ', 'ㅐ'), arrayOf('ㅗ', 'ㅣ'), arrayOf('ㅛ'),
        arrayOf('ㅜ'), arrayOf('ㅜ', 'ㅓ'), arrayOf('ㅜ', 'ㅔ'), arrayOf('ㅜ', 'ㅣ'), arrayOf('ㅠ'), arrayOf('ㅡ'), arrayOf('ㅡ', 'ㅣ'), arrayOf('ㅣ'))

/**
 * 한글 받침 분해
 */
private val KO_SEPARATION_FINAL_CONSONANTS = arrayOf(arrayOf(), arrayOf('ㄱ'), arrayOf('ㄱ', 'ㄱ'), arrayOf('ㄱ', 'ㅅ'), arrayOf('ㄴ'), arrayOf('ㄴ', 'ㅈ'), arrayOf('ㄴ', 'ㅎ'), arrayOf('ㄷ'), arrayOf('ㄹ'), arrayOf('ㄹ', 'ㄱ'), arrayOf('ㄹ', 'ㅁ'),
        arrayOf('ㄹ', 'ㅂ'), arrayOf('ㄹ', 'ㅅ'), arrayOf('ㄹ', 'ㅌ'), arrayOf('ㄹ', 'ㅍ'), arrayOf('ㄹ', 'ㅎ'), arrayOf('ㅁ'), arrayOf('ㅂ'), arrayOf('ㅂ', 'ㅅ'), arrayOf('ㅅ'), arrayOf('ㅅ', 'ㅅ'),
        arrayOf('ㅇ'), arrayOf('ㅈ'), arrayOf('ㅊ'), arrayOf('ㅋ'), arrayOf('ㅌ'), arrayOf('ㅍ'), arrayOf('ㅎ'))

/**
 * 한글 쌍자음/이중 모음 분해
 */
private val KO_SEPARATION_FORTES_VOWELS = arrayOf(arrayOf('ㄱ'), arrayOf('ㄱ', 'ㄱ'), arrayOf('ㄱ', 'ㅅ'), arrayOf('ㄴ'), arrayOf('ㄴ', 'ㅈ'), arrayOf('ㄴ', 'ㅎ'), arrayOf('ㄷ'), arrayOf('ㄸ'), arrayOf('ㄹ'), arrayOf('ㄹ', 'ㄱ'),
        arrayOf('ㄹ', 'ㅁ'), arrayOf('ㄹ', 'ㅂ'), arrayOf('ㄹ', 'ㅅ'), arrayOf('ㄹ', 'ㄷ'), arrayOf('ㄹ', 'ㅍ'), arrayOf('ㄹ', 'ㅎ'), arrayOf('ㅁ'), arrayOf('ㅂ'), arrayOf('ㅂ', 'ㅂ'),
        arrayOf('ㅂ', 'ㅅ'), arrayOf('ㅅ'), arrayOf('ㅅ', 'ㅅ'), arrayOf('ㅇ'), arrayOf('ㅈ'), arrayOf('ㅈ', 'ㅈ'), arrayOf('ㅊ'), arrayOf('ㅋ'), arrayOf('ㅌ'), arrayOf('ㅍ'), arrayOf('ㅎ'), arrayOf('ㅏ'), arrayOf('ㅐ'),
        arrayOf('ㅑ'), arrayOf('ㅒ'), arrayOf('ㅓ'), arrayOf('ㅔ'), arrayOf('ㅕ'), arrayOf('ㅖ'), arrayOf('ㅗ'), arrayOf('ㅗ', 'ㅏ'), arrayOf('ㅗ', 'ㅐ'), arrayOf('ㅗ', 'ㅣ'), arrayOf('ㅛ'), arrayOf('ㅜ'),
        arrayOf('ㅜ', 'ㅓ'), arrayOf('ㅜ', 'ㅔ'), arrayOf('ㅜ', 'ㅣ'), arrayOf('ㅠ'), arrayOf('ㅡ'), arrayOf('ㅡ', 'ㅣ'), arrayOf('ㅣ'))

private const val EMAIL_EXPRESSION = "^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$"

private const val URL_EXPRESSION = "(http(s?)://)?[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+)+(/[#&\\n\\-=?+%/.\\w가-힣ㄱ-ㅎㅏ]*)?"

private val URL_PATTERN = Pattern.compile(URL_EXPRESSION, Pattern.CASE_INSENSITIVE)

/**
 * 이메일 유효성 검사
 *
 * @param target 대상 문자열
 * @param isCaseSensitive 대소문자 구문 여부. 생략시 대소문자 구분하지 않음.
 *
 * @return 유효성 검사 결과
 */
fun isValidEmail(target: String, isCaseSensitive: Boolean = false): Boolean {
    val pattern: Pattern = when {
        isCaseSensitive -> Pattern.compile(EMAIL_EXPRESSION)
        else -> Pattern.compile(EMAIL_EXPRESSION, Pattern.CASE_INSENSITIVE)
    }

    return pattern.matcher(target).matches()
}

/**
 * URL 문자열의 유효성 검사 결과를 반환한다
 *
 * @param target 대상 문자열
 *
 * @return 유효성 검사 결과
 */
fun isValidURL(target: String): Boolean {
    return URL_PATTERN.matcher(target).matches()
}

/**
 * 문자열로 전달된 금액을 3자리 쉼표로 반환
 *
 * @throws NumberFormatException 숫자가 아님
 */
@Throws(NumberFormatException::class)
fun String.formatCurrency(): String {
    // FIXME(joonho): 2023-08-25 double 이외의 형식은??
    return DECIMAL_FORMAT.format(this.toDouble())
}

/**
 * 문자열로 전달된 금액을 3자리 쉼표로 반환
 *
 * @param precision 소수점 자리수
 * @throws NumberFormatException 숫자가 아님
 */
@Throws(NumberFormatException::class)
fun String.formatCurrency(precision: Int = 1): String? {
    // FIXME(joonho): 2023-08-25 double 이외의 형식은??
    val formatter = DecimalFormat("#,###.".padEnd(precision + 6, '0'))

    return formatter.format(this.toDouble())
}