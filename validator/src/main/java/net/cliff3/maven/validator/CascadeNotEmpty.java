package net.cliff3.maven.validator;

import static java.lang.annotation.ElementType.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CascadeNotEmpty
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Target(value = {TYPE, ANNOTATION_TYPE, METHOD, FIELD, CONSTRUCTOR, PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CascadeNotEmptyValidator.class)
@Documented
public @interface CascadeNotEmpty {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "필수값이 누락되었습니다.";

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
     * 비교 대상의 첫 번째 필드명 반환
     *
     * @return 필드명
     */
    String parentField();

    /**
     * 비교 대상의 두 번째 필드명 반환
     *
     * @return 필드명
     */
    String childObjectField();

    /**
     * 비교 대상 지정
     */
    @Target(value = {TYPE, ANNOTATION_TYPE})
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    @interface TargetList {
        /**
         * 비교 대상 반환
         *
         * @return 비교 대상
         */
        CascadeNotEmpty[] value();
    }
}
