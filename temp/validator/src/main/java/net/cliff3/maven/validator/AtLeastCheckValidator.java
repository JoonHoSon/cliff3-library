package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 지정된 필드에서 하나 이상의 값이 입력되었는지 여부를 검사한다.<br>
 * JSR-303 validation은 원칙적으로 field 기준으로 처리하지만, 두 개 이상의 field를 비교하기 위해 {@code @NGAtLeastCheck}
 * annotation을 class 단위로 지정하여 처리한다.
 *
 * <pre>
 *     &#64;AtLeastCheck.TargetList({&#64;AtLeastCheck(fields = {"value1", "value2", "value3"},
 *                                                     message = "적어도 하나 이상의 값은 입력하여야 합니다.")})
 *     public class User {
 *         ...
 *         ...
 *         private String value1;
 *         private String value2;
 *         private String value3;
 *         ...
 *         ...
 *     }
 * </pre>
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class AtLeastCheckValidator implements ConstraintValidator<AtLeastCheck, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastCheck constraintAnnotation) {
        fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;

        for (String fieldName : fields) {
            try {
                Field field = value.getClass().getDeclaredField(fieldName);

                field.setAccessible(true);

                if (field.get(value) != null && StringUtils.isNotEmpty(String.valueOf(field.get(value)))) {
                    result = true;

                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("AtLeastCheckValidator error ->", e);
            }
        }

        return result;
    }
}
