package net.cliff3.maven.common.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 여부(참/거짓)에 해당하는 결과 출력
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
public class BooleanTag extends SimpleTagSupport {
    /**
     * 여부에 해당하는 <strong>Boolean</strong>
     */
    @Setter
    private Boolean status = false;

    /**
     * 참에 해당하는 문자열
     */
    @Setter
    private String trueValue = "참";

    /**
     * 거짓에 해당하는 문자열
     */
    @Setter
    private String falseValue = "거짓";

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter writer = getJspContext().getOut();

        writer.print((status)? trueValue: falseValue);
    }
}
