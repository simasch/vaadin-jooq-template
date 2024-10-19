package ch.martinelli.vj.ui.views.helloworld;

import ch.martinelli.vj.ui.components.Notifier;
import ch.martinelli.vj.ui.layout.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HelloWorldView extends HorizontalLayout implements HasDynamicTitle {

    public HelloWorldView() {
        setMargin(true);

        var name = new TextField(getTranslation("Your name"));
        name.setId("name");

        var sayHello = new Button(getTranslation("Say hello"));
        sayHello.setId("say-hello");
        sayHello.addClickListener(e -> Notifier.info(getTranslation("Hello {0}", name.getValue())));
        sayHello.addClickShortcut(Key.ENTER);

        add(name, sayHello);

        setVerticalComponentAlignment(Alignment.END, name, sayHello);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Hello World");
    }
}
