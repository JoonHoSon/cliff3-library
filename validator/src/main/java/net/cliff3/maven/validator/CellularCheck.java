package net.cliff3.maven.validator;

import static java.lang.annotation.RetentionPolicy.*;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 휴대전화번호 확인. 필수 입력이 아닐경우 입력값이 없을 경우(null, blank) true 반환. 필수일 경우 false 반환
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CellularCheckValidator.class)
public @interface CellularCheck {
    /**
     * 출력 메세지 반환
     *
     * @return 메세지
     */
    String message() default "휴대전화 번호가 유효하지 않습니다.";

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
     * 필수 입력 여부. 필수 입력이 아닐경우 입력값이 없을 경우(null, blank) true 반환
     *
     * @return 필수 여부
     */
    boolean required() default false;
}
