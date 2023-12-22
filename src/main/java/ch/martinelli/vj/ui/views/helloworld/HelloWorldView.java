package ch.martinelli.vj.ui.views.helloworld;

import ch.martinelli.vj.ui.layout.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HelloWorldView extends HorizontalLayout {

    public HelloWorldView() {
        setMargin(true);

        var name = new TextField("Your name");
        var sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello %s".formatted(name.getValue()), 3000, Notification.Position.TOP_END);
        });
        sayHello.addClickShortcut(Key.ENTER);

        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
    }

}
