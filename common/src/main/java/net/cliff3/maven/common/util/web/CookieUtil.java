package net.cliff3.maven.common.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;

import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
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
        Optional<Integer> _maxAge = Optional.of(DEFAULT_MAX_AGE);

        if (_name.isPresent() && _value.isPresent()) {
            Cookie _cookie = new Cookie(_name.get(), _value.get());

            Optional.ofNullable(domain).filter(StringUtils::isNotBlank).ifPresent(c -> {
                log.debug("cookie domain : {}", domain);

                _cookie.setDomain(domain);
            });

            _cookie.setPath(_path.get());
        }
    }
}
