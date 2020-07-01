package net.cliff3.maven.common.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.common.util.crypto.AESCrypto;
import net.cliff3.maven.common.util.crypto.CryptoUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * CookieUtil
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-16 최초 작성
 * @since 1.0.0
 */
@Slf4j
public class CookieUtil {
    /**
     * 쿠키 기본 유지 시간(하루)
     */
    public static final int DEFAULT_MAX_AGE = 86400;

    public static final String DEFAULT_PATH = "/";

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static boolean isValidNameValue(String name, String value) {
        return Optional.ofNullable(name).filter(StringUtils::isNotBlank).isPresent() && Optional.ofNullable(value)
                                                                                                .filter(StringUtils::isNotBlank)
                                                                                                .isPresent();
    }

    /**
     * 쿠키 등록. <strong>name</strong> 및 <strong>value</strong> 항목이 존재할 경우에만 등록 처리한다.
     * 유효하지 못한 <strong>path</strong> 및 <strong>maxAge</strong>는 각각 {@link #DEFAULT_PATH}와
     * {@link #DEFAULT_MAX_AGE}로 대체된다.
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cookie max age(초)
     */
    public static Optional<Cookie> addCookie(HttpServletResponse response,
                                             String name,
                                             String value,
                                             String domain,
                                             String path,
                                             Integer maxAge) {
        if (isValidNameValue(name, value)) {
            return Optional.of(doAddCookie(response, name, value, domain, path, maxAge));
        } else {
            log.error("쿠키 등록 실패. name 혹은 value 없음");
            return Optional.empty();
        }
    }

    /**
     * 쿠키 등록 처리
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cookie max age(초)
     */
    private static Cookie doAddCookie(HttpServletResponse response,
                                      String name,
                                      String value,
                                      String domain,
                                      String path,
                                      Integer maxAge) {
        log.debug("cookie name : {}", name);
        log.debug("cookie value : {}", value);
        log.debug("domain : {}", domain);
        log.debug("path : {}", path);
        log.debug("maxAge : {}", maxAge);

        String _path = Optional.ofNullable(path)
                               .filter(t -> StringUtils.isNotBlank(t) && t.startsWith("/"))
                               .orElse(DEFAULT_PATH);
        int _maxAge = Optional.ofNullable(maxAge).orElse(DEFAULT_MAX_AGE);

        Cookie _cookie = new Cookie(name, value);

        Optional.ofNullable(domain).filter(StringUtils::isNotBlank).ifPresent(c -> {
            log.debug("cookie domain : {}", domain);

            _cookie.setDomain(domain);
        });

        _cookie.setPath(_path);
        _cookie.setMaxAge(_maxAge);

        response.addCookie(_cookie);

        return _cookie;
    }

    /**
     * {@link #addCookie(HttpServletResponse, String, String, String, String, Integer)} 참고
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     */
    public static Optional<Cookie> addCookie(HttpServletResponse response,
                                             String name,
                                             String value) {
        return addCookie(response, name, value, null, DEFAULT_PATH, DEFAULT_MAX_AGE);
    }

    /**
     * 쿠키 등록. 쿠키 값은 {@link Base64#encodeBase64URLSafeString(byte[])} 처리하여 등록한다.
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값. {@link Base64#encodeBase64URLSafeString(byte[])} 처리하여 등록한다.
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cookie max age(초)
     *
     * @see #addCookie(HttpServletResponse, String, String, String, String, Integer)
     */
    public static Optional<Cookie> addCookieWithBase64(HttpServletResponse response,
                                                       String name,
                                                       String value,
                                                       String domain,
                                                       String path,
                                                       Integer maxAge) {
        if (isValidNameValue(name, value)) {
            return Optional.of(doAddCookie(response,
                                           name,
                                           Base64.encodeBase64URLSafeString(value.getBytes(UTF_8)),
                                           domain,
                                           path,
                                           maxAge));
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@link #addCookieWithBase64(HttpServletResponse, String, String, String, String, Integer)} 참고
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값. {@link Base64#encodeBase64URLSafeString(byte[])} 처리하여 등록한다.
     *
     * @see #addCookieWithBase64(HttpServletResponse, String, String, String, String, Integer)
     */
    public static Optional<Cookie> addCookieWithBase64(HttpServletResponse response,
                                                       String name,
                                                       String value) {
        if (isValidNameValue(name, value)) {
            return Optional.of(doAddCookie(response,
                                           name,
                                           Base64.encodeBase64URLSafeString(value.getBytes(UTF_8)),
                                           null,
                                           DEFAULT_PATH,
                                           DEFAULT_MAX_AGE));
        } else {
            return Optional.empty();
        }
    }

    /**
     * 지정된 쿠키 이름에 해당하는 값을 반환
     *
     * @param request    {@link HttpServletRequest}
     * @param name       쿠키 이름
     * @param isDecoding {@link Base64#decode(String)} 처리 여부
     *
     * @return 쿠키 값
     */
    public static Optional<String> getCookie(HttpServletRequest request, String name, boolean isDecoding) {
        return Optional.ofNullable(name).filter(StringUtils::isNotBlank).map(t -> {
            Optional<Cookie> _cookie = Optional.ofNullable(request.getCookies())
                                               .map(Stream::of)
                                               .orElseGet(Stream::empty)
                                               .filter(d -> name.equals(d.getName()))
                                               .findFirst();

            if (_cookie.isPresent()) {
                if (isDecoding) {
                    return new String(Base64.decodeBase64(_cookie.get().getValue()));
                } else {
                    return _cookie.get().getValue();
                }
            } else {
                return "";
            }
        });
    }

    /**
     * 지정된 쿠키 이름에 해당하는 값을 반환. 쿠키에 저장된 값을 그대로 반환한다.
     *
     * @param request {@link HttpServletRequest}
     * @param name    쿠키 이름
     *
     * @return 쿠키 값
     * @see #getCookie(HttpServletRequest, String, boolean)
     */
    public static Optional<String> getCookie(HttpServletRequest request, String name) {
        return getCookie(request, name, false);
    }

    /**
     * 지정된 쿠키를 제거
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param domain   도메인
     * @param path     경로
     */
    public static void removeCookie(HttpServletResponse response, String name, String domain, String path) {
        Optional.ofNullable(name).filter(StringUtils::isNotBlank).ifPresent(c -> {
            Optional<String> _domain = Optional.ofNullable(domain).filter(StringUtils::isNotBlank);
            Optional<String> _path = Optional.ofNullable(path).filter(StringUtils::isNotBlank);
            Cookie _cookie = new Cookie(c, "");

            _cookie.setMaxAge(0);

            _path.ifPresent(_cookie::setPath);
            _domain.ifPresent(_cookie::setDomain);

            response.addCookie(_cookie);
        });
    }

    /**
     * {@link CryptoUtil#encryptAES256(String, String)} 함수를 이용한 암호화된 쿠키 등록. 쿠키 등록이 정상적으로 처리되면
     * 암호화 결과인 {@link AESCrypto} 인스턴스를 반환한다. 해당 인스턴스는 복호화시 필요한 정보를 담고 있으며, 해당 값을 별도로 관리하여야만
     * 복호화가 가능하다. 암호화 처리된 결과는 {@link Base64#encodeBase64URLSafeString(byte[])}로 인코딩하여 쿠키로 저장한다.
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param secret   암호화키
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cooke max age(초)
     *
     * @return 암호화 결과
     * @see CryptoUtil#encryptAES256(String, String)
     * @see #doAddCookie(HttpServletResponse, String, String, String, String, Integer)
     * @see AESCrypto
     */
    public static Optional<AESCrypto> addCookieWithEncrypt(HttpServletResponse response,
                                                           String name,
                                                           String value,
                                                           String secret,
                                                           String domain,
                                                           String path,
                                                           Integer maxAge) {
        Optional<String> _secret = Optional.ofNullable(secret).filter(StringUtils::isNotBlank);

        if (isValidNameValue(name, value) && _secret.isPresent()) {
            Optional<AESCrypto> result = CryptoUtil.encryptAES256(value, secret);

            result.ifPresent(t -> {
                final String encrypted = Base64.encodeBase64URLSafeString(t.getEncrypted());

                log.debug("암호화된 쿠키값 : {}", encrypted);

                doAddCookie(response, name, encrypted, domain, path, maxAge);
            });

            return result;
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@link #addCookieWithEncrypt(HttpServletResponse, String, String, String, String, String, Integer)} 참고
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param secret   암호화키
     *
     * @return 암호화 결과
     * @see #addCookieWithEncrypt(HttpServletResponse, String, String, String, String, String, Integer)
     * @see CryptoUtil#encryptAES256(String, String)
     * @see #doAddCookie(HttpServletResponse, String, String, String, String, Integer)
     * @see AESCrypto
     */
    public static Optional<AESCrypto> addCookieWithEncrypt(HttpServletResponse response,
                                                           String name,
                                                           String value,
                                                           String secret) {
        return addCookieWithEncrypt(response, name, value, secret, null, DEFAULT_PATH, DEFAULT_MAX_AGE);
    }

    /**
     * 지정된 쿠키 이름에 해당하는 값을 복호화 하여 반환. 복호화 중 오류가 발생할 경우에도 {@link Optional#empty()}를 반환한다.
     *
     * @param request {@link HttpServletRequest}
     * @param name    쿠키 이름
     * @param secret  암호화키
     * @param iv      Initialize vector
     * @param salt    Salt
     *
     * @return 복호화 처리된 쿠키 값
     * @see CryptoUtil#decryptAES256(String, String, byte[], byte[])
     * @see AESCrypto
     */
    public static Optional<String> getCookieWithDecrypt(HttpServletRequest request,
                                                        String name,
                                                        String secret,
                                                        byte[] iv,
                                                        byte[] salt) {
        return Optional.ofNullable(name)
                       .filter(StringUtils::isNotBlank)
                       .map(t -> {
                           Optional<Cookie> _cookie = Optional.ofNullable(request.getCookies())
                                                              .map(Stream::of)
                                                              .orElseGet(Stream::empty)
                                                              .filter(d -> name.equals(d.getName()))
                                                              .findFirst();

                           try {
                               if (_cookie.isPresent()) {
                                   byte[] decoded = Base64.decodeBase64(_cookie.get().getValue());
                                   Optional<byte[]> decrypted = CryptoUtil.decryptAES256(decoded, secret, iv, salt);

                                   return decrypted.map(bytes -> new String(bytes, UTF_8)).orElse("");
                               } else {
                                   return "";
                               }
                           } catch (Exception e) {
                               log.error("쿠키 복호화 중 오류 발생", e);

                               return "";
                           }
                       });
    }
}
