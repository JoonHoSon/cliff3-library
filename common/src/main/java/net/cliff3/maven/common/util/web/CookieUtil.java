package net.cliff3.maven.common.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.common.util.StringUtil;
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

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * 쿠키 등록. <strong>name</strong> 및 <strong>value</strong> 항목이 존재할 경우에만 등록 처리한다.
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cookie max age(초)
     */
    public void addCookie(HttpServletResponse response,
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

        Optional<String> _name = Optional.ofNullable(name).filter(StringUtils::isNotBlank);
        Optional<String> _value = Optional.ofNullable(value).filter(StringUtils::isNotBlank);
        Optional<String> _path = Optional.of(Optional.ofNullable(path).filter(StringUtils::isNotBlank).orElse("/"));
        Optional<Integer> _maxAge = Optional.of(Optional.ofNullable(maxAge).orElse(DEFAULT_MAX_AGE));

        if (_name.isPresent() && _value.isPresent()) {
            Cookie _cookie = new Cookie(_name.get(), _value.get());

            Optional.ofNullable(domain).filter(StringUtils::isNotBlank).ifPresent(c -> {
                log.debug("cookie domain : {}", domain);

                _cookie.setDomain(domain);
            });

            _cookie.setPath(_path.get());
            _cookie.setMaxAge(_maxAge.get());

            response.addCookie(_cookie);
        } else {
            log.debug("쿠키 등록 실패. name 혹은 value 없음");
        }
    }

    /**
     * {@link #addCookie(HttpServletResponse, String, String, String, String, Integer)} 참고
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값
     */
    public void addCookie(HttpServletResponse response,
                          String name,
                          String value) {
        addCookie(response, name, value, null, "/", DEFAULT_MAX_AGE);
    }

    /**
     * 쿠키 등록. 쿠키 값은 {@link Base64#encodeBase64URLSafeString(byte[])} 처리하여 등록한다.
     * {@link #addCookie(HttpServletResponse, String, String, String, String, Integer)} 참고
     *
     * @param response {@link HttpServletResponse}
     * @param name     쿠키 이름
     * @param value    쿠키 값. {@link Base64#encodeBase64URLSafeString(byte[])} 처리하여 등록한다.
     * @param domain   도메인
     * @param path     경로
     * @param maxAge   cookie max age(초)
     */
    public void addCookieByBase64(HttpServletResponse response,
                                  String name,
                                  String value,
                                  String domain,
                                  String path,
                                  Integer maxAge) {
        Optional<String> _name = Optional.ofNullable(name).filter(StringUtils::isNotBlank);
        Optional<String> _value = Optional.ofNullable(value).filter(StringUtils::isNotBlank);

        if (_name.isPresent() && _value.isPresent()) {
            addCookie(response, name, Base64.encodeBase64URLSafeString(value.getBytes(UTF_8)), domain, path, maxAge);
        }
    }

    public void addCookieByBAse64(HttpServletResponse response,
                                  String name,
                                  String value) {
        Optional<String> _name = Optional.ofNullable(name).filter(StringUtils::isNotBlank);
        Optional<String> _value = Optional.ofNullable(value).filter(StringUtils::isNotBlank);

        if (_name.isPresent() && _value.isPresent()) {
            addCookie(response,
                      name,
                      Base64.encodeBase64URLSafeString(value.getBytes(UTF_8)),
                      null,
                      "/",
                      DEFAULT_MAX_AGE);
        }
    }

    public Optional<String> getCookie(HttpServletRequest request, String name, boolean isDecoding) {
        return Optional.ofNullable(name).filter(StringUtils::isNotBlank).map(c -> {
            Optional<Cookie> _cookie = Stream.of(request.getCookies())
                                             .filter(d -> name.equals(d.getName()))
                                             .findFirst();

            if (_cookie.isPresent()) {
                return _cookie.get().getValue();
            } else {
                return "";
            }
        });
    }

    public Optional<String> getCookie(HttpServletRequest request, String name) {
        return getCookie(request, name, false);
    }

    public void removeCookie(HttpServletResponse response, String name, String domain, String path) {
        Optional.ofNullable(name).filter(StringUtils::isNotBlank).ifPresent(c -> {
            Optional<String> _domain = Optional.ofNullable(domain).filter(StringUtils::isNotBlank);
            Optional<String> _path = Optional.ofNullable(path).filter(StringUtils::isNotBlank);
            Cookie _cookie = new Cookie(c, "");

            _cookie.setMaxAge(0);

            _path.ifPresent(_cookie::setPath);

            response.addCookie(_cookie);
        });
    }
}
