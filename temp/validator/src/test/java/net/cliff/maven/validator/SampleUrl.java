package net.cliff.maven.validator;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.validator.Insert;
import net.cliff3.maven.validator.Update;
import net.cliff3.maven.validator.UrlCheck;

/**
 * SampleUrl
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
public class SampleUrl {
    @Getter
    @Setter
    @UrlCheck(message = "URL을 입력하여 주십시오.", required = true, groups = Insert.class)
    private String url1;

    @Getter
    @Setter
    @UrlCheck(message = "URL을 입력하여 주십시오.", groups = Update.class)
    private String url2;
}
