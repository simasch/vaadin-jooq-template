package ch.martinelli.vj;

import org.springframework.boot.SpringApplication;

public class TestVjApplication {

    public static void main(String[] args) {
        SpringApplication.from(VjApplication::main).with(TestVjConfiguration.class).run(args);
    }
}
