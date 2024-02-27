package net.cliff3.maven.common.util.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.common.util.crypto.AESCrypto;
import net.cliff3.maven.common.util.crypto.CryptoUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * CookieUtilTest
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CookieUtilTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final String name = "testCookie";

    private final String value = "this is a cookie value";

    private final String secret = "password1!";

    private final Charset UTF_8 = StandardCharsets.UTF_8;

    private AutoCloseable closeable;

    @BeforeEach
    public void beforeEach() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void afterEach() throws Exception {
        closeable.close();
    }

    @Test
    @Order(1)
    @DisplayName("쿠키 등록 테스트")
    public void addCookieTest() {
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

            assertArrayEquals(decryptResult.get(), value.getBytes(UTF_8), "암호화 실패");
        });
    }

    @Test
    @Order(2)
    @DisplayName("쿠키 조회 테스트")
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

    @Test
    @Order(3)
    @DisplayName("쿠키에서 locale 정보 등록/조회 테스트")
    public void getLocaleFromCookieTest() {
        final String languageCode = "ko";
        final String countryCode = "KR";
        final String key = "savedLocale";
        final Locale enUS = new Locale("en", "US");
        Optional<Cookie> saved = CookieUtil.addCookie(response, key, String.format("%s_%s", languageCode, countryCode));

        assertTrue(saved.isPresent());

        when(request.getCookies()).thenReturn(new Cookie[]{saved.get()});

        Optional<Locale> result = CookieUtil.getLocaleFromCookie(request, key, enUS);

        assertTrue(result.isPresent(), "locale 정보 조회 실패");
        result.ifPresent(t -> {
            assertEquals(t, new Locale(languageCode, countryCode), "반환된 locale 불일치");
        });

        result = CookieUtil.getLocaleFromCookie(request, "abc", enUS);

        assertTrue(result.isPresent());
        result.ifPresent(t -> {
            assertEquals(t, enUS);
        });

        result = CookieUtil.getLocaleFromCookie(request, "abc", new Locale("a", "b"));

        assertFalse(result.isPresent());
    }
}
