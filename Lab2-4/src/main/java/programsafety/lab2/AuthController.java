package programsafety.lab2;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class AuthController {
    public Map<String, String> session;
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.session = new HashMap<>();
        this.authService = authService;
    }

    @GetMapping("/*")
    public String home(Model model) {
        if(session.get("username") != null){
            model.addAttribute("username", session.get("username"));
            model.addAttribute("header", session.get("header"));
            model.addAttribute("payload", session.get("payload"));

            Long timeExp = Long.valueOf(session.get("expirationTime"));
            Long currentTime =  TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

            if((timeExp - currentTime) < 10){
                return "redirect:/updateToken";
            }
        }
        return "home";
    }

    @GetMapping("/loginPage")
    public String login(Model model) {
        if(session.get("message") != null){
            model.addAttribute("message", session.get("message"));
        }
        return "loginPage";
    }

    @GetMapping("/login")
    public String logIn(@RequestParam String username, @RequestParam String pass) {
        HttpResponse<JsonNode> response = authService.logIn(username, pass);

        if (response.getStatus() == 200) {
            String access_token = (String) response.getBody().getObject().get("access_token");
            String refresh_token = (String) response.getBody().getObject().get("refresh_token");
            authService.addParamsToSession(session, access_token, refresh_token, username);

            return "redirect:/home";
        }

        session.put("message", response.getBody().getObject().get("error_description").toString());

        return "redirect:/loginPage";
    }

    @GetMapping("/signupPage")
    public String signup(Model model) {
        if(session.get("message") != null){
            model.addAttribute("message", session.get("message"));
        }
        return "signupPage";
    }

    @GetMapping("/signup")
    public String signUp(@RequestParam String username, @RequestParam String pass) {
        HttpResponse<JsonNode> response = authService.signUp(username, pass);

        if (response.getStatus() == 201) {
            session.put("message", "Success create new user");
            return "redirect:/loginPage";
        }

        session.put("error", response.getBody().getObject().get("error_description").toString());

        return "redirect:/signupPage";
    }

    @GetMapping("/logout")
    public String logout() {
        session.clear();
        return "redirect:/home";
    }

    @GetMapping("/updateToken")
    public String updateToken() {
        HttpResponse<JsonNode> response = authService.updateTokenWithRefresh(session.get("refresh_token"));

        if (response.getStatus() == 200) {
            String access_token = (String) response.getBody().getObject().get("access_token");
            authService.updateAccessTokenInSession(session, access_token);
        } else {
            session.put("message", response.getBody().getObject().get("error_description").toString());
        }

        return "redirect:/home";
    }

    @GetMapping("/validateToken")
    @ResponseBody
    public String validateToken(@RequestParam(defaultValue = "") String addToToken ) throws JwkException {
        String codeJwt = session.get("access_token") + addToToken;
        DecodedJWT jwt = JWT.decode(codeJwt);
        JwkProvider jwkProvider = new UrlJwkProvider("https://dev-l5yejihpj316wdj1.us.auth0.com/");
        Jwk jwk = jwkProvider.get(jwt.getKeyId());
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

        StringBuilder answer = new StringBuilder("Checking <br>");
        int length = codeJwt.length();

        for (int i = 0; i < length; i += 60) {
            answer.append(codeJwt, i, Math.min(length, i + 60)).append("<br>");
        }
        try {
            algorithm.verify(jwt);
            return answer + "Token is valid";
        } catch (Exception exception) {
            return answer + "Token is invalid";
        }
    }
}

