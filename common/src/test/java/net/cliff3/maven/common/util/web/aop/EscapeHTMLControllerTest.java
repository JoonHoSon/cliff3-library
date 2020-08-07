package net.cliff3.maven.common.util.web.aop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * EscapeHTMLControllerTest
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Controller
public class EscapeHTMLControllerTest {
    @RequestMapping(value = "/board/form", method = RequestMethod.POST)
    public String save(EscapeSampleDTO dto, Model model) {
        model.addAttribute("dto", dto);

        return "test";
    }
}
