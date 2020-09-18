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
public class TagTest {
    private MockPageContext newTagContext;

    private MockPageContext maskingTagContext;

    private MockPageContext cutStringTagContext;

    private NewTag newTag;

    private MaskingTag maskingTag;

    private CutStringTag cutStringTag;

    private final String tagString = "<strong>new</strong>";

    @Mock
    private ServletOutputStream outputStream;

    @BeforeMethod
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        //
        // new tag
        //
        MockServletContext _mockNewTagContext = new MockServletContext();
        newTagContext = new MockPageContext();

        _mockNewTagContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, newTagContext);


        newTag = new NewTag();

        JspFragment _fragment = new JspFragment() {
            @Override
            public void invoke(Writer out) throws JspException, IOException {
                out.write(tagString);
            }

            @Override
            public JspContext getJspContext() {
                return newTagContext;
            }
        };

        newTag.setJspBody(_fragment);
        newTag.setJspContext(newTagContext);

        //
        // masking
        //
        MockServletContext _maskingTagContext = new MockServletContext();
        maskingTagContext = new MockPageContext();
        maskingTagContext.getResponse().setCharacterEncoding("UTF-8"); // 한글처리

        _maskingTagContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                                        maskingTagContext);

        maskingTag = new MaskingTag();

        maskingTag.setJspContext(maskingTagContext);

        //
        // cut string
        //
        MockServletContext _cutStringTagContext = new MockServletContext();
        cutStringTagContext = new MockPageContext();

        cutStringTagContext.getResponse().setCharacterEncoding("UTF-8");

        _cutStringTagContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                                          cutStringTagContext);

        cutStringTag = new CutStringTag();

        cutStringTag.setJspContext(cutStringTagContext);
    }

    @Test
    public void successTest() throws IOException, JspException {
        //
        // new tag
        //
        Calendar _calendar = Calendar.getInstance();

        _calendar.set(Calendar.DAY_OF_MONTH, _calendar.get(Calendar.DAY_OF_MONTH) - 5);

        newTag.setCompareDate(_calendar.getTime());

        newTag.doTag();

        ServletResponse _newTagResponse = newTagContext.getResponse();
        String _outputResult = ((MockHttpServletResponse)_newTagResponse).getContentAsString();

        log.debug("NewTag 결과 : {}", _outputResult);

        assertEquals(_outputResult, tagString, "일자 비교후 tag 출력 실패");

        //
        // masking
        //
        final String _maskingSource = "01012345678";
        final String _expectedMasking = "0101234****";

        maskingTag.setMaskingCount(4);
        maskingTag.setTarget(_maskingSource);

        maskingTag.doTag();

        ServletResponse _maskingTagResponse = maskingTagContext.getResponse();
        String _maskingResult = ((MockHttpServletResponse)_maskingTagResponse).getContentAsString();

        log.debug("masking 결과 : {}", _maskingResult);

        assertEquals(_maskingResult, _expectedMasking, "마스킹 처리 실패");


        //
        // cut string
        //
        final String _cutStringSource = "이것은 한글과 English가 함께 있는 문장 입니다.";
        final String _expectedCutString = "이것은 한글과 Eng...";

        cutStringTag.setLimit(11);
        cutStringTag.setTarget(_cutStringSource);

        cutStringTag.doTag();

        ServletResponse _cutStringTagResponse = cutStringTagContext.getResponse();
        String _cutStringResult = ((MockHttpServletResponse)_cutStringTagResponse).getContentAsString();

        log.debug("cut string 결과 : {}", _cutStringResult);

        assertEquals(_cutStringResult, _expectedCutString, "말줄임 처리 실패");
    }

    @Test
    public void failTest() throws IOException, JspException {
        //
        // new tag
        //
        Calendar _calendar = Calendar.getInstance();

        _calendar.set(Calendar.DAY_OF_MONTH, _calendar.get(Calendar.DAY_OF_MONTH) - 23);

        newTag.setCompareDate(_calendar.getTime());

        newTag.doTag();

        ServletResponse _servletResponse = newTagContext.getResponse();
        String _outputResult = ((MockHttpServletResponse)_servletResponse).getContentAsString();

        log.debug("NewTag 결과 : {}", _outputResult);

        assertTrue(StringUtils.isBlank(_outputResult), "일자 비교후 tag 출력 실패");

        //
        // masking
        //
        final String _maskingSource = "";
        final String _expected = "값없음";

        maskingTag.setMaskingCount(4);
        maskingTag.setTarget(_maskingSource);
        maskingTag.setEmpty(_expected);

        maskingTag.doTag();

        ServletResponse _maskingTagResponse = maskingTagContext.getResponse();

        String _maskingResult = ((MockHttpServletResponse)_maskingTagResponse).getContentAsString();

        assertEquals(_maskingResult, _expected, "마스킹 처리 실패");

        //
        // cut string
        //
        final String _cutStringSource = "이것은 한글과 English가 함께 있는 문장 입니다.";

        cutStringTag.setLimit(_cutStringSource.length() + 10); // 길이 초과
        cutStringTag.setTarget(_cutStringSource);
        cutStringTag.doTag();

        ServletResponse _cutStringTagResponse = cutStringTagContext.getResponse();
        String _cutStringResult = ((MockHttpServletResponse)_cutStringTagResponse).getContentAsString();

        log.debug("cut string 결과 : {}", _cutStringResult);

        assertEquals(_cutStringResult, _cutStringSource, "말줄임 처리 실패");
    }
}
