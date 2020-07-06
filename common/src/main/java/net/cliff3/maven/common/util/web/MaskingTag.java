package net.cliff3.maven.common.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Optional;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 대상 문자열을 지정된 길이만큼 뒤에서부터 마스킹 처리
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Slf4j
public class MaskingTag extends SimpleTagSupport {
    /**
     * 마스크 문자
     */
    @Setter
    private String mask = "*";

    /**
     * 마스킹 처리 개수
     */
    @Setter
    private Integer maskingCount = 0;

    /**
     * 대상
     */
    @Setter
    private Object target;

    /**
     * 대상이 null 이거나 빈 값일 경우 출력 문자열
     */
    @Setter
    private String empty = "";

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        log.debug("masking empty string : {}", empty);

        if (target == null || "null".equals(String.valueOf(target)) || StringUtils.isEmpty(String.valueOf(target))) {
            out.print(empty);
        } else {
            String _targetString = String.valueOf(target);
            Optional<String> _result = StringUtil.masking(_targetString, _targetString.length() - maskingCount, mask);

            if (_result.isPresent()) {
                out.print(_result.get());
            } else {
                out.print(empty);
            }
        }
    }
}
