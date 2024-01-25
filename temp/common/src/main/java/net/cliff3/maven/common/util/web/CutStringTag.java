package net.cliff3.maven.common.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 긴 문자열을 지정한 길이와 지정된 줄임표시로 출력한다.
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
public class CutStringTag extends SimpleTagSupport {
    /**
     * 줄임표시 대상 문자열
     */
    @Setter
    private String target;

    /**
     * 출력 문자열 길이
     */
    @Setter
    private Integer limit;

    /**
     * 줄임표시
     */
    @Setter
    private String tail = "...";

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        StringBuilder builder = new StringBuilder();

        if (target != null && limit != null) {
            if (target.length() > limit) {
                builder.append(target.substring(0, limit)).append(tail);
            } else {
                builder.append(target);
            }
        }

        out.print(builder.toString());
    }
}
