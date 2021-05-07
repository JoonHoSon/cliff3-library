package net.cliff3.maven.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이메일 유효성 검사. 필수 입력이 아닐 경우 입력값이 없으면(null, blank) true 반환. 필수일 경우 false 반환
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = EmailCheckValidator.class)
public @interface EmailCheck {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "이메일 주소가 유효하지 않습니다.";

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
     * 필수 여부
     *
     * @return 필수 여부
     */
    boolean required() default false;
}
