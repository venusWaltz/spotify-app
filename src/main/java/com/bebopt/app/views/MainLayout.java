package com.bebopt.app.views;

import com.bebopt.app.api.SpotifyApiClient;
import com.bebopt.app.security.AuthenticatedUser;
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

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The {@code MainLayout} class provides the navigation and header.
 */
public class MainLayout extends AppLayout {

    /**
     * Nested static class representing a menu item.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        /**
         * Constructs a new menu item for each page of the application.
         * 
         * @param menuTitle The title of the menu item.
         * @param icon      The icon of the menu item.
         * @param view      The view class associated with the menu item for routing.
         */
        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER,
                    Padding.Horizontal.SMALL, TextColor.BODY);
            link.setRoute(view);
            Span text = new Span(menuTitle);
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);
            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        /**
         * Gets the view class associated with the menu item.
         * 
         * @return The view class.
         */
        public Class<?> getView() {
            return view;
        }
    }

    private AuthenticatedUser authenticatedUser;

    /**
     * Constructs a new {@code MainLayout}.
     * 
     * @param authenticatedUser The authenticated user.
     */
    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        addToNavbar(createHeaderContent());
    }

    /**
     * Creates the header content of the layout.
     * 
     * @return The header content component.
     */
    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

        H1 appName = new H1("Bebopt");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        layout.add(appName);

        if (AuthenticatedUser.isLoggedIn()) {
            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");
            MenuItem userName = userMenu.addItem("");
            Avatar avatar = new Avatar(SpotifyApiClient.getUser().getDisplayName());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            try {
                avatar.setImage(SpotifyApiClient.getUser().getImages()[0].getUrl());
            } catch (Exception e) {
                avatar.setImage("images/empty-plant.png");
            }

            Div div = new Div();
            div.add(avatar);
            div.add(SpotifyApiClient.getUser().getDisplayName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });
            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor(SpotifyApiClient.getSpotifyLoginUri(), "Sign in");
            loginLink.setId("sign-in");
            layout.add(loginLink);
        }

        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        /* Wrap the links in a list; improves accessibility. */
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);
        for (MenuItemInfo menuItem : createPublicMenuItems()) {
            list.add(menuItem);
        }
        for (MenuItemInfo menuItem : createMenuItems()) {
            if (AuthenticatedUser.isLoggedIn()) {
                list.add(menuItem);
            }
        }

        header.add(layout, nav);
        return header;
    }

    /**
     * Creates the public menu items.
     * 
     * @return The array of public {@code MenuItemInfo} objects.
     */
    private MenuItemInfo[] createPublicMenuItems() {
        return new MenuItemInfo[] {
                new MenuItemInfo("Home", LineAwesomeIcon.HOME_SOLID.create(), HomeView.class),
        };
    }

    /**
     * Creates the authenticated menu items.
     * 
     * @return The array of authenticated {@code MenuItemInfo} objects.
     */
    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[] {
                new MenuItemInfo("Statistics", LineAwesomeIcon.CHART_BAR_SOLID.create(), StatisticsView.class),
                new MenuItemInfo("Playlists", LineAwesomeIcon.LIST_OL_SOLID.create(), PlaylistsView.class),
                // ! Recommendations endpoint deprecated
                // new MenuItemInfo("Recommendations", LineAwesomeIcon.MUSIC_SOLID.create(),
                // RecommendationsView.class),
        };
    }
}
