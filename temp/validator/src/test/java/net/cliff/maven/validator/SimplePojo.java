package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.CellularCheck;
import net.cliff3.maven.validator.Insert;
import net.cliff3.maven.validator.Update;

/**
 * SimplePojo
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
public class SimplePojo {
    @Getter
    @Setter
    @CellularCheck(groups = {Insert.class})
    private String cellularNumber;

    @Getter
    @Setter
    @CellularCheck(groups = {Update.class}, required = true)
    private String requiredNumber;
}
