package net.cliff3.maven.common.util.web;

import static org.testng.Assert.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * NewTagTest
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Slf4j
@ContextConfiguration(locations = "classpath:dispatcher-simple.xml")
public class NewTagTest {
    private MockPageContext pageContext;

    private NewTag newTag;

    private final String tagString = "<strong>new</strong>";

    @Mock
    private ServletOutputStream outputStream;

    @BeforeMethod
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        MockServletContext _mockServletContext = new MockServletContext();
        pageContext = new MockPageContext();

        _mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, pageContext);

        newTag = new NewTag();

        JspFragment _fragment = new JspFragment() {
            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write(tagString);
            }

            @Override
            public JspContext getJspContext() {
                return pageContext;
            }
        };

        newTag.setJspBody(_fragment);
        newTag.setJspContext(pageContext);
    }

    @Test
    public void successTest() throws IOException, JspException {
        Calendar _calendar = Calendar.getInstance();

        _calendar.set(Calendar.DAY_OF_MONTH, _calendar.get(Calendar.DAY_OF_MONTH) - 5);

        newTag.setCompareDate(_calendar.getTime());

        newTag.doTag();

        ServletResponse _servletResponse = pageContext.getResponse();
        String _outputResult = ((MockHttpServletResponse)_servletResponse).getContentAsString();

        log.debug("NewTag 결과 : {}", _outputResult);

        assertEquals(_outputResult, tagString, "일자 비교후 tag 출력 실패");
    }

    @Test
    public void failTest() throws IOException, JspException {
        Calendar _calendar = Calendar.getInstance();

        _calendar.set(Calendar.DAY_OF_MONTH, _calendar.get(Calendar.DAY_OF_MONTH) - 23);

        newTag.setCompareDate(_calendar.getTime());

        newTag.doTag();

        ServletResponse _servletResponse = pageContext.getResponse();
        String _outputResult = ((MockHttpServletResponse)_servletResponse).getContentAsString();

        log.debug("NewTag 결과 : {}", _outputResult);

        assertTrue(StringUtils.isBlank(_outputResult), "일자 비교후 tag 출력 실패");
    }
}
