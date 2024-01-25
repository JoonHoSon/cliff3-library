package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.CascadeNotEmpty;
import net.cliff3.maven.validator.Update;

/**
 * SampleParent
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@CascadeNotEmpty.TargetList(@CascadeNotEmpty(parentField = "user",
                                             childObjectField = "value2",
                                             groups = Update.class,
                                             message = "사용자의 비밀번호는 필수 입니다."))
public class SampleParent {
    @Getter
    @Setter
    private SampleUserAtLeast user;
}
