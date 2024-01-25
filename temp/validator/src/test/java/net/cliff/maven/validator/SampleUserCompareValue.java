package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.CompareValue;
import net.cliff3.maven.validator.Insert;

/**
 * SampleUserCompareValue
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@CompareValue.TargetList(@CompareValue(first = "password",
                                       second = "confirmPassword",
                                       groups = Insert.class,
                                       message = "비밀번호가 일치하지 않습니다."))
public class SampleUserCompareValue {
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String confirmPassword;
}
