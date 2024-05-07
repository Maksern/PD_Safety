package programsafety.lab6;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public String sixthDeparture(@AuthenticationPrincipal OAuth2User principal) {
       String username = principal.getAttribute("login");
        if (username == null) {
            username = principal.getAttribute("given_name");
        }
        
       return username.substring(0, 1).toUpperCase() + username.substring(1) + ", welcome in Sixth Departure";
    }
}
