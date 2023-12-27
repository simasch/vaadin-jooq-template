package ch.martinelli.vj.ui.views.helloworld;

import ch.martinelli.vj.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static com.github.mvysny.kaributesting.v10.NotificationsKt.expectNotifications;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldViewTest extends KaribuTest {

    @BeforeEach
    void navigate() {
        UI.getCurrent().navigate(HelloWorldView.class);
    }

    @Test
    void say_hello() {
        var appName = _get(H1.class);
        assertThat(appName.getText()).isEqualTo("Vaadin jOOQ Template");

        var title = _get(H2.class);
        assertThat(title.getText()).isEqualTo("Hello World");

        _setValue(_get(TextField.class, s -> s.withId("name")), "Test");
        _click(_get(Button.class, s -> s.withId("say-hello")));

        expectNotifications("Hello Test");
    }
}