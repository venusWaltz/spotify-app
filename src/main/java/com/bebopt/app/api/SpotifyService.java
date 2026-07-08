package com.bebopt.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bebopt.app.objects.ArtistCard;
import com.bebopt.app.objects.PlaylistCard;
import com.bebopt.app.objects.TimeRange;
import com.bebopt.app.objects.TrackCard;
import com.vaadin.flow.component.html.OrderedList;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

/**
 * Provides methods to interact with and manage data retrieved from the Spotify API.
 */
public class SpotifyService {

    @FunctionalInterface
    private interface DataFetcher<T> {
        /**
         * Fetches data from the Spotify API.
         * 
         * @param timeRange The time range for which to fetch data.
         * @param limit     The number of items to fetch.
         * @param offset    The index of the first entry to return.
         * @return
         * @throws Exception
         */
        T[] fetch(TimeRange timeRange, int limit, int offset) throws Exception;
    }

    /**
     * Fetches data with retry logic in case there are too few top items available
     * to retrieve.
     * 
     * @param <T>         The type of data to fetch.
     * @param timeRange   The time range for the data being fetched.
     * @param dataFetcher The method reference.
     * @return The list of fetched data.
     */
    private static <T> List<T> fetchDataWithRetry(TimeRange timeRange, DataFetcher<T> dataFetcher) {
        List<T> dataList = new ArrayList<>();
        int offset = 0;
        try {
            dataList = Arrays.asList(dataFetcher.fetch(timeRange, SpotifyApiClient.MAX_LIMIT, 0));
        } catch (Exception e) {
            System.out.println("Insufficient data; too few results. " + e.getMessage());
            while (offset <= SpotifyApiClient.MAX_LIMIT - SpotifyApiClient.INCREMENT) {
                try {
                    dataList.addAll(Arrays.asList(dataFetcher.fetch(timeRange, SpotifyApiClient.INCREMENT, offset)));
                    offset += SpotifyApiClient.INCREMENT;
                } catch (Exception f) {
                    System.out.println(f.getMessage());
                }
            }
        }
        return dataList;
    }

    /**
     * Loads top tracks into an ordered list based on the specified time range.
     * 
     * @param timeRange The time range for top tracks (short, medium, long).
     * @return The OrderedList of top tracks.
     */
    public static OrderedList getTopTracks(TimeRange timeRange) {
        List<Track> userTracks = fetchDataWithRetry(timeRange, SpotifyApiClient::getTopTracks);
        return createTracksOrderedList(userTracks);
    }

    /**
     * Loads top artists into an ordered list based on the specified time range.
     * 
     * @param timeRange The time range for top artists (short, medium, long).
     * @return The OrderedList of top artists.
     */
    public static OrderedList getTopArtists(TimeRange timeRange) {
        List<Artist> userArtists = fetchDataWithRetry(timeRange, SpotifyApiClient::getTopArtists);
        return createArtistsOrderedList(userArtists);
    }

    /**
     * Creates an OrderedList of {@code TrackCard} objects from a list of tracks.
     * 
     * @param tracks The list of tracks.
     * @return The OrderedList of {@code TrackCard} objects.
     */
    private static OrderedList createTracksOrderedList(List<Track> tracks) {
        OrderedList list = new OrderedList();
        for (int i = 0; i < tracks.size(); i++) {
            list.add(new TrackCard(tracks.get(i), i));
        }
        return list;
    }

    /**
     * Creates an OrderedList of {@code ArtistCard} objects from a list of artists.
     * 
     * @param tracks The list of artists.
     * @return The OrderedList of {@code ArtistCard} objects.
     */
    private static OrderedList createArtistsOrderedList(List<Artist> artists) {
        OrderedList list = new OrderedList();
        for (Artist artist : artists) {
            list.add(new ArtistCard(artist));
        }
        return list;
    }

    /**
     * ! Deprecated endpoint
     * Loads recommended tracks into an OrderedList.
     * 
     * @return The OrderedList containing recommended tracks.
     */
    public static OrderedList getRecommendedTracks() {
        OrderedList container = new OrderedList();
        List<String> ids = new ArrayList<>();
        int i = 0;

        for (TrackSimplified track : SpotifyApiClient.getRecommendedTracks(getTrackSeeds()).getTracks()) {
            ids.add(track.getId());
        }
        for (Track track : SpotifyApiClient.getSeveralTracks(String.join(",", ids))) {
            container.add(new TrackCard(track, i++));
        }
        return container;
    }

    /**
     * ! Deprecated endpoint
     * Loads related artists into an OrderedList.
     * 
     * @return The OrderedList containing related artists.
     */
    public static OrderedList getRelatedArtists() {
        String artistSeed = SpotifyApiClient.getTopArtists(TimeRange.SHORT_TERM, 1, 0)[0].getId();

        OrderedList orderedList = new OrderedList();
        for (Artist artist : SpotifyApiClient.getRelatedArtists(artistSeed)) {
            orderedList.add(new ArtistCard(artist));
        }
        return orderedList;
    }

    /**
     * Returns track seeds to use when getting recommendations.
     * 
     * @return A comma-separated list of track IDs.
     */
    public static String getTrackSeeds() {
        Track[] tracks = SpotifyApiClient.getTopTracks(TimeRange.SHORT_TERM, SpotifyApiClient.NUM_TRACKS, null);
        List<String> ids = new ArrayList<>();
        for (Track track : tracks) {
            ids.add(track.getId());
        }
        return String.join(",", ids);
    }

    /**
     * Loads Spotify playlists.
     *
     * @throws Exception If an error occurs during Spotify API interaction.
     */
    public static OrderedList loadPlaylists() throws Exception {
        OrderedList list = new OrderedList();
        PlaylistSimplified[] playlists = SpotifyApiClient.getPlaylists();
        for (PlaylistSimplified playlist : playlists) {
            list.add(new PlaylistCard(playlist));
        }
        return list;
    }

    /**
     * Returns the current user's Spotify playlists.
     * 
     * @return A list containing the user's playlists.
     */
    public static List<PlaylistSimplified> getPlaylistsAsList() {
        return Arrays.asList(SpotifyApiClient.getPlaylists());
    }
}
