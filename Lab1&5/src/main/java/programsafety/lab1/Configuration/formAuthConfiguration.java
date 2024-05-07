package programsafety.lab1.Configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
@EnableWebSecurity
public class formAuthConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(httpRequest -> httpRequest
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
