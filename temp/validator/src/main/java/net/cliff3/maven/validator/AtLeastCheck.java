package net.cliff3.maven.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 지정된 필드의 값 중 적어도 하나 이상 입력이 되었는지 여부를 검사
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = AtLeastCheckValidator.class)
public @interface AtLeastCheck {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "적어도 하나 이상의 값을 입력하셔야 합니다.";

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
     * 대상 필드 목록
     *
     * @return 필드 목록 반환
     */
    String[] fields() default {};

    /**
     * 비교 대상 지정
     */
    @Target(value = {TYPE, ANNOTATION_TYPE})
    @Retention(value = RUNTIME)
    @Documented
    @interface TargetList {
        /**
         * 비교 대상
         *
         * @return 비교 대상 목록
         */
        AtLeastCheck[] value();
    }
}
