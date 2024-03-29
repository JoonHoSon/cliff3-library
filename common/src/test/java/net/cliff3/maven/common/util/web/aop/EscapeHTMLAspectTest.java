package net.cliff3.maven.common.util.web.aop;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * EscapeHTMLAspectTest
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@ContextConfiguration(locations = {"classpath:dispatcher-simple.xml", "classpath:applicationContext.xml"})
public class EscapeHTMLAspectTest {
    @Autowired
    private ApplicationContext context;

    @Test
    @Order(1)
    @DisplayName("EscapeHTML test")
    public void testEscapeHTML() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/board/form");

        final String source = "<script>alert('abc');</script><a href=\"http://www.apple.com/kr\" target=\"_blank\">테스트</a>";
        final String expected = "&lt;script&gt;alert('abc');&lt;/script&gt;<a href=\"http://www.apple.com/kr\" target=\"_blank\">테스트</a>";

        request.addParameter("content", source);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)context)
                                         .build();

        MvcResult result = mockMvc.perform(post("/board/form").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                              .param("content", source))
                                  .andReturn();
        EscapeSampleDTO escaped = (EscapeSampleDTO)result.getModelAndView().getModelMap().get("dto");

        assertNotNull(escaped);
        assertEquals(escaped.getContent(), expected, "escape 처리 실패");
    }
}
