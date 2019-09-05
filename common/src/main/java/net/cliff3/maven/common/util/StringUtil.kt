package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DecimalFormat
import java.util.regex.Pattern

/**
 * 문자열 관련 유틸리티 클래스
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-08-30 최초 작성
 * @since 1.0.0
 */
class StringUtil {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StringUtil::class.java)

        /**
         * 랜덤 문자열
         */
        private val randomSource: Array<String> = "0,1,2,3,4,5,6,7,8,9,0,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(
                ",").toTypedArray()

        /**
         * 한글 자음
         */
        private val consonants: CharArray = charArrayOf('ㄱ',
                                                        'ㄲ',
                                                        'ㄴ',
                                                        'ㄷ',
                                                        'ㄸ',
                                                        'ㄹ',
                                                        'ㅁ',
                                                        'ㅂ',
                                                        'ㅃ',
                                                        'ㅅ',
                                                        'ㅆ',
                                                        'ㅇ',
                                                        'ㅈ',
                                                        'ㅉ',
                                                        'ㅊ',
                                                        'ㅋ',
                                                        'ㅌ',
                                                        'ㅍ',
                                                        'ㅎ')

        /**
         * 한글 모음
         */
        private val vowels: CharArray = charArrayOf('ㅏ',
                                                    'ㅐ',
                                                    'ㅑ',
                                                    'ㅒ',
                                                    'ㅓ',
                                                    'ㅔ',
                                                    'ㅕ',
                                                    'ㅖ',
                                                    'ㅗ',
                                                    'ㅘ',
                                                    'ㅙ',
                                                    'ㅚ',
                                                    'ㅛ',
                                                    'ㅜ',
                                                    'ㅝ',
                                                    'ㅞ',
                                                    'ㅟ',
                                                    'ㅠ',
                                                    'ㅡ',
                                                    'ㅢ',
                                                    'ㅣ')

        /**
         * 이메일 정규식
         */
        private const val emailExpression: String = "^[\\w\\-]+(\\.[\\w\\-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$"

        /**
         * 이메일 정규식 [Pattern]. 대소문자 구분 없음.
         */
        private val emailPatternInCaseSensitive: Pattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE)

        /**
         * 이메일 정규식 [Pattern]. 대소문자 구분
         */
        private val emailPatternCaseSensitive: Pattern = Pattern.compile(emailExpression)

        /**
         * URL 정규식. Protocol(http/https)는 옵션임.
         */
        private const val urlExpression: String = "(http(s)?://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+%/\\.\\w가-힣ㄱ-ㅎㅏ]+)?"

        /**
         * URL 정규식. Protocol(http/https)를 포함한 전체 검사.
         */
        private const val urlExpressionWithProtocol: String = "(http(s)?://)[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+%/\\.\\w가-힣ㄱ-ㅎㅏ]+)?"

        /**
         * URL 정규식([urlExpression]) [Pattern]
         */
        private val urlPattern: Pattern = Pattern.compile(urlExpression)

        /**
         * URL 정규식([urlExpressionWithProtocol]) [Pattern]
         */
        private val urlPatternWithProtocol: Pattern = Pattern.compile(urlExpressionWithProtocol)

        /**
         * 통화 표시
         *
         * 소수점 없음
         */
        private val currencyFormatter: DecimalFormat = DecimalFormat("#,##0")

        /**
         * 통화 표시
         *
         * 소수점 하위 두 자리
         */
        private val currencyFormatterWithDecimalPoint = DecimalFormat("#,##0.00")

        /**
         * 이메일 문자열의 유효성 검사 결과를 반환한다.
         *
         * @param target 대상 문자열
         * @param isCaseSensitive 대소문자 구분 여부
         *
         * @return 유효성 검사 결과
         */
        fun isValidEmail(target: String?, isCaseSensitive: Boolean = true): Boolean {
            logger.debug("isValidEmail target : $target")

            target?.apply {
                return if (isCaseSensitive) emailPatternCaseSensitive.matcher(this).matches() else emailPatternInCaseSensitive.matcher(
                        this).matches()
            }

            logger.error("대상 문자열 없음")

            return false
        }

        /**
         * URL 문자열의 유효성 검사 결과를 반환한다.
         *
         * @param target 대상 문자열
         *
         * @return 유효성 검사 결과
         */
        fun isValidURL(target: String?, withProtocol: Boolean = true): Boolean {
            logger.debug("isValidURL target : $target")

            target?.apply {
                return if (withProtocol) urlPatternWithProtocol.matcher(this).matches() else urlPattern.matcher(this).matches()
            }

            logger.error("대상 문자열 없음")

            return false
        }

        /**
         * 대상 문자열을 통화 형식으로 변환하여 반환한다.
         *
         * 빈 문자열 혹은 [NumberFormatException]일 경우 빈 문자열 반환
         *
         * @param target 대상 문자열
         * @param showDecimalPoint 소수점 하위 2자리 표시 여부. [currencyFormatterWithDecimalPoint] 참고.
         *
         * @return 세 자리마다 쉼표가 찍히는 통화 형식의 문자열
         */
        fun currencyFormat(target: String?, showDecimalPoint: Boolean = false): String {
            logger.debug("currencyFormat target : $target")

            target?.apply {
                return try {
                    if (showDecimalPoint) {
                        currencyFormatterWithDecimalPoint.format(this.toDouble())
                    } else {
                        currencyFormatter.format(this.toDouble())
                    }
                } catch (e: NumberFormatException) {
                    ""
                }
            }

            return ""
        }
    }
}