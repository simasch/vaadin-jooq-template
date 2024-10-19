package ch.martinelli.vj.ui.layout;

import ch.martinelli.vj.security.AuthenticatedUser;
import ch.martinelli.vj.ui.views.helloworld.HelloWorldView;
import ch.martinelli.vj.ui.views.person.PersonView;
import ch.martinelli.vj.ui.views.user.UserView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;

public class MainLayout extends AppLayout {

    private final transient AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    private H2 viewTitle;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        var appName = new H1("Vaadin jOOQ Template");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        var header = new Header(appName);

        var scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        var nav = new SideNav();

        if (accessChecker.hasAccess(HelloWorldView.class)) {
            nav.addItem(new SideNavItem(getTranslation("Hello World"), HelloWorldView.class, VaadinIcon.GLOBE.create()));

        }
        if (accessChecker.hasAccess(PersonView.class)) {
            nav.addItem(new SideNavItem(getTranslation("Persons"), PersonView.class, VaadinIcon.ARCHIVES.create()));
        }
        if (accessChecker.hasAccess(UserView.class)) {
            nav.addItem(new SideNavItem(getTranslation("Users"), UserView.class, VaadinIcon.USER.create()));
        }

        return nav;
    }

    private Footer createFooter() {
        var layout = new Footer();

        var optionalUserRecord = authenticatedUser.get();
        if (optionalUserRecord.isPresent()) {
            var user = optionalUserRecord.get();

            var avatar = new Avatar("%s %s".formatted(user.getFirstName(), user.getLastName()));
            var resource = new StreamResource("profile-pic", () -> new ByteArrayInputStream(user.getPicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            var userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            var userName = userMenu.addItem("");

            var div = new Div();
            div.add(avatar);
            div.add("%s %s".formatted(user.getFirstName(), user.getLastName()));
            div.add(LumoIcon.DROPDOWN.create());
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
            userName.add(div);
            userName.getSubMenu().addItem(getTranslation("Sign out"), e -> authenticatedUser.logout());

            layout.add(userMenu);
        } else {
            var loginLink = new Anchor("login", getTranslation("Sign in"));
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        if (getContent() instanceof HasDynamicTitle hasDynamicTitle) {
            return hasDynamicTitle.getPageTitle() == null ? "" : hasDynamicTitle.getPageTitle();
        } else {
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "" : title.value();
        }
    }
}
