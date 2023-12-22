package ch.martinelli.vj.ui.views.user;

import ch.martinelli.vj.domain.user.Role;
import ch.martinelli.vj.domain.user.UserService;
import ch.martinelli.vj.domain.user.UserWithRoles;
import ch.martinelli.vj.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import io.seventytwo.vaadinjooq.util.VaadinJooqUtil;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static ch.martinelli.vj.db.tables.LoginUser.LOGIN_USER;

@RolesAllowed(Role.ADMIN)
@PageTitle("Users")
@Route(value = "users", layout = MainLayout.class)
public class UserView extends Div implements HasUrlParameter<String> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Grid<UserWithRoles> grid = new Grid<>();
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Binder<UserWithRoles> binder = new Binder<>();
    private UserWithRoles user;
    private TextField usernameField;

    public UserView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setSizeFull();

        // Create UI
        var splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(80);
        add(splitLayout);

        // Configure Grid
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        var usernameColumn = grid.addColumn(u -> u.getUser().getUsername())
                .setHeader("Username")
                .setSortable(true).setSortProperty(LOGIN_USER.USERNAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> u.getUser().getFirstName())
                .setHeader("First Name")
                .setSortable(true).setSortProperty(LOGIN_USER.FIRST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> u.getUser().getLastName())
                .setHeader("Last Name")
                .setSortable(true).setSortProperty(LOGIN_USER.LAST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> String.join(", ", u.getRoles()))
                .setHeader("Roles")
                .setAutoWidth(true);

        grid.sort(GridSortOrder.asc(usernameColumn).build());
        grid.setItems(query ->
                userService.findAllUserWithRoles(query.getOffset(), query.getLimit(), VaadinJooqUtil.orderFields(LOGIN_USER, query)).stream()
        );

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(UserView.class, event.getValue().getUser().getUsername());
            } else {
                clearForm();
                UI.getCurrent().navigate(UserView.class);
            }
        });

        var gridLayout = new VerticalLayout(grid);
        gridLayout.setSizeFull();
        splitLayout.addToPrimary(gridLayout);

        var form = createForm();
        var buttons = createButtonLayout();

        var formLayout = new VerticalLayout(form, buttons);
        formLayout.setSizeFull();
        splitLayout.addToSecondary(formLayout);
    }

    private void clearForm() {
        user = null;
        binder.readBean(user);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String username) {
        if (username != null) {
            userService.findUserWithRolesByUsername(username).ifPresent(userRecord -> user = userRecord);
        } else {
            user = null;
        }
        binder.readBean(user);
        grid.select(user);

        if (user != null && user.getUser().getUsername() != null) {
            usernameField.setReadOnly(true);
        }
    }

    private FormLayout createForm() {
        var formLayout = new FormLayout();

        usernameField = new TextField("Username");
        binder.forField(usernameField)
                .asRequired()
                .bind(u -> u.getUser().getUsername(), (u, s) -> u.getUser().setUsername(s));

        var firstNameField = new TextField("First Name");
        binder.forField(firstNameField)
                .asRequired()
                .bind(u -> u.getUser().getFirstName(), (u, s) -> u.getUser().setFirstName(s));

        var lastNameField = new TextField("Last Name");
        binder.forField(lastNameField)
                .asRequired()
                .bind(u -> u.getUser().getLastName(), (u, s) -> u.getUser().setLastName(s));

        var passwordField = new PasswordField("Password");
        binder.forField(passwordField)
                .asRequired()
                .bind(u -> "", (u, s) -> u.getUser().setHashedPassword(passwordEncoder.encode(s)));

        var roleMultiSelect = new MultiSelectComboBox<String>("Roles");
        binder.forField(roleMultiSelect)
                        .bind(UserWithRoles::getRoles, UserWithRoles::setRoles);

        roleMultiSelect.setItems(Set.of(Role.ADMIN, Role.USER));

        formLayout.add(usernameField, firstNameField, lastNameField, passwordField, roleMultiSelect);

        return formLayout;
    }

    private HorizontalLayout createButtonLayout() {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                if (user == null) {
                    user = new UserWithRoles();
                }
                binder.writeBeanIfValid(user);
                userService.save(user);

                clearForm();

                refreshGrid();

                Notification.show("Person saved");

                UI.getCurrent().navigate(UserView.class);
            }
        });

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save, cancel);

        return buttonLayout;
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

}
