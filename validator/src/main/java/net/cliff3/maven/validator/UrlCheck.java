package net.cliff3.maven.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http protocol url 형식 유효성 확인. 입력값이 없을 경우(null) 필수 상태일 경우는 false, 필수가 아닐 경우에는 true를 반환.
 *
 * @author JoonHo Son
 * @see UrlCheckValidator
 * @since 0.3.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UrlCheckValidator.class)
public @interface UrlCheck {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "URL 주소가 유효하지 않습니다.";

    /**
     * 실행 그룹 반환
     *
     * @return 그룹
     */
    Class<?>[] groups() default {};

    /**
     * Validation Error에 대한 심각의 정도를 반환
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 필수 항목 여부. 필수 항목이 아닐 경우 입력값이 없을 경우 true 반환.
     *
     * @return 필수 여부
     */
    boolean required() default false;
}
