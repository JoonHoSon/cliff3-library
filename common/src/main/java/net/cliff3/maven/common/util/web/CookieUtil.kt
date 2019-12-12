package net.cliff3.maven.common.util.web

import net.cliff3.maven.common.util.crypto.CryptoUtil
import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * CookieUtil
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-07 최초 작성
 * @since 1.0.0
 */
class CookieUtil {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CookieUtil::class.java)

        /**
         * 쿠키 기본 유지 시간(하루)
         */
        private const val DEFAULT_MAX_AGE = 86400;

        /**
         * 쿠키 생성. 지정된 도메인에 해당 이름과 값을 이용하여 지정된 기간만큼 유지되는 쿠키 생성. 쿠키 값은 [Base64.encodeBase64URLSafeString]
         * 처리하여 지정한다.
         *
         * @param response [HttpServletResponse]
         * @param name 쿠키 이름
         * @param value 쿠키 값
         * @param domain 도메인
         * @param path 쿠키 경로
         * @param maxAge 유지기간(초)
         *
         * @see Base64.encodeBase64URLSafeString
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun addCookie(response: HttpServletResponse,
                      name: String,
                      value: String,
                      domain: String?,
                      path: String = "/",
                      maxAge: Int?) {
            logger.debug("cookieName : $name")
            logger.debug("cookieValue : $value")
            logger.debug("domain : $domain")
            logger.debug("path : $path")
            logger.debug("maxAge : $maxAge")

            if (StringUtils.isBlank(value)) {
                throw IllegalArgumentException("Cookie value is empty")
            }

            try {
                val cookie: Cookie = Cookie(name, Base64.encodeBase64URLSafeString(value.toByteArray()))

                domain?.let {
                    logger.debug("cookie domain : $it")
                    cookie.domain = it
                }

                cookie.path = path

                maxAge?.let {
                    cookie.maxAge = maxAge
                }

                response.addCookie(cookie)
            } catch (e: Exception) {
                logger.error("Fail add cookie", e)
            }
        }

        /**
         * [addCookie] 참고
         */
        @JvmStatic
        fun addCookie(response: HttpServletResponse, name: String, value: String) {
            addCookie(response = response, name = name, value = value, domain = null, maxAge = -1)
        }

        /**
         * [addCookie] 참고
         */
        @JvmStatic
        fun addCookie(response: HttpServletResponse, name: String, value: String, domain: String) {
            addCookie(response = response, name = name, value = value, domain = domain, maxAge = -1)
        }

        /**
         * 쿠키 생성. 지정된 도메인에 해당 이름과 값을 이용하여 지정된 기간만큼 유지되는 쿠키 생성. 쿠키 값은 원본 그대로 저장한다.
         *
         * @param response [HttpServletResponse]
         * @param name 쿠키 이름
         * @param value 쿠키 값
         * @param domain 도메인
         * @param path 쿠키 경로
         * @param maxAge 유지시간(초)
         */
        @JvmStatic
        fun addNormalCookie(response: HttpServletResponse,
                            name: String,
                            value: String,
                            domain: String?,
                            path: String = "/",
                            maxAge: Int?) {
            val cookie: Cookie = Cookie(name, value)

            domain?.let {
                cookie.domain = it
            }

            cookie.path = path

            maxAge?.let {
                cookie.maxAge = it
            }

            response.addCookie(cookie)
        }

        /**
         * [addNormalCookie] 참고
         */
        @JvmStatic
        fun addNormalCookie(response: HttpServletResponse, name: String, value: String, domain: String) {
            return addNormalCookie(response = response,
                                   name = name,
                                   value = value,
                                   domain = domain)
        }

        /**
         * [addNormalCookie] 참고
         */
        @JvmStatic
        fun addNormalCookie(response: HttpServletResponse, name: String, value: String) {
            return addNormalCookie(response = response, name = name, value = value, domain = null, maxAge = -1)
        }

        /**
         * 쿠키 값을 반환한다. **isDecoding** 인자를 이용하여 복호화([Base64.decodeBase64]) 여부를 지정한다.
         *
         * @param request [HttpServletRequest]
         * @param name 쿠키 이름
         * @param isDecoding [Base64.decodeBase64] 처리 여부
         *
         * @return 쿠키 값
         * @see Base64.decodeBase64
         */
        @JvmStatic
        fun getCookie(request: HttpServletRequest, name: String, isDecoding: Boolean = false): String {
            val cookies: Array<Cookie>? = request.cookies
            var value: String = ""

            cookies?.forEach loop@{
                if (it.name == name) {
                    try {
                        value = if (isDecoding) {
                            String(Base64.decodeBase64(it.value), StandardCharsets.UTF_8)
                        } else {
                            it.value
                        }
                    } catch (e: UnsupportedEncodingException) {
                        logger.error("쿠키값 반환 중 Base64.decode 실패", e)
                    }

                    return@loop
                }
            }

            return value
        }

        /**
         * [getCookie] 참고
         */
        @JvmStatic
        fun getCookie(request: HttpServletRequest, name: String): String {
            return getCookie(request = request, name = name, isDecoding = false)
        }

        /**
         * 지정된 쿠키를 제거한다. 등록한 쿠키와 [domain] 및 [path]가 일치할 경우에만 삭제된다.
         *
         * @param response [HttpServletResponse]
         * @param name 쿠키 이름
         * @param domain 도메인
         * @param path 쿠키 경로
         */
        fun removeCookie(response: HttpServletResponse, name: String, domain: String?, path: String = "/") {
            val cookie: Cookie = Cookie(name, "")

            cookie.maxAge = 0
            cookie.path = path

            domain?.let {
                cookie.domain = it
            }

            response.addCookie(cookie)
        }

        fun addEncryptedCookie(response: HttpServletResponse,
                               name: String,
                               value: String,
                               domain: String?,
                               path: String = "/",
                               maxAge: Int?,
                               encryptKey: String) {
            logger.debug("cookieName : $name")
            logger.debug("cookieValue : $value")
            logger.debug("domain : $domain")
            logger.debug("path: $path")
            logger.debug("maxAge : $maxAge")
            logger.debug("encryptKey : $encryptKey")

//            try {
//                var encryptedBytes: ByteArray = CryptoUtil.encryptDataByRSA(value.toByteArray(StandardCharsets.UTF_8),
//                                                                            encryptKey)
//
//            }
        }
    }
}