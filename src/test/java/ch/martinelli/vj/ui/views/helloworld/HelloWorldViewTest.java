package ch.martinelli.vj.ui.views.helloworld;

import ch.martinelli.vj.ui.AbstractKaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldViewTest extends AbstractKaribuTest {

    @BeforeEach
    void navigate() {
        UI.getCurrent().navigate(HelloWorldView.class);
    }

    @Test
    void say_hello_en() {
        var appName = _get(H1.class);
        assertThat(appName.getText()).isEqualTo("Vaadin jOOQ Template");

        var title = _get(H2.class);
        assertThat(title.getText()).isEqualTo("Hello World");
    }
}