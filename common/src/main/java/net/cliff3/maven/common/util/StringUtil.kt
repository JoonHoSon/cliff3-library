package net.cliff3.maven.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DecimalFormat

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
         * URL 정규식
         */
        private const val urlExpression: String = "(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+%/\\.\\w가-힣ㄱ-ㅎㅏ]+)?"

        /**
         * 통화 표시
         *
         * 소수점 없음
         */
        private val currencyFormat: DecimalFormat = DecimalFormat("#,##0")

        /**
         * 통화 표시
         *
         * 소수점 하위 두 자리
         */
        private val currencyFormatWithDecimalPoint = DecimalFormat("#,##0.00")
    }
}