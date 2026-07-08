package com.bebopt.app.views;

import com.bebopt.app.api.SpotifyService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * The {@code RecommendationsView} class displays recommendations for tracks and
 * related artists.
 */
@AnonymousAllowed
@PageTitle("Recommendations")
@Route(value = "Recommendations", layout = MainLayout.class)
public class RecommendationsView extends Div {

    private Div recommendedTracksTab;
    private Div recommendedArtistsTab;
    private TabSheet tabsheet;

    private OrderedList tracksContainer;
    private OrderedList artistsContainer;

    /**
     * Constructor for the {@code RecommendationsView} class.
     */
    public RecommendationsView() {
        loadRecommendations();
        createTabSheet();
        add(tabsheet);
    }

    /**
     * Loads recommendation data.
     */
    private void loadRecommendations() {
        tracksContainer = SpotifyService.getRecommendedTracks();
        artistsContainer = SpotifyService.getRelatedArtists();
    }

    /**
     * Create the TabSheet and tabs.
     */
    private void createTabSheet() {
        tabsheet = new TabSheet();
        recommendedTracksTab = createTracksTab();
        recommendedArtistsTab = createArtistsTab();
        tabsheet.add("Recommended Tracks", recommendedTracksTab);
        tabsheet.add("Related Artists", recommendedArtistsTab);
        tabsheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        addClassNames("page-view", "recommendations-view");
    }

    /**
     * Create the tab to display recommended tracks.
     * 
     * @return The "Recommendations" tab.
     */
    private Div createTracksTab() {
        Div tab = createTab("Recommendations");
        tab.add(tracksContainer);
        return tab;
    }

    /**
     * Create the tab to display recommended related artists.
     * 
     * @return The "Related Artists" tab.
     */
    private Div createArtistsTab() {
        Div tab = createTab("Related Artists");
        tab.addClassNames("artist-view");
        artistsContainer.addClassNames("artists-container");
        tab.add(artistsContainer);
        return tab;
    }

    /**
     * Create a base tab layout.
     * 
     * @param headerText    The title used for the header text of the tab.
     * @param viewClassName The class name for the tab.
     * @return The new tab.
     */
    private Div createTab(String headerText) {
        Div tab = new Div();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2(headerText);

        horizontalLayout.addClassNames("header-container");
        headerContainer.add(header);
        horizontalLayout.add(headerContainer);
        tab.add(horizontalLayout);
        return tab;
    }
}
