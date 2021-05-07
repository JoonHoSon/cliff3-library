package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.EmailCheck;
import net.cliff3.maven.validator.Insert;
import net.cliff3.maven.validator.Update;

/**
 * SimpleEmail
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
public class SimpleEmail {
    @Getter
    @Setter
    @EmailCheck(groups = Insert.class, required = true)
    private String email1;

    @Getter
    @Setter
    @EmailCheck(groups = Update.class)
    private String email2;
}
