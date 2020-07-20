package net.cliff3.maven.common.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * <a href="https://ckeditor.com">CKEditor</a> 생성 스크립트 처리
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
public class CKEditorDisplayTag extends SimpleTagSupport {
    /**
     * ckeditor.js 경로
     */
    @Setter
    private String ckPath;

    /**
     * 대상 <strong>textarea tag의 id 속성</strong> 배열. 쉼표로 구분. 예 : 'content1', 'content2'
     */
    private String[] targets;

    /**
     * CKeditor 설정파일(config.js) 위치
     */
    @Setter
    private String configPath = "/assets/js/ckeditor/config.js";

    public void setTargets(String targets) {
        log.debug("ckeditor targets : {}", targets);
        this.targets = targets.split(",");
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter writer = getJspContext().getOut();
        StringBuilder builder = new StringBuilder("<script type=\"text/javascript\" src=\"");

        builder.append(ckPath).append("\"></script>");
        builder.append("<script type=\"text/javascript\">\n");
        builder.append("$(document).ready(function () {\n");

        log.debug("targets length : {}", targets.length);

        for (String target : targets) {
            builder.append("\tCKEditor.replace('").append(StringUtils.trim(target)).append("'");
            builder.append(", {customConfig: '").append(configPath).append("'});\n");
        }

        builder.append("});\n</script>");
        writer.write(builder.toString());
    }
}
