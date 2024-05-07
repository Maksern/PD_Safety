package programsafety.lab1.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class UserConfiguration {
    @Bean
    InMemoryUserDetailsManager user() {
        return new InMemoryUserDetailsManager(
                User.withUsername("maksym")
                        .password("{noop}maksmaks")
                        .build()
        );
    }
}
