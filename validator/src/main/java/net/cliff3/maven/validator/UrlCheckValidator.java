package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Http protocol url 형식 유효성 확인. 입력값이 없을 경우(null) 필수 상태일 경우는 false, 필수가 아닐 경우에는 true를 반환.
 *
 * @author JoonHo Son
 * @see UrlCheck
 * @since 0.3.0
 */
public class UrlCheckValidator implements ConstraintValidator<UrlCheck, String> {
    private static final String expression = "https?://(www\\.)?[-a-zA-Z0-9@:%.+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%+.~#?&/=]*)";

    private static final Pattern PATTERN = Pattern.compile(expression);

    private boolean required;

    @Override
    public void initialize(UrlCheck constraintAnnotation) {
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
