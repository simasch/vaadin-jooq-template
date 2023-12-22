package ch.martinelli.vj.domain.user;

import ch.martinelli.vj.db.tables.records.LoginUserRecord;
import ch.martinelli.vj.db.tables.records.UserRoleRecord;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.Record1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.martinelli.vj.db.tables.LoginUser.LOGIN_USER;
import static ch.martinelli.vj.db.tables.UserRole.USER_ROLE;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;


@Transactional(readOnly = true)
@Service
public class UserService {

    private final DSLContext ctx;

    public UserService(DSLContext ctx) {
        this.ctx = ctx;
    }

    public Optional<? extends LoginUserRecord> findUserByUsername(String username) {
        return ctx.selectFrom(LOGIN_USER)
                .where(LOGIN_USER.USERNAME.eq(username))
                .fetchOptional();
    }

    public Optional<UserWithRoles> findUserWithRolesByUsername(String username) {
        return ctx.select(LOGIN_USER,
                        multiset(select(USER_ROLE.ROLE)
                                .from(USER_ROLE)
                                .where(USER_ROLE.USERNAME.eq(LOGIN_USER.USERNAME))
                        ).convertFrom(r -> r.map(Record1::value1)))
                .from(LOGIN_USER)
                .where(LOGIN_USER.USERNAME.eq(username))
                .fetchOptional(mapping(UserWithRoles::new));
    }

    public List<UserRoleRecord> findRolesByUsername(String username) {
        return ctx.selectFrom(USER_ROLE)
                .where(USER_ROLE.USERNAME.eq(username))
                .fetch();
    }

    public List<UserWithRoles> findAllUserWithRoles(int offset, int limit, List<OrderField<?>> orderFields) {
        return ctx.select(LOGIN_USER,
                        multiset(select(USER_ROLE.ROLE)
                                .from(USER_ROLE)
                                .where(USER_ROLE.USERNAME.eq(LOGIN_USER.USERNAME))
                        ).convertFrom(r -> r.map(Record1::value1)))
                .from(LOGIN_USER)
                .orderBy(orderFields)
                .offset(offset)
                .limit(limit)
                .fetch(mapping(UserWithRoles::new));
    }

    @Transactional
    public void save(UserWithRoles userWithRoles) {
        var user = userWithRoles.getUser();
        ctx.attach(user);
        user.store();

        // First delete all assigned roles
        ctx.deleteFrom(USER_ROLE)
                .where(USER_ROLE.USERNAME.eq(user.getUsername()))
                .execute();

        // Then add all roles
        for (var role : userWithRoles.getRoles()) {
            var userRole = new UserRoleRecord(user.getUsername(), role);
            ctx.attach(userRole);
            userRole.store();
        }
    }
}
