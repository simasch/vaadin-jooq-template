package ch.martinelli.vj;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "vj")
public class VjApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(VjApplication.class, args);
    }
}
