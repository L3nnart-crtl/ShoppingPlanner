package backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle fallback requests for client-side routing in a single-page application (SPA).
 * <p>
 * This ensures that any unmatched routes in the backend are forwarded to the SPA's `index.html`,
 * allowing the client-side application to handle the routing.
 * </p>
 */
@Controller
public class FallbackController {

    /**
     * Redirects any unmatched paths to the `index.html` of the application.
     * <p>
     * The mapping uses a regular expression to match all paths that do not contain a period (`.`),
     * excluding static resources like JavaScript, CSS, or image files.
     * </p>
     *
     * @return a string that forwards the request to `/index.html`
     */
    @GetMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}
