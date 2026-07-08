package com.bebopt.app.views;

import java.util.Arrays;
import java.util.List;
import com.bebopt.app.api.PlaylistManager;
import com.bebopt.app.api.SpotifyService;
import com.bebopt.app.objects.PlaylistCard;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

/**
 * The {@code PlaylistsView} class represents the view for playlist management.
 * 
 * This class interacts with the {@code PlaylistActions} class.
 */
@AnonymousAllowed
@PageTitle("Playlists")
@Route(value = "playlists", layout = MainLayout.class)
public class PlaylistsView extends Main {

    private static PlaylistCard playlistCard;
    private OrderedList playlistsContainer;
    private static TabSheet tabsheet;
    public static Dialog optionsDialog;

    private static RadioButtonGroup<String> sortGroup;
    private static RadioButtonGroup<String> filterGroup;
    private static RadioButtonGroup<String> decadesGroup;
    private static RadioButtonGroup<String> genresGroup;
    private static ListBox<PlaylistSimplified> mergeListBox;

    private static String selectedSort;
    private static String selectedFilter;
    private static Integer selectedDecade;
    private static String selectedGenre;
    private static Div decadesContainer;
    private static Div genresContainer;
    private static String selectedMergePlaylistId;
    private static String selectedMergePlaylistName;
    private static List<String> sortItems = Arrays.asList("Release date", "Duration", "Popularity", "Acousticness",
            "Danceability", "Energy", "Instrumentalness", "Loudness", "Speechiness", "Tempo", "Valence");
    private static List<String> filterItems = Arrays.asList("Release decade", "Genre");

    /**
     * Constructor for the {@code PlaylistsView} class.
     * Initializes the UI and loads Spotify playlists.
     * 
     * @throws Exception If an error occurs during Spotify API interaction.
     */
    public PlaylistsView() throws Exception {
        playlistsContainer = SpotifyService.loadPlaylists();
        constructUI();
    }

    /**
     * Constructs the user interface for the "Playlists" view.
     */
    private void constructUI() {
        addClassNames("playlists-view", "page-view");

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames("header-container");

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Your Playlists");
        Paragraph description = new Paragraph("Select a playlist to view more options");
        headerContainer.add(header, description);

        optionsDialog = createDialog(); /*
                                         * Playlist action dialog opens when the user
                                         * // selects a playlist.
                                         */
        container.add(headerContainer);
        add(container, playlistsContainer, optionsDialog);
    }

    // -------------------------------------- Options dialog --------------------------------------

    /**
     * Creates a dialog window displaying the playlist options.
     * 
     * @return The playlist options dialog window.
     */
    public static Dialog createDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Playlist Options");
        dialog.setHeight("800px");
        dialog.setWidth("650px");
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(createTabsheet());

        Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);

        Button newPlaylist = new Button("New Playlist", e -> dialogChooseActionEvent());
        newPlaylist.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(newPlaylist);

        return dialog;
    }

    /**
     * Displays a dialog prompting the user to confirm their selected action.
     */
    public static void dialogChooseActionEvent() {
        Tab t = tabsheet.getSelectedTab();
        if (t == tabsheet.getTabAt(0)) {
            displayConfirmDialog("Sort", selectedSort);
        } else if (t == tabsheet.getTabAt(1)) {
            displayConfirmDialog("Filter", selectedFilter);
        } else if (t == tabsheet.getTabAt(2)) {
            displayConfirmDialog("Merge", selectedMergePlaylistId);
        }
    }

    /**
     * Saves the selected playlist and displays a dialog for other options.
     * 
     * @param playlist The {@code PlaylistCard} object of the playlist selected by
     *                 the user.
     */
    public static void onPlaylistSelect(PlaylistCard playlist) {
        resetMergePlaylistSelection();
        playlistCard = playlist;
        optionsDialog.setHeaderTitle(playlistCard.getName());
        optionsDialog.open();
    }

    /**
     * Creates the TabSheet component with tabs for sorting, filtering, and merging
     * playlists.
     * 
     * @return The TabSheet component.
     */
    private static TabSheet createTabsheet() {
        tabsheet = new TabSheet();
        VerticalLayout sortTab = createSortTab();
        HorizontalLayout filterTab = createFilterTab();
        VerticalLayout mergeTab = createMergeTab();

        tabsheet.add("Sort", sortTab);
        tabsheet.add("Filter", filterTab);
        tabsheet.add("Merge", mergeTab);

        tabsheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        return tabsheet;
    }

    /**
     * Creates the tab for sorting playlists.
     * 
     * @return The "Sort" tab.
     */
    private static VerticalLayout createSortTab() {
        VerticalLayout tab = new VerticalLayout();
        sortGroup = createRadioGroup("Sort by: ", null, sortItems);
        sortGroup.setValue(sortItems.get(0));
        sortGroup.addValueChangeListener(e -> selectedSort = e.getValue());
        tab.add(sortGroup);
        return tab;
    }

    /**
     * Creates the tab for filtering playlists.
     * 
     * @return The "Filter" tab.
     */
    private static HorizontalLayout createFilterTab() {
        HorizontalLayout tab = new HorizontalLayout();
        VerticalLayout left = new VerticalLayout();
        VerticalLayout right = new VerticalLayout();

        filterGroup = createRadioGroup("Filter by:", null, filterItems);

        decadesGroup = createRadioGroup("Decades:", null, null);
        decadesGroup.addValueChangeListener(event -> {
            selectedDecade = Integer.valueOf(event.getValue().substring(0, 4));
            selectedGenre = null;
        });

        genresGroup = createRadioGroup("Genres:", null, null);
        genresGroup.addValueChangeListener(event -> {
            selectedGenre = event.getValue();
            selectedDecade = null;
        });

        decadesContainer = new Div();
        decadesContainer.setVisible(false);
        decadesContainer.add(decadesGroup);

        genresContainer = new Div();
        genresContainer.setVisible(false);
        genresContainer.add(genresGroup);

        addFilterValueChangeListener(filterGroup);
        left.add(filterGroup);
        right.add(decadesContainer, genresContainer);
        tab.add(left, right);
        return tab;
    }

    /**
     * Creates the tab for merging playlists.
     * 
     * @return The "Merge" tab.
     */
    private static VerticalLayout createMergeTab() {
        VerticalLayout tab = new VerticalLayout();
        Label label = new Label("Select a second playlist: ");
        List<PlaylistSimplified> items = SpotifyService.getPlaylistsAsList();
        mergeListBox = createMergeListBox(items);
        tab.add(label, mergeListBox);
        return tab;
    }

    /**
     * Creates a radio button group with the given label, class name, and/or items.
     * 
     * @param label     The label for the radio button group.
     * @param className The class name for the radio button group.
     * @param items     The items for the radio button group options.
     * @return The radio button group component.
     */
    private static RadioButtonGroup<String> createRadioGroup(String label, String className, List<String> items) {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        if (label != null)
            group.setLabel(label);
        if (className != null)
            group.addClassNames(className);
        if (items != null)
            group.setItems(items);
        return group;
    }

    /**
     * Adds a value change listener to the filter radio group. The listener updates
     * the selected
     * filter and manages the visibility and contents of the filter containers.
     * 
     * @param group The radio group to add the value change listener to.
     * @return The {@code Registration} object representing the registration of the
     *         value change listener.
     */
    private static Registration addFilterValueChangeListener(RadioButtonGroup<String> group) {
        return group.addValueChangeListener(e -> {
            selectedFilter = e.getValue();

            if (selectedFilter != null) {
                if (selectedFilter.equals("Release decade")) {
                    genresContainer.setVisible(false);
                    decadesContainer.setVisible(true);
                    if (playlistCard.isDecadeMapNull() == true)
                        PlaylistManager.createDecadeMap(playlistCard);
                    decadesGroup.setItems(playlistCard.getDecades());
                    decadesContainer.add(decadesGroup);
                } else if (selectedFilter.equals("Genre")) {
                    decadesContainer.setVisible(false);
                    genresContainer.setVisible(true);
                    if (playlistCard.isGenreMapNull() == true)
                        PlaylistManager.createGenreMap(playlistCard);
                    genresGroup.setItems(playlistCard.getGenres());
                    genresContainer.add(genresGroup);
                }
            } else {
                decadesContainer.setVisible(false);
                genresContainer.setVisible(false);
            }
        });
    }

    /**
     * Creates and configures a list box for displaying and selecting playlists,
     * sets a
     * custom renderer to display each playlist with its image and title, and adds a
     * value
     * change listener to update the selected playlist ID when a new playlist is
     * selected.
     * 
     * @param items A list of {@code PlaylistSimplified} objects representing the
     *              user's playlists.
     * @return The ListBox of playlists.
     */
    private static ListBox<PlaylistSimplified> createMergeListBox(List<PlaylistSimplified> items) {
        ListBox<PlaylistSimplified> listBox = new ListBox<>();
        listBox.addClassName("full-width");
        listBox.setItems(items);

        listBox.setRenderer(new ComponentRenderer<>(item -> {
            HorizontalLayout row = new HorizontalLayout();
            Avatar avatar = new Avatar();
            avatar.setImage(item.getImages().length != 0 ? item.getImages()[0].getUrl() : "images/empty-plant.png");
            Span name = new Span(item.getName());
            row.addClassName("align-center");
            row.add(avatar, name);
            return row;
        }));

        listBox.addValueChangeListener(e -> {
            PlaylistSimplified listBoxPlaylist = listBox.getValue();
            if (listBoxPlaylist != null) {
                selectedMergePlaylistId = listBox.getValue().getId();
                selectedMergePlaylistName = listBox.getValue().getName();
            }
        });

        return listBox;
    }

    // -------------------------------------- Confirm dialog --------------------------------------

    /**
     * Creates a confirmation dialog.
     * 
     * @param action         The primary action chosen ("Sort", "Filter", or
     *                       "Merge").
     * @param selectedAction The secondary action chosen based on the primary
     *                       action.
     */
    private static void displayConfirmDialog(String action, String selectedAction) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.addClassName("confirm-dialog");
        confirmDialog.setHeader("Confirm");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText(action);
        confirmDialog.addConfirmListener(e -> onConfirm(action, selectedAction));

        String dialogText = getDialogText(action, selectedAction);
        if (dialogText != null) {
            confirmDialog.setText(getDialogText(action, selectedAction));
            confirmDialog.open();
        } else {
            displayErrorDialog();
        }
    }

    /**
     * Handles the specified action.
     * 
     * @param action         The primary action chosen ("Sort", "Filter", or
     *                       "Merge").
     * @param selectedAction The secondary action chosen based on the primary
     *                       action.
     */
    private static void onConfirm(String action, String selectedAction) {
        switch (action) {
            case "Sort":
                PlaylistManager.sortPlaylist(playlistCard, selectedSort);
                break;
            case "Filter":
                if ("Release decade".equals(selectedFilter) && selectedDecade != null)
                    PlaylistManager.filterPlaylist(playlistCard, selectedDecade);
                else if ("Genre".equals(selectedFilter) && selectedGenre != null)
                    PlaylistManager.filterPlaylist(playlistCard, selectedGenre);
                break;
            case "Merge":
                PlaylistManager.mergePlaylists(playlistCard, selectedMergePlaylistId);
                break;
        }
    }

    /**
     * Constructs the text to be displayed in the confirmation dialog.
     * 
     * @param action         The primary action chosen ("Sort", "Filter", or
     *                       "Merge").
     * @param selectedAction The secondary action chosen based on the primary
     *                       action.
     * @return The dialog text to be displayed, or {@code null} if no valid text
     *         could be constructed.
     */
    private static String getDialogText(String action, String selectedAction) {
        if ("Sort".equals(action) && selectedAction != null) {
            return "Sort playlist \"" + playlistCard.getName() + "\" by " + selectedAction.toLowerCase() + "?";
        }
        if ("Filter".equals(action) && "Release decade".equals(selectedFilter) && selectedDecade != null) {
            return "Filter playlist \"" + playlistCard.getName() + "\" by release decade (" + selectedDecade + ")?";
        }
        if ("Filter".equals(action) && "Genre".equals(selectedFilter) && selectedGenre != null) {
            return "Filter playlist \"" + playlistCard.getName() + "\" by genre (" + selectedGenre + ")?";
        }
        if ("Merge".equals(action) && selectedMergePlaylistId != null) {
            return "Merge playlists\n\"" + playlistCard.getName() + "\" and \"" + selectedMergePlaylistName + "\"?";
        }
        return null;
    }

    /**
     * Reset the values of the second playlist selected under the "Merge" tab when
     * the primary playlist is deselected and a new primary playlist is selected.
     */
    public static void resetMergePlaylistSelection() {
        sortGroup.setValue(null);
        filterGroup.setValue(null);
        mergeListBox.setValue(null);
        decadesContainer.setVisible(false);
        genresContainer.setVisible(false);
        decadesGroup.clear();
        selectedDecade = null;
        selectedSort = selectedFilter = null;
        selectedMergePlaylistId = selectedMergePlaylistName = null;
    }

    // --------------------------------------- Error dialog ---------------------------------------

    /**
     * Display an error dialog when the user needs to select a second playlist.
     */
    private static void displayErrorDialog() {
        ConfirmDialog errorDialog = new ConfirmDialog();
        errorDialog.setWidth("550px");
        errorDialog.setHeader("Error");
        errorDialog.setText("Please make a selection.");
        errorDialog.setConfirmText("Continue");
        errorDialog.open();
    }
}
