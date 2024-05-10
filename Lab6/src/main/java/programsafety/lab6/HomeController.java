package programsafety.lab6;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {

    @GetMapping("/*")
    public String redirectHome() {
        return "redirect:/home";
    }
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String username = getUserName(principal);
            model.addAttribute("username", username);
        }

        return "home";
    }
    @GetMapping("/loginPage")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/secure")
    @ResponseBody
    public String sixthDeparture(@AuthenticationPrincipal OAuth2User principal) {
        String username = getUserName(principal);
        return username.substring(0, 1).toUpperCase() + username.substring(1) + ", welcome in Sixth Departure";
    }



    private String getUserName(OAuth2User principal) {
        String[] types = {"login", "nickname", "given_name", "name"};
        int i = 0;
        String username = null;

        while (username == null){
            username = principal.getAttribute(types[i]);
            i++;
        }

        return username;
    }
}
