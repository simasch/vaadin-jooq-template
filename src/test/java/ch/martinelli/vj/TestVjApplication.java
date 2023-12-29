package ch.martinelli.vj;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

@Import(TestVjConfiguration.class)
public class TestVjApplication {

    public static void main(String[] args) {
        SpringApplication.from(VjApplication::main).with(TestVjApplication.class).run(args);
    }
}
