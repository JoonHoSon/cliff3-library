package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드 객체의 특정 필드가 notEmpty를 만족하는지 검사<br>
 * JSR-303 validation은 원칙적으로 field 기준으로 처리한다. 하지만, 해당 field가 특정 POJO 클래스일 경우 하위 POJO 클래스의
 * validation 대상 필드는 검사를 하지 못한다. 이를 처리하기 위한 validator며 다음과 같은 형식으로 사용한다.<br>
 * 아래의 예제는 <strong>CommonCode#groupCode</strong> 항목의 입력 여부(not null)와
 * <strong>CommonGroupCode#code</strong> 항목의 입력 여부(not null, not blank string)
 *
 * <pre>
 *     &#64;CascadeNotEmpty.targetList({&#64;CascadeNotEmpty(parentField = "groupCode",
 *                                                           childObject = "code",
 *                                                           message = "그룹코드는 필수 입니다.")})
 *     public class CommonCode {
 *         ...
 *         ...
 *         private CommonCodeGroup groupCode;
 *         ...
 *         ...
 *     }
 *
 *     public class CommonCodeGroup {
 *         ...
 *         ...
 *         private String code;
 *         ...
 *     }
 * </pre>
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class CascadeNotEmptyValidator implements ConstraintValidator<CascadeNotEmpty, Object> {
    /**
     * 부모 필드명
     */
    private String parentField;

    /**
     * 자식 객체의 검증 대상 필드명
     */
    private String childObjectField;

    @Override
    public void initialize(CascadeNotEmpty constraintAnnotation) {
        parentField = constraintAnnotation.parentField();
        childObjectField = constraintAnnotation.childObjectField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 필드의 객체를 검사한다.
        try {
            Field field = value.getClass().getDeclaredField(parentField);
            Object child;

            field.setAccessible(true);

            child = field.get(value);

            if (child == null) {
            	return false;
            }

            Field childField = child.getClass().getDeclaredField(childObjectField);

            childField.setAccessible(true);
            Object childFieldValue = childField.get(child);
            String result = String.valueOf(childFieldValue);

            return (result != null && !result.equals("null") && result.replaceAll("/(\\s)/g", "").length() > 0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("AtLeastCheckValidator error ->", e);
        }

        return false;
    }
}
