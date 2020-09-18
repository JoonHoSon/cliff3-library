package net.cliff3.maven.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 문자열이 null이거나 빈값인 경우를 확인
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = NotEmptyStringValidator.class)
public @interface NotEmptyString {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
