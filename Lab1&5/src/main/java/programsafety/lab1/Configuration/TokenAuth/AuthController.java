package programsafety.lab1.Configuration.TokenAuth;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;

    @PostMapping("/token")
    public String getToken(Authentication authentication) {
        LOG.debug("Generate token for user '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted '{}'", token);
        return token;
    }
}
