package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

/**
 * 두 개의 값을 비교하여 반환한다.<br>
 * JSR-303 validation은 원칙적으로 field 기준으로 처리하지만, 두 개 이상의 field를 비교하기 위해
 * {@code @NGCompareValue} annotation을 class 단위로 지정하여 처리한다.
 *
 * <pre>
 *     {@code
 *      &#64;CompareValue.TargetList({&#64;CompareValue(first="password",
 *                                                      second="confirmPassword",
 *                                                      message="비밀번호가 일치하지 않습니다.")})
 *      public class User {
 *          ...
 *          ...
 *          private String password;
 *          private String confirmPassword;
 *          ...
 *          ...
 *      }
 *     }
 * </pre>
 *
 * @author JoonHo Son
 * @see CompareValue
 * @since 0.3.0
 */
@Slf4j
public class CompareValueValidator implements ConstraintValidator<CompareValue, Object> {
    /**
     * 비교 대상의 첫 번째 필드명
     */
    private String firstFieldName;

    /**
     * 비교 대상의 두 번째 필드명
     */
    private String secondFieldName;

    @Override
    public void initialize(CompareValue constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field firstField = value.getClass().getDeclaredField(firstFieldName);
            Field secondField = value.getClass().getDeclaredField(secondFieldName);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            // 모두 값이 null이거나 값이 같을 경우 true
            return (firstField.get(value) == null && secondField.get(value) == null) || firstField.get(value)
                                                                                                  .equals(secondField.get(
                                                                                                      value));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("CompareValueValidator fail :", e);

            return false;
        }
    }
}
