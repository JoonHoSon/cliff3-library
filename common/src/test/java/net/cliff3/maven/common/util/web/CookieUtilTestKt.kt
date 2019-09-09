package net.cliff3.maven.common.util.web

import org.apache.commons.codec.binary.Base64
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals

/**
 * CookieUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
class CookieUtilTestKt {
    private val logger: Logger = LoggerFactory.getLogger(CookieUtilTestKt::class.java)

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var cookie: Cookie

    @BeforeClass
    fun beforeClass() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun addBase64CookieTest() {
        // 이게 맞는건가....?
        logger.debug("------------------------------------------------------------------------")
        logger.debug("start addBase64CookieTest")

        val cookieName:String = "kotlinCookieName"
        val cookieValue:String = "kotlinCookieValue123"

        Mockito.`when`(cookie.name).thenReturn(cookieName)
        Mockito.`when`(request.cookies).thenReturn(arrayOf(cookie))
        Mockito.`when`(cookie.value).thenReturn(Base64.encodeBase64URLSafeString(cookieValue.toByteArray()))

        CookieUtil.addCookie(response = response, name = cookieName, value = cookieValue)

        val result: String = CookieUtil.getCookie(request, cookieName, true)

        assertEquals(cookieValue, result, "쿠키 값(decoding) 반환 실패 [$cookieValue]")
    }
}