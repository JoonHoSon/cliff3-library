package net.cliff3.maven.validator;

import static java.lang.annotation.ElementType.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 두 개의 값을 비교하는 유효성 검사
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = TYPE)
@Constraint(validatedBy = CompareValueValidator.class)
public @interface CompareValue {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "비밀번호가 일치하지 않습니다";

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
    String first();

    /**
     * 비교 대상의 두 번째 필드명 반환
     *
     * @return 필드명
     */
    String second();

    /**
     * 비교 대상 지정
     */
    @Target(value = {TYPE, ANNOTATION_TYPE})
    @Retention(value = RetentionPolicy.RUNTIME)
    @Documented
    @interface TargetList {
        CompareValue[] value();
    }
}
