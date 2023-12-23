package ch.martinelli.vj.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class Notifier extends Notification {

    public static final int DURATION = 3000;

    public static void info(String message) {
        showNotification(message);
    }

    public static void success(String message) {
        var notification = showNotification(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void warn(String message) {
        var notification = showNotification(message);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    public static void error(String message) {
        var text = new NativeLabel(message);
        var close = new Button("OK");

        var content = new HorizontalLayout(text, close);
        content.addClassName(LumoUtility.AlignItems.CENTER);

        var notification = new Notification(content);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Position.TOP_END);

        close.addClickListener(buttonClickEvent -> notification.close());
        notification.open();
        close.focus();
    }

    private static Notification showNotification(String message) {
        return show(message, DURATION, Position.TOP_END);
    }

}
