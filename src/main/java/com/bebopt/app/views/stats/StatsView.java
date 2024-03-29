package com.bebopt.app.views.stats;

import com.bebopt.app.data.spotify.SpotifyService;
import com.bebopt.app.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Grid.Column;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;

import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Stats")
@Route(value = "stats", layout = MainLayout.class)
public class StatsView extends Div {

    private Div trackTab;
    private Div artistTab;
    private TabSheet tabsheet;

    private OrderedList genreContainer;
    Select<String> sortBy;

    private String shortTerm = "short_term";
    private String mediumTerm = "medium_term";
    private String longTerm = "long_term";

    // load user data from spotify into ordered lists
    OrderedList trackContainerShortTerm;
    OrderedList trackContainerMediumTerm;
    OrderedList trackContainerLongTerm;
    
    OrderedList artistContainerShortTerm;
    OrderedList artistContainerMediumTerm;
    OrderedList artistContainerLongTerm;

    public StatsView() {
        
        tabsheet = new TabSheet();

        trackContainerShortTerm = getTopTracks(shortTerm);
        trackContainerMediumTerm = getTopTracks(mediumTerm);
        trackContainerLongTerm = getTopTracks(longTerm);
        
        artistContainerShortTerm = getTopArtists(shortTerm);
        artistContainerMediumTerm = getTopArtists(mediumTerm);
        artistContainerLongTerm = getTopArtists(longTerm);
        
        // create tab page layouts
        trackTab = createTopTracks();
        artistTab = createTopArtists();
 
        // add tabs to tabsheet
        tabsheet.add("Top Tracks", trackTab);
        tabsheet.add("Top Artists", artistTab);
        add(tabsheet);
        
        tabsheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
    }

// ----------------------------------------- Get data -----------------------------------------

    // load top tracks into ordered lists
    private OrderedList getTopTracks(String term) {
        // get user's top tracks
        Track[] userTracks = SpotifyService.getTopTracks(term);

        // add tracks to container
        int i = 0;
        OrderedList trackContainer = new OrderedList();

        for(Track track : userTracks) 
            trackContainer.add(new TrackCard(track, i++));
        
        return trackContainer;
    }

    // load top artists into ordered lists
    private OrderedList getTopArtists(String term) {
        // get user's top artists
        Artist[] userArtists = SpotifyService.getTopArtists(term);

        // add artists to container
        OrderedList artistContainer = new OrderedList();

        for(Artist artist : userArtists) 
            artistContainer.add(new ArtistCard(artist));

        return artistContainer;
    }

// ----------------------------------------- Tabsheet -----------------------------------------

    // create top tracks tab
    private Div createTopTracks() {
        Div div = new Div();
        addClassNames("track-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Your Top Tracks");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        headerContainer.add(header);

        Select<String> sortBy = new Select<>();
        sortBy.setItems("Last four weeks", "Last six months", "All data");
        sortBy.setValue("Last four weeks");

        Div trackDiv = new Div();
        trackDiv.add(trackContainerShortTerm);
        container.add(headerContainer, sortBy);
        div.add(container, trackDiv);

        // update tracks when user selects time frame from select menu
        sortBy.addValueChangeListener(e -> {
            if (sortBy.getValue() == "Last four weeks"){
                trackDiv.removeAll();
                trackDiv.add(trackContainerShortTerm);            
            }
            if (sortBy.getValue() == "Last six months") {
                trackDiv.removeAll();
                trackDiv.add(trackContainerMediumTerm);
            }
            if (sortBy.getValue() == "All data") {
                trackDiv.removeAll();
                trackDiv.add(trackContainerLongTerm);
            }
        });

        return div;
    }

    // create top artists tab
    private Div createTopArtists() {
        addClassNames("artist-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        Div div = new Div();
        
        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Your Top Artists");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        headerContainer.add(header);

        Select<String> sortBy = new Select<>();
        sortBy.setItems("Last four weeks", "Last six months", "All data");
        sortBy.setValue("Last four weeks");

        OrderedList artistContainer = new OrderedList();
        artistContainer = getTopArtists(longTerm);
        artistContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE, JustifyContent.BETWEEN, Column.COLUMNS_5);

        artistContainerShortTerm.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE, JustifyContent.BETWEEN, Column.COLUMNS_5);
        artistContainerMediumTerm.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE, JustifyContent.BETWEEN, Column.COLUMNS_5);
        artistContainerLongTerm.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE, JustifyContent.BETWEEN, Column.COLUMNS_5);

        container.add(headerContainer, sortBy);
        Div artistDiv = new Div();
        artistDiv.add(artistContainerShortTerm);
        div.add(container, artistDiv);

        // update artists when user selects time frame from select menu
        sortBy.addValueChangeListener(e -> {
            if (sortBy.getValue() == "Last four weeks"){
                artistDiv.removeAll();
                artistDiv.add(artistContainerShortTerm);            
            }
            if (sortBy.getValue() == "Last six months") {
                artistDiv.removeAll();
                artistDiv.add(artistContainerMediumTerm);
            }
            if (sortBy.getValue() == "All data") {
                artistDiv.removeAll();
                artistDiv.add(artistContainerLongTerm);
            }
        });

        return div;
    }

    // create top genres tab
    private Div createTopGenres() {
        addClassNames("genre-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
        
        Div div = new Div();
        
        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Your Top Genres");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        headerContainer.add(header);
        
        genreContainer = new OrderedList();
        div.add(headerContainer, genreContainer);
        return div;
    }
}