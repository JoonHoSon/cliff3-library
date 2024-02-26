package net.cliff3.maven.web.common

import net.cliff3.maven.common.exception.InvalidArgumentException
import net.cliff3.maven.common.topLogger
import net.cliff3.maven.common.util.crypto.*
import org.apache.commons.lang3.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 쿠키 기본 유지 시간(하루)
 */
const val COOKIE_DEFAULT_MAX_AGE = 86_400

/**
 * 쿠키 기본 설정 경로
 */
const val COOKIE_DEFAULT_PATH = "/"

private val base64Encoder = Base64.getEncoder()

private val base64Decoder = Base64.getDecoder()

private fun isValidNameValue(name: String?, value: String?): Boolean {
    if (name == "null" || value == "null") {
        return false
    }

    return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value);
}

/**
 * 쿠키 등록 처리
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param domain 도메인
 * @param path 경로
 * @param maxAge 쿠키 max age(초)
 * @return 등록된 쿠키
 * @see [COOKIE_DEFAULT_PATH]
 * @see [COOKIE_DEFAULT_MAX_AGE]
 */
private fun doAddCookie(
    response: HttpServletResponse,
    name: String,
    value: String,
    domain: String?,
    path: String?,
    maxAge: Int?
): Cookie {
    val innerPath = path ?: COOKIE_DEFAULT_PATH
    val innerMaxAge = maxAge ?: COOKIE_DEFAULT_MAX_AGE

    topLogger.debug("Cookie name : {}", name)
    topLogger.debug("Cookie value : {}", value)
    topLogger.debug("Cookie domain : {}", domain)
    topLogger.debug("Cookie path : {}", innerPath)
    topLogger.debug("Cookie maxAge : {}", innerMaxAge)

    val cookie: Cookie = Cookie(name, value)

    domain?.let {
        topLogger.debug("Cookie domain : {}", it)

        cookie.domain = it
    }

    cookie.path = innerPath
    cookie.maxAge = innerMaxAge

    response.addCookie(cookie)

    return cookie
}

/**
 * 쿠키 등록. **name** 및 **value**가 유효할 경우에만 처리하며 잘못된 값이 전달될 경우 [InvalidArgumentException] 예외 발생.
 * `path` 및 `maxAge`는 생략될 경우 각각 [COOKIE_DEFAULT_PATH] 및 [COOKIE_DEFAULT_MAX_AGE]로 설정된다.
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param domain 도메인
 * @param path 경로
 * @param maxAge 쿠키 max age(초)
 * @throws [InvalidArgumentException] 잘못된 **name / value**
 * @return 등록된 쿠키
 */
@Throws(InvalidArgumentException::class)
fun addCookie(
    response: HttpServletResponse,
    name: String,
    value: String,
    domain: String?,
    path: String?,
    maxAge: Int?
): Cookie {
    if (isValidNameValue(name, value)) {
        return doAddCookie(response, name, value, domain, path, maxAge)
    } else {
        topLogger.error("쿠키 등록 실패. name 혹은 value 없음")

        throw InvalidArgumentException("Name / Value 잘못 지정됨")
    }
}

/**
 * [addCookie] 참고
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @throws [InvalidArgumentException] 잘못된 **name / value**
 */
@Throws(InvalidArgumentException::class)
fun addCookie(response: HttpServletResponse, name: String, value: String): Cookie {
    return addCookie(response, name, value, null, COOKIE_DEFAULT_PATH, COOKIE_DEFAULT_MAX_AGE)
}

/**
 * 쿠키 등록. 쿠키 값은 `Base64.getEncoder().encodeToString(byte[])` 처리하여 등록한다.
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param domain 도메인
 * @param path 경로
 * @param maxAge 쿠키 max age(초)
 * @return 등록된 쿠키
 * @throws [InvalidArgumentException] 잘못된 **name / value**
 */
@Throws(InvalidArgumentException::class)
fun addCookieWithBase64(
    response: HttpServletResponse,
    name: String,
    value: String,
    domain: String?,
    path: String?,
    maxAge: Int?
): Cookie {
    if (isValidNameValue(name, value)) {
        return doAddCookie(
            response,
            name,
            base64Encoder.encodeToString(value.toByteArray(StandardCharsets.UTF_8)),
            domain,
            path,
            maxAge
        )
    } else {
        topLogger.error("쿠키 등록 실패. name 혹은 value 없음")

        throw InvalidArgumentException("Name / Value 잘못 지정됨")
    }
}

/**
 * [addCookieWithBase64] 참고
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 */
@Throws(InvalidArgumentException::class)
fun addCookieWithBase64(response: HttpServletResponse, name: String, value: String): Cookie {
    return addCookieWithBase64(response, name, value, null, COOKIE_DEFAULT_PATH, COOKIE_DEFAULT_MAX_AGE)
}

/**
 * 지정된 쿠키 이름에 해당하는 값을 반환. 쿠키 이름이 공백일 경우 [InvalidArgumentException] 예외 발생
 *
 * @param request [HttpServletRequest]
 * @param name 쿠키 이름
 * @param isDecoding [Base64] decoding 여부
 * @return 쿠키 값
 * @throws [InvalidArgumentException] 쿠키 이름이 공백일 경우
 */
@Throws(InvalidArgumentException::class)
fun getCookie(request: HttpServletRequest, name: String, isDecoding: Boolean): String? {
    return name.let {
        if (StringUtils.isBlank(name)) {
            throw InvalidArgumentException("쿠키 이름이 잘못 지정됨")
        }

        val target: Cookie? = request.cookies.first { name == it.name }

        target?.let {
            if (isDecoding) {
                return String(base64Decoder.decode(it.value), StandardCharsets.UTF_8)
            } else {
                return it.value
            }
        } ?: ""
    }
}

/**
 * [getCookie] 참고
 *
 * @param request [HttpServletRequest]
 * @param name 쿠키 이름
 * @return 쿠키 값
 * @throws [InvalidArgumentException] 쿠키 이름이 공백일 경우
 */
@Throws(InvalidArgumentException::class)
fun getCookie(request: HttpServletRequest, name: String): String? {
    return getCookie(request, name, false)
}

/**
 * 쿠키 삭제
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param domain 도메인
 * @param path 경로
 */
fun removeCookie(response: HttpServletResponse, name: String, domain: String?, path: String?) {
    return name.let {
        if (StringUtils.isBlank(name)) {
            return
        }

        val target: Cookie = Cookie(name, "")

        target.maxAge = 0

        domain?.let {
            target.domain = it
        }

        path?.let {
            target.path = it
        }

        response.addCookie(target)
    }
}

/**
 * [encryptAES256] 함수를 이용하여 암호화된 쿠키 등록. 쿠키 등록이 정상적으로 처리되면 암호화 결과인 [AESResult]와 생성된 쿠키를
 * [Pair]를 이용하여 반환한다. [AESResult]는 복호화시 필요한 정보를 담고 있으므로 별도로 관리되어야 한다.
 * 암호화 처리 결과는 [Base64]로 인코딩되어 쿠키에 저장한다.
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param secret 암호화 키
 * @param domain 도메인
 * @param path 경로
 * @param maxAge 쿠키 max age(초)
 * @return [AESResult] 및 생성된 [Cookie]
 * @throws [InvalidArgumentException] 공백으로 작성된 암호화 키 혹은 쿠키 이름이 공백일 경우
 * @throws [CryptoException] **AES256** 암호화 처리 오류
 * @see [encryptAES256]
 */
@Throws(InvalidArgumentException::class, CryptoException::class)
fun addCookieWithEncrypt(
    response: HttpServletResponse,
    name: String,
    value: String,
    secret: String,
    domain: String?,
    path: String?,
    maxAge: Int?
): Pair<AESResult, Cookie> {
    return secret.let {
        if (StringUtils.isBlank(secret)) {
            topLogger.error("암호화 키 공백")

            throw InvalidArgumentException("잘못된 암호화 키 지정")
        }

        if (isValidNameValue(name, value)) {
            topLogger.error("쿠키 등록 실패. name 혹은 value 없음")

            throw InvalidArgumentException("Name / Value 잘못 지정됨")
        }

        val result: AESResult = encryptAES256(value, secret)!!
        val encrypted: String = base64Encoder.encodeToString(result.result)

        Pair(result, doAddCookie(response, name, encrypted, domain, path, maxAge))
    }
}

/**
 * [addCookieWithEncrypt] 참고
 *
 * @param response [HttpServletResponse]
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param secret 암호화 키
 * @return [AESResult] 및 생성된 [Cookie]
 * @throws [InvalidArgumentException] 공백으로 작성된 암호화 키 혹은 쿠키 이름이 공백일 경우
 * @throws [CryptoException] **AES256** 암호화 처리 오류
 */
@Throws(InvalidArgumentException::class, CryptoException::class)
fun addCookieWithEncrypt(
    response: HttpServletResponse,
    name: String,
    value: String,
    secret: String
): Pair<AESResult, Cookie> {
    return addCookieWithEncrypt(response, name, value, secret, null, COOKIE_DEFAULT_PATH, COOKIE_DEFAULT_MAX_AGE)
}

/**
 * 지정된 쿠키 이름에 해당하는 값을 복호화 하여 반환.
 *
 * @param request [HttpServletRequest]
 * @param name 쿠키 이름
 * @param secret 암호화 키
 * @param iv Initialize vector
 * @param salt salt
 * @return 복호화 된 쿠키 값
 * @throws [InvalidArgumentException] 공백으로 작성된 암호화 키 혹은 쿠키 이름이 공백일 경우
 * @throws [CryptoException] **AES256** 복호화 오류
 */
@Throws(InvalidArgumentException::class, CryptoException::class)
fun getCookieWithEncrypted(
    request: HttpServletRequest,
    name: String,
    secret: String,
    iv: ByteArray,
    salt: ByteArray
): String? {
    return name.let {
        if (StringUtils.isBlank(it)) {
            topLogger.error("쿠키 이름 공백")

            throw InvalidArgumentException("쿠키 이름 공백")
        }

        val cookie: Cookie? = request.cookies.first { c -> name == c.name }

        cookie?.let { c ->
            val decoded: ByteArray = base64Decoder.decode(c.value)
            val result = decryptAES256(decoded, secret, iv, salt, DEFAULT_REPEAT_COUNT)!!

            result.toString(StandardCharsets.UTF_8)
        }
    }
}