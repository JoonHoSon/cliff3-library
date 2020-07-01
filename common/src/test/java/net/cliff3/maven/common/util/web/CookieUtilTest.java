package net.cliff3.maven.common.util.web;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.common.util.crypto.AESCrypto;
import net.cliff3.maven.common.util.crypto.CryptoUtil;
import org.apache.commons.codec.binary.Base64;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * CookieUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2020-07-01 최초 작성
 * @since 1.0.0
 */
@Slf4j
public class CookieUtilTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final String name = "testCookie";

    private final String value = "this is a cookie value";

    private final String secret = "password1!";

    private final Charset UTF_8 = StandardCharsets.UTF_8;

    private final Optional<AESCrypto> aesValue = CryptoUtil.encryptAES256(value, secret);

    @BeforeClass
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(groups = "CookieUtilTest")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void addCookieTest() throws UnsupportedEncodingException {
        final String encryptedValue = Base64.encodeBase64URLSafeString(value.getBytes(UTF_8));

        Optional<Cookie> saved = CookieUtil.addCookie(response, name, value);

        assertTrue(saved.isPresent(), "쿠키 생성 실패");

        saved.ifPresent(t -> assertEquals(t.getValue(), value, "저장된 값 불일치"));

        saved = CookieUtil.addCookieWithBase64(response, name, value);

        assertTrue(saved.isPresent(), "base64 쿠키 생성 실패");

        saved.ifPresent(t -> assertEquals(t.getValue(), encryptedValue, "저장된 값 불일치"));

        Optional<AESCrypto> _aesCrypto = CookieUtil.addCookieWithEncrypt(response, name, value, secret);

        assertTrue(_aesCrypto.isPresent(), "암호화 쿠키 저장 실패");

        _aesCrypto.ifPresent(t -> {
            Optional<byte[]> decryptResult = CryptoUtil.decryptAES256(t.getEncrypted(), secret, t.getIv(), t.getSalt());

            assertTrue(decryptResult.isPresent());

            log.debug("복호화된 쿠키값 : {}", new String(decryptResult.get(), UTF_8));

            assertEquals(decryptResult.get(), value.getBytes(UTF_8), "암호화 실패");
        });
    }

    @Test
    public void getCookieTest() {
        Optional<Cookie> saved = CookieUtil.addCookie(response, name, value);

        assertTrue(saved.isPresent(), "쿠키 생성 실패");

        Cookie[] _cookies = new Cookie[]{saved.get()};

        when(request.getCookies()).thenReturn(_cookies);

        Optional<String> result = CookieUtil.getCookie(request, name);

        assertTrue(result.isPresent(), "쿠키 조회 실패");

        result.ifPresent(t -> assertEquals(t, value, "쿠키 조회 실패"));

        // 암/복호화 테스트
        Optional<AESCrypto> _aesCrypto = CryptoUtil.encryptAES256(value, secret);

        assertTrue(_aesCrypto.isPresent());

        Cookie _compareCookie = new Cookie(name, Base64.encodeBase64URLSafeString(_aesCrypto.get().getEncrypted()));

        _compareCookie.setPath(CookieUtil.DEFAULT_PATH);

        _cookies = new Cookie[]{_compareCookie};

        when(request.getCookies()).thenReturn(_cookies);

        Optional<AESCrypto> encrypted = CookieUtil.addCookieWithEncrypt(response, name, value, secret);

        assertTrue(encrypted.isPresent());

        Optional<String> decryptedCookie = CookieUtil.getCookieWithDecrypt(request,
                                                                           name,
                                                                           secret,
                                                                           _aesCrypto.get().getIv(),
                                                                           _aesCrypto.get().getSalt());

        assertTrue(decryptedCookie.isPresent(), "복호화 쿠키 조회 실패");

        decryptedCookie.ifPresent(t -> {
            assertEquals(t, value, "복호화된 값 불일치");
        });
    }
}
