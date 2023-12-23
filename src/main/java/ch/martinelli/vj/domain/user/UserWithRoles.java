package ch.martinelli.vj.domain.user;

import ch.martinelli.vj.db.tables.records.UserRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserWithRoles {
    private final UserRecord user;
    private Set<String> roles;

    public UserWithRoles() {
        this.user = new UserRecord();
        this.roles = new HashSet<>();
    }

    public UserWithRoles(UserRecord user, List<String> roles) {
        this.user = user;
        this.roles = new HashSet<>(roles);
    }

    public UserRecord getUser() {
        return user;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
