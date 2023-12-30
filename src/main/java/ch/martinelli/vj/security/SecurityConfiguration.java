package ch.martinelli.vj.security;

import ch.martinelli.vj.ui.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private final String authSecret;

    public SecurityConfiguration(@Value("${jwt.auth.secret}") String authSecret) {
        this.authSecret = authSecret;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

        super.configure(http);

        setLoginView(http, LoginView.class);

        // https://vaadin.com/blog/jwt-authentication-with-vaadin-flow-for-better-developer-and-user-experience
        setStatelessAuthentication(http,
                new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256),
                "ch.martinelli.vj", 3600);
    }

}
