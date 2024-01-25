package net.cliff3.maven.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 휴대전화번호 확인. 필수 입력이 아닐 경우 입력값이 없으면(null, blank) true 반환. 필수일 경우 false 반환
 *
 * @author JoonHo Son
 * @since 0.3.0
 * @see CellularCheck
 */
public class CellularCheckValidator implements ConstraintValidator<CellularCheck, String> {
    /**
     * 번호 유형. 010, 011, 016, 017, 018, 019 포함
     */
    private static final String EXPRESSION = "^(01[016789])-([0-9]){3,4}-([0-9]){4}$";

    private static final Pattern PATTERN = Pattern.compile(EXPRESSION);

    private boolean required;

    @Override
    public void initialize(CellularCheck constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return !required;
        }

        return PATTERN.matcher(value).matches();
    }
}
