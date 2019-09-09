package net.cliff3.maven.common.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * CookieUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-09 최초 작성
 * @since 1.0.0
 */
public class CookieUtilTest {
    @Mock()
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Cookie cookie;

    @BeforeClass
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addNormalCookieTest() {
        // 이게 맞는건가....?
        final String _cookieValue = "test123";
        final String _cookieName = "cookieKey123";

        Mockito.when(cookie.getName()).thenReturn(_cookieName);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        Mockito.when(cookie.getValue()).thenReturn(_cookieValue);

        CookieUtil.addCookie(response, _cookieName, _cookieValue);

        final String _result = CookieUtil.getCookie(request, _cookieName, false);

        Assert.assertEquals(_result, _cookieValue, "쿠키 반환 실패");
    }
}
