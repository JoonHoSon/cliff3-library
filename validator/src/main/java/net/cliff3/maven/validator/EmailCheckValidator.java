package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 이메일 유효성 검사. 필수 입력이 아닐 경우 입력값이 없으면(null) true 반환. 필수일 경우 false 반환
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
public class EmailCheckValidator implements ConstraintValidator<EmailCheck, String> {
    /**
     * 이메일 표현식
     */
    private static final String EXPRESSION = "^[\\w-]+(\\.[\\w-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";

    private static final Pattern PATTERN = Pattern.compile(EXPRESSION);

    private boolean required;

    @Override
    public void initialize(EmailCheck constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!required && value == null) {
            return true;
        }

        if (value == null) {
        	return false;
        }

        return PATTERN.matcher(value).matches();
    }
}
