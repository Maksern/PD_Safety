package programsafety.lab1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {
    @GetMapping
    public String home() {
        return "Welcome, Home";
    }

    @GetMapping("/secure")
    public String sixthDeparture(Principal principal) {
        String name = principal.getName();
        return name.substring(0, 1).toUpperCase() + name.substring(1) + ", welcome in sixth departure";
    }
}
