package ch.martinelli.vj.domain.person;

import ch.martinelli.vj.db.tables.records.PersonRecord;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.martinelli.vj.db.tables.Person.PERSON;

@Service
public class PersonService {

    private final DSLContext ctx;

    public PersonService(DSLContext ctx) {
        this.ctx = ctx;
    }

    public List<PersonRecord> findAll(int offset, int limit, List<OrderField<?>> orderFields) {
        return ctx.selectFrom(PERSON)
                .orderBy(orderFields)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Transactional
    public void save(PersonRecord person) {
        ctx.attach(person);
        person.store();
    }

    public Optional<PersonRecord> findById(long id) {
        return ctx.selectFrom(PERSON)
                .where(PERSON.ID.eq(id))
                .fetchOptional();
    }

    @Transactional
    public void deleteById(long id) {
        ctx.deleteFrom(PERSON)
                .where(PERSON.ID.eq(id))
                .execute();
    }
}
