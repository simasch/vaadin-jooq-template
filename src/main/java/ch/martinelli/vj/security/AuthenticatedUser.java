package ch.martinelli.vj.security;

import ch.martinelli.vj.db.tables.records.UserRecord;
import ch.martinelli.vj.domain.user.UserService;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final AuthenticationContext authenticationContext;
    private final UserService userService;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserService userService) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }

    public Optional<UserRecord> get() {
        return authenticationContext.getAuthenticatedUser(Jwt.class)
                .flatMap(jwt -> userService.findUserByUsername(jwt.getSubject()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
