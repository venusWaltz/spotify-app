package com.bebopt.app.objects;

import java.util.List;
import java.util.Map;

import com.bebopt.app.views.PlaylistsView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The {@code PlaylistsViewCard} class represents a UI component for displaying
 * a single playlist.
 * 
 * It includes a playlist image, playlist name, and context menu.
 */
public class PlaylistCard extends ListItem {

    private String spotifyId;
    private String name;
    private String imgSrc;
    private Map<Integer, List<Track>> decadeMap;
    private Map<String, List<Track>> genreMap;
    private List<String> decades;
    private List<String> genres;

    /**
     * Constructor for the {@code PlaylistsViewCard} class.
     * 
     * @param playlist A {@code PlaylistSimplified} object representing a Spotify
     *                 playlist.
     */
    public PlaylistCard(PlaylistSimplified playlist) {
        this.spotifyId = playlist.getId();
        name = playlist.getName();
        imgSrc = playlist.getImages().length != 0 ? playlist.getImages()[0].getUrl()
                : "images/empty-plant.png";
        initializeCard();
    }

    /**
     * Initializes the UI component for a playlist card.
     */
    private void initializeCard() {
        addClassNames("card-square", "playlist");
        Div div = new Div();

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(imgSrc);

        Span header = new Span();
        header.addClassNames("playlist-name");
        header.setText(name);

        this.addClickListener(e -> PlaylistsView.onPlaylistSelect(this));
        div.add(image);
        add(div, header);
    }

    /**
     * Gets the Spotify ID of the playlist.
     *
     * @return The Spotify ID of the playlist.
     */
    public String getSpotifyId() {
        return spotifyId;
    }

    /**
     * Gets the name of the playlist.
     *
     * @return The name of the playlist.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the decade map is empty.
     *
     * @return {@code true} if the decade map is null, otherwise {@code false}.
     */
    public boolean isDecadeMapNull() {
        return decadeMap == null;
    }

    /**
     * Gets the map of tracks grouped by their release decade.
     *
     * @return A map where the key is the decade and the value is a list of tracks.
     */
    public Map<Integer, List<Track>> getDecadeMap() {
        return decadeMap;
    }

    /**
     * Sets the map of tracks grouped by their release decade.
     *
     * @param decadeMap A map where the key is the decade and the value is a list of
     *                  tracks.
     */
    public void setDecadeMap(Map<Integer, List<Track>> decadeMap) {
        this.decadeMap = decadeMap;
    }

    /**
     * Checks if the genre map is empty.
     *
     * @return {@code true} if the genre map is null, otherwise {@code false}.
     */
    public boolean isGenreMapNull() {
        return genreMap == null;
    }

    /**
     * Gets the map of tracks grouped by their genre.
     *
     * @return A map where the key is the genre and the value is a list of tracks.
     */
    public Map<String, List<Track>> getGenreMap() {
        return genreMap;
    }

    /**
     * Sets the map of tracks grouped by their genre.
     *
     * @param genreMap A map where the key is the genre and the value is a list of
     *                 tracks.
     */
    public void setGenreMap(Map<String, List<Track>> genreMap) {
        this.genreMap = genreMap;
    }

    /**
     * Gets the list of decades present in the playlist.
     *
     * @return A list of strings representing the decades.
     */
    public List<String> getDecades() {
        return decades;
    }

    /**
     * Sets the list of decades present in the playlist.
     *
     * @param decades A list of strings representing the decades.
     */
    public void setDecades(List<String> decades) {
        this.decades = decades;
    }

    /**
     * Gets the list of genres present in the playlist.
     *
     * @return A list of strings representing the genres.
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * Sets the list of genres present in the playlist.
     *
     * @param genres A list of strings representing the genres.
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
