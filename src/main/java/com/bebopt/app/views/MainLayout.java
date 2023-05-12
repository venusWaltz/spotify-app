package com.bebopt.app.views;

import com.bebopt.app.data.controller.AuthController;
import com.bebopt.app.data.controller.SpotifyService;
import com.bebopt.app.data.entity.SpotifyUser;
import com.bebopt.app.data.entity.User;
import com.bebopt.app.security.AuthenticatedUser;
import com.bebopt.app.views.about.AboutView;
import com.bebopt.app.views.home.HomeView;
import com.bebopt.app.views.playlists.PlaylistsView;
import com.bebopt.app.views.recommendations.RecommendationsView;
import com.bebopt.app.views.stats.StatsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */


    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
                    TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

        H1 appName = new H1("Spotify App");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        layout.add(appName);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            
            SpotifyUser spotifyUser = new SpotifyUser(SpotifyService.getCurrentUser());

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            if (spotifyUser != null) {
                // add user profile info here
                Avatar avatar = new Avatar(SpotifyUser.getUsername());
                avatar.setImage(SpotifyUser.getProfileImage());
                avatar.setThemeName("xsmall");
                avatar.getElement().setAttribute("tabindex", "-1");

                MenuItem userName = userMenu.addItem("");
                Div div = new Div();
                div.add(avatar);
                div.add(SpotifyUser.getUsername());
                div.add(new Icon("lumo", "dropdown"));
                div.getElement().getStyle().set("display", "flex");
                div.getElement().getStyle().set("align-items", "center");
                div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
                userName.add(div);
                userName.getSubMenu().addItem("Sign out", e -> {
                    authenticatedUser.logout();
                });
            }

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor(AuthController.spotifyLogin(), "Sign in");
            loginLink.setId("sign-in");
            layout.add(loginLink);
        }

        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }

        header.add(layout, nav);
        return header;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[] { //
                new MenuItemInfo("Home", LineAwesomeIcon.HOME_SOLID.create(), HomeView.class), //

                new MenuItemInfo("Stats", LineAwesomeIcon.CHART_BAR_SOLID.create(), StatsView.class), //

                new MenuItemInfo("Playlists", LineAwesomeIcon.LIST_OL_SOLID.create(), PlaylistsView.class), //

                new MenuItemInfo("Recommendations", LineAwesomeIcon.MUSIC_SOLID.create(), RecommendationsView.class), //

                new MenuItemInfo("About", LineAwesomeIcon.INFO_CIRCLE_SOLID.create(), AboutView.class), //

        };
    }

}
