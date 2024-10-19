package ch.martinelli.vj.ui.views.user;

import ch.martinelli.vj.domain.user.Role;
import ch.martinelli.vj.domain.user.UserService;
import ch.martinelli.vj.domain.user.UserWithRoles;
import ch.martinelli.vj.ui.components.Notifier;
import ch.martinelli.vj.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import io.seventytwo.vaadinjooq.util.VaadinJooqUtil;
import jakarta.annotation.security.RolesAllowed;
import org.jooq.exception.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static ch.martinelli.vj.db.tables.User.USER;

@RolesAllowed(Role.ADMIN)
@Route(value = "users", layout = MainLayout.class)
public class UserView extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    private final transient UserService userService;
    private final transient PasswordEncoder passwordEncoder;
    private final Grid<UserWithRoles> grid = new Grid<>();
    private final Button cancel = new Button(getTranslation("Cancel"));
    private final Button save = new Button(getTranslation("Save"));
    private final Binder<UserWithRoles> binder = new Binder<>();
    private transient UserWithRoles user;
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
                .setHeader(getTranslation("Username"))
                .setSortable(true).setSortProperty(USER.USERNAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> u.getUser().getFirstName())
                .setHeader(getTranslation("First Name"))
                .setSortable(true).setSortProperty(USER.FIRST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> u.getUser().getLastName())
                .setHeader(getTranslation("Last Name"))
                .setSortable(true).setSortProperty(USER.LAST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(u -> String.join(", ", u.getRoles()))
                .setHeader(getTranslation("Roles"))
                .setAutoWidth(true);

        var addIcon = VaadinIcon.PLUS.create();
        addIcon.addClickListener(e -> clearForm());
        grid.addComponentColumn(u -> {
                    var deleteIcon = VaadinIcon.TRASH.create();
                    deleteIcon.addClickListener(e ->
                            new ConfirmDialog(
                                    getTranslation("Delete User?"),
                                    getTranslation("Do you really want to delete the user {0}?", u.getUser().getUsername()),
                                    getTranslation("Delete"),
                                    confirmEvent -> {
                                        userService.deleteByUsername(u.getUser().getUsername());
                                        clearForm();
                                        refreshGrid();
                                    },
                                    getTranslation("Cancel"),
                                    cancelEvent -> {
                                    })
                                    .open());
                    return deleteIcon;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setHeader(addIcon);

        grid.sort(GridSortOrder.asc(usernameColumn).build());
        grid.setItems(query ->
                userService.findAllUserWithRoles(query.getOffset(), query.getLimit(), VaadinJooqUtil.orderFields(USER, query)).stream()
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
        usernameField.setReadOnly(false);
        user = null;
        binder.readBean(null);
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

        usernameField = new TextField(getTranslation("Username"));
        binder.forField(usernameField)
                .asRequired()
                .bind(u -> u.getUser().getUsername(), (u, s) -> u.getUser().setUsername(s));

        var firstNameField = new TextField(getTranslation("First Name"));
        binder.forField(firstNameField)
                .asRequired()
                .bind(u -> u.getUser().getFirstName(), (u, s) -> u.getUser().setFirstName(s));

        var lastNameField = new TextField(getTranslation("Last Name"));
        binder.forField(lastNameField)
                .asRequired()
                .bind(u -> u.getUser().getLastName(), (u, s) -> u.getUser().setLastName(s));

        var passwordField = new PasswordField(getTranslation("Password"));
        binder.forField(passwordField)
                .asRequired()
                .bind(u -> "", (u, s) -> u.getUser().setHashedPassword(passwordEncoder.encode(s)));

        var roleMultiSelect = new MultiSelectComboBox<String>(getTranslation("Roles"));
        binder.forField(roleMultiSelect)
                .bind(UserWithRoles::getRoles, UserWithRoles::setRoles);

        roleMultiSelect.setItems(Set.of(Role.ADMIN, Role.USER));

        formLayout.add(usernameField, firstNameField, lastNameField, passwordField, roleMultiSelect);

        return formLayout;
    }

    private HorizontalLayout createButtonLayout() {
        var buttonLayout = new HorizontalLayout();

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    if (user == null) {
                        user = new UserWithRoles();
                    }

                    binder.writeChangedBindingsToBean(user);

                    try {
                        userService.save(user);
                        Notifier.success(getTranslation("User saved"));
                    } catch (DataAccessException ex) {
                        Notifier.error(getTranslation("User could not be saved!"));
                    }
                } catch (ValidationException ex) {
                    Notifier.error(getTranslation("There have been validation errors!"));
                    ex.getValidationErrors().forEach(validationResult ->
                            Notifier.error(validationResult.getErrorMessage()));
                }

                clearForm();
                refreshGrid();

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

    @Override
    public String getPageTitle() {
        return getTranslation("Users");
    }
}
