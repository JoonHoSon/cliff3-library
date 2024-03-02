package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * 문자열이 null("null" 포함)이거나 빈값 여부를 확인
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class NotEmptyStringValidator implements ConstraintValidator<NotEmptyString, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNotBlank(value) && !"null".equalsIgnoreCase(value);
    }
}
