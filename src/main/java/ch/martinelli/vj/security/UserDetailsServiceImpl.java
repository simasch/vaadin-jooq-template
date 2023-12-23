package ch.martinelli.vj.security;

import ch.martinelli.vj.db.tables.records.UserRecord;
import ch.martinelli.vj.domain.user.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userService.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + username));
        return new User(user.getUsername(), user.getHashedPassword(), getAuthorities(user));
    }

    private List<SimpleGrantedAuthority> getAuthorities(UserRecord user) {
        return userService.findRolesByUsername(user.getUsername())
                .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).toList();
    }

}
