package ch.martinelli.vj.ui.views.person;

import ch.martinelli.vj.db.tables.records.PersonRecord;
import ch.martinelli.vj.domain.person.PersonService;
import ch.martinelli.vj.domain.user.Role;
import ch.martinelli.vj.ui.components.Notifier;
import ch.martinelli.vj.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import io.seventytwo.vaadinjooq.util.VaadinJooqUtil;
import jakarta.annotation.security.RolesAllowed;
import org.jooq.exception.DataAccessException;

import static ch.martinelli.vj.db.tables.Person.PERSON;

@RolesAllowed({Role.USER, Role.ADMIN})
@Route(value = "persons", layout = MainLayout.class)
public class PersonView extends Div implements HasUrlParameter<Long>, HasDynamicTitle {

    private final transient PersonService personService;

    private final Grid<PersonRecord> grid = new Grid<>();

    private final Button cancel = new Button(getTranslation("Cancel"));
    private final Button save = new Button(getTranslation("Save"));

    private final Binder<PersonRecord> binder = new Binder<>();

    private PersonRecord person;

    public PersonView(PersonService personService) {
        this.personService = personService;

        setSizeFull();

        // Create UI
        var splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(80);
        add(splitLayout);

        // Configure Grid
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        var firstNameColumn = grid.addColumn(PersonRecord::getFirstName)
                .setHeader(getTranslation("First Name"))
                .setSortable(true).setSortProperty(PERSON.FIRST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(PersonRecord::getLastName)
                .setHeader(getTranslation("Last Name"))
                .setSortable(true).setSortProperty(PERSON.LAST_NAME.getName())
                .setAutoWidth(true);
        grid.addColumn(PersonRecord::getEmail)
                .setHeader(getTranslation("E-Mail"))
                .setSortable(true).setSortProperty(PERSON.EMAIL.getName())
                .setAutoWidth(true);
        grid.addComponentColumn(p -> {
                    var importantCheckbox = new Checkbox();
                    importantCheckbox.setReadOnly(true);
                    importantCheckbox.setValue(p.getImportant());
                    return importantCheckbox;
                })
                .setHeader(getTranslation("Important"))
                .setAutoWidth(true);

        var addIcon = VaadinIcon.PLUS.create();
        addIcon.addClickListener(e -> clearForm());
        grid.addComponentColumn(p -> {
                    var deleteIcon = VaadinIcon.TRASH.create();
                    deleteIcon.addClickListener(e ->
                            new ConfirmDialog(
                                    getTranslation("Delete Person?"),
                                    getTranslation("Do you really want to delete the person {0} {1}?", p.getFirstName(), p.getLastName()),
                                    getTranslation("Delete"),
                                    confirmEvent -> {
                                        personService.deleteById(p.getId());
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

        grid.sort(GridSortOrder.asc(firstNameColumn).build());
        grid.setItems(query ->
                personService.findAll(query.getOffset(), query.getLimit(), VaadinJooqUtil.orderFields(PERSON, query)).stream()
        );
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(PersonView.class, event.getValue().getId());
            } else {
                clearForm();
                UI.getCurrent().navigate(PersonView.class);
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
        person = null;
        binder.readBean(null);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long personId) {
        if (personId != null) {
            personService.findById(personId).ifPresent(personRecord -> person = personRecord);
        } else {
            person = null;
        }
        binder.readBean(person);
        grid.select(person);
    }

    private FormLayout createForm() {
        var formLayout = new FormLayout();

        var firstNameField = new TextField(getTranslation("First Name"));
        binder.forField(firstNameField)
                .asRequired()
                .bind(PersonRecord::getFirstName, PersonRecord::setFirstName);

        var lastNameField = new TextField(getTranslation("Last Name"));
        binder.forField(lastNameField)
                .asRequired()
                .bind(PersonRecord::getLastName, PersonRecord::setLastName);

        var emailField = new EmailField(getTranslation("Email"));
        binder.forField(emailField)
                .asRequired()
                .bind(PersonRecord::getEmail, PersonRecord::setEmail);

        var phoneField = new TextField(getTranslation("Phone"));
        binder.forField(phoneField)
                .asRequired()
                .bind(PersonRecord::getPhone, PersonRecord::setPhone);

        var dateOfBirthField = new DatePicker(getTranslation("Date of birth"));
        binder.forField(dateOfBirthField)
                .asRequired()
                .bind(PersonRecord::getDateOfBirth, PersonRecord::setDateOfBirth);

        var occupationField = new TextField(getTranslation("Occupation"));
        binder.forField(occupationField)
                .asRequired()
                .bind(PersonRecord::getOccupation, PersonRecord::setOccupation);

        var roleField = new TextField(getTranslation("Role"));
        binder.forField(roleField)
                .asRequired()
                .bind(PersonRecord::getRole, PersonRecord::setRole);

        var importantCheckbox = new Checkbox(getTranslation("Important"));
        binder.forField(importantCheckbox)
                .bind(PersonRecord::getImportant, PersonRecord::setImportant);

        formLayout.add(firstNameField, lastNameField, emailField, phoneField, dateOfBirthField, occupationField, roleField, importantCheckbox);

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
                    if (person == null) {
                        person = new PersonRecord();
                    }

                    binder.writeChangedBindingsToBean(person);

                    try {
                        personService.save(person);
                        Notifier.success(getTranslation("Person saved"));
                    } catch (DataAccessException ex) {
                        Notifier.error(getTranslation("Person could not be saved!"));
                    }
                } catch (ValidationException ex) {
                    Notifier.error(getTranslation("There have been validation errors!"));
                    ex.getValidationErrors().forEach(validationResult ->
                            Notifier.error(validationResult.getErrorMessage()));
                }

                clearForm();
                refreshGrid();

                UI.getCurrent().navigate(PersonView.class);
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
        return getTranslation("Persons");
    }
}
