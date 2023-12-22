package ch.martinelli.vj.domain.user;

import ch.martinelli.vj.db.tables.records.LoginUserRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserWithRoles {
    private LoginUserRecord user;
    private Set<String> roles;

    public UserWithRoles() {
        this(new LoginUserRecord(), new HashSet<>());
    }

    public UserWithRoles(LoginUserRecord user, List<String> roles) {
        this(user, new HashSet<>(roles));
    }

    public UserWithRoles(LoginUserRecord user, Set<String> roles) {
        this.user = user;
        this.roles = roles;
    }

    public LoginUserRecord getUser() {
        return user;
    }

    public void setUser(LoginUserRecord user) {
        this.user = user;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
