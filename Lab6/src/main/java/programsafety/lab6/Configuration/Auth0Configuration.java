package programsafety.lab6.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class Auth0Configuration {

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(httpRequests -> {
                    httpRequests.requestMatchers("/home").permitAll();
                    httpRequests.requestMatchers("/secure").authenticated();
                    httpRequests.anyRequest().permitAll();
                })
                .oauth2Login(withDefaults())
                .logout(logout -> logout
                        .addLogoutHandler(logoutHandler()))
                .build();
    }

    private LogoutHandler logoutHandler() {
        return ((request, response, authentication) -> {
            try {
                String baseUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath().build().toUriString();
                response.sendRedirect(issuer + "v2/logout?client_id="
                                    + clientId + "&returnTo=" + baseUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
