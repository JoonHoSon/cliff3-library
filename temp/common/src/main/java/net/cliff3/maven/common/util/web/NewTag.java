package net.cliff3.maven.common.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 현재 일자 기준으로 인자로 전달된 {@link #compareDate}와 비교하여 해당 조건을 충족할 시 {@code <c3:newTag></c3:newTag>} 사이의
 * 컨텐츠(body)를 출력한다.
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
public class NewTag extends SimpleTagSupport {
    /**
     * NewTag 적용 기준
     */
    public enum NewTagType {
        DAY, HOUR, MINUTE
    }

    private final StringWriter bodyWriter = new StringWriter();

    /**
     * 비교 대상 일자
     */
    @Setter
    private Date compareDate;

    /**
     * 기준값
     */
    @Setter
    private int term = 7;

    /**
     * 비교 대상 기준
     */
    @Setter
    private NewTagType type = NewTagType.DAY;

    @Override
    public void doTag() throws JspException, IOException {
        if (log.isDebugEnabled()) {
            SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            log.debug("인자로 전달된 compareDate : {}", _simpleDateFormat.format(compareDate));
        }

        JspWriter out = getJspContext().getOut();

        if (compareDate != null) {
            Calendar _calendar = Calendar.getInstance();
            Calendar _target = Calendar.getInstance();

            _target.setTime(compareDate);

            if (NewTagType.DAY == type) {
                _target.set(Calendar.DAY_OF_MONTH, _target.get(Calendar.DAY_OF_MONTH) + term);
            } else if (NewTagType.HOUR == type) {
                _target.set(Calendar.HOUR_OF_DAY, _target.get(Calendar.HOUR_OF_DAY) + term);
            } else {
                _target.set(Calendar.MINUTE, _target.get(Calendar.MINUTE) + term);
            }

            if (_calendar.compareTo(_target) < 0) {
                getJspBody().invoke(bodyWriter);
                out.print(bodyWriter.toString());
            }
        }
    }
}
