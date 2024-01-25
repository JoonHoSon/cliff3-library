package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.AtLeastCheck;
import net.cliff3.maven.validator.CompareValue;
import net.cliff3.maven.validator.Insert;

/**
 * SampleParent
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@AtLeastCheck.TargetList(@AtLeastCheck(fields = {"value1", "value2", "value3"},
                                       groups = Insert.class))
public class SampleUserAtLeast {
    @Getter
    @Setter
    private String value1;

    @Getter
    @Setter
    private String value2;

    @Getter
    @Setter
    private String value3;
}
