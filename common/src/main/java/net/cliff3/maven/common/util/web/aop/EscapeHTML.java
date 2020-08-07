package net.cliff3.maven.common.util.web.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EscapeHTML
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EscapeHTML {
    /**
     * 태그에 대한 주석
     */
    static final String COMMENT_TAG = "<!-- Not Allowed Tag Filtered -->";

    /**
     * 속성에 대한 주석
     */
    static final String COMMENT_ATTRIBUTE = "<!-- Not Allowed Attribute Filtered -->";

    /**
     * 필수 여부
     *
     * @return 필수 여부
     */
    boolean required() default true;

    /**
     * 주석 제거 여부
     *
     * @return 주석 제거 여부
     */
    boolean removeCommentTag() default true;
}
