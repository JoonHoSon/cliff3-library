package net.cliff3.maven.common.util.web.aop;

import lombok.Getter;
import lombok.Setter;
import net.cliff3.maven.common.util.web.aop.EscapeHTML;

/**
 * EscapeSampleDTO
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Getter
@Setter
public class EscapeSampleDTO {
    @EscapeHTML
    private String content;
}
