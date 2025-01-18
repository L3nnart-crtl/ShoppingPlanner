package backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FallbackController {

    @GetMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";  // Weiterleitung an die index.html
    }
}
