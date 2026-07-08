package com.bebopt.app.api;

import java.io.IOException;
import java.net.URI;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bebopt.app.objects.TimeRange;
import com.bebopt.app.routing.Router;
import com.bebopt.app.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;

/**
 * The {@code AuthController} class manages Spotify API interactions and user 
 * authorization.
 */
@RestController
@RequestMapping("")
public class SpotifyApiClient {

    private static User user;

    // ! Insecure URI no longer allowed, updated
    private static String URIPrefix = "http://127.0.0.1:8080";
    private static final URI redirectUri = SpotifyHttpManager.makeUri(String.join("/", URIPrefix, "callback"));

    public static SpotifyApi spotifyApi = createSpotifyAPI();
    private static String scopes = "user-read-private,user-top-read,playlist-modify-public,playlist-read-private,playlist-modify-private";
    private String code = "";

    public static final int MAX_LIMIT = 50;
    public static final int DEFAULT_LIMIT = 20;
    public static final int INCREMENT = 5;
    public static final int NUM_TRACKS = 5;

    // ---------------------------------------- Authorization -----------------------------------------

    /**
     * Creates the base object used to send requests to the Spotify API.
     * 
     * @return The SpotifyApi object.
     */
    public static SpotifyApi createSpotifyAPI() {
        return new SpotifyApi.Builder()
                .setClientId(System.getenv("CLIENT_ID"))
                .setClientSecret(System.getenv("CLIENT_SECRET"))
                .setRedirectUri(redirectUri)
                .build();
    }

    /**
     * Retrieves the Spotify login URI for user authorization.
     * 
     * @return The Spotify login URI.
     */
    @GetMapping("spotify-url")
    @ResponseBody
    public static String getSpotifyLoginUri() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
                .authorizationCodeUri()
                .scope(scopes)
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    /**
     * Handles user authorization callback from Spotify by requesting a temporary
     * access token.
     * 
     * @param code     The authorization code provided by Spotify.
     * @param response The {@link HttpServletResponse} used to redirect the user to
     *                 the home page.
     * @return         The access token obtained from Spotify, which can be used for
     *                 subsequent API requests.
     * @throws IOException If an I/O error occurs.
     */
    @GetMapping("callback")
    public String getUserCode(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        this.code = code;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(this.code).build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error" + e.getMessage());
        }
        user = getUserProfile();
        AuthenticatedUser.login();
        response.sendRedirect(URIPrefix);
        return spotifyApi.getAccessToken();
    }

    /**
     * Refreshes the access token at expiry.
     */
    public static void refreshAccessToken() {
        try {
            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                    .refresh_token(spotifyApi.getRefreshToken())
                    .build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Logs the current user out by revoking their access tokens and clearing their
     * session data.
     * 
     * @param response     The HTTP servlet response.
     * @throws IOException If an I/O error occurs.
     */
    @GetMapping("logout")
    public static void logout(HttpServletResponse response) throws IOException {
        try {
            spotifyApi.authorizationCodeRefresh(System.getenv("CLIENT_ID"), System.getenv("CLIENT_SECRET"), spotifyApi.getRefreshToken()).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        Router.redirect("home");
        spotifyApi.setAccessToken(null);
        spotifyApi.setRefreshToken(null);
        AuthenticatedUser.setIsLoggedIn(false);
    }

    // --------------------------------------------- User ---------------------------------------------

    /**
     * Retrieves the locally cached profile information of the current authenticated
     * Spotify user.
     *
     * @return The cached Spotify {@code User} object.
     */
    public static User getUser() {
        return user;
    }

    /**
     * Retrieves the profile information of the current authenticated Spotify user
     * from the Spotify API.
     * 
     * @return The Spotify {@code User} profile.
     */
    @GetMapping("user-profile")
    private static User getUserProfile() {
        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
        try {
            final User user = getCurrentUsersProfileRequest.execute();
            return user;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------- Tracks --------------------------------------------

    /**
     * Retrieves the top tracks of the current authenticated Spotify user.
     * 
     * @param timeRange The time range for which to retrieve top tracks.
     * @param limit     The number of tracks to retrieve.
     * @param offset    The index of the first entry to return.
     * @return          The array of {@code Track} objects representing the top tracks.
     */
    @GetMapping("user-top-tracks/{timeRange}")
    public static Track[] getTopTracks(TimeRange timeRange, Integer limit, Integer offset) {
        final GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi
                .getUsersTopTracks()
                .time_range(timeRange.getTimeRange())
                .limit(limit != null ? limit : MAX_LIMIT)
                .offset(offset != null ? offset : 0)
                .build();
        try {
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new Track[0];
    }

    /**
     * Retrieves a specific track from the Spotify API by its ID.
     * 
     * @param id The track ID.
     * @return   The {@code Track} object representing the requested track.
     */
    @GetMapping("get-track-by-id/{id}")
    public static Track getTrackById(String id) {
        final GetTrackRequest getTrackRequest = spotifyApi
                .getTrack(id)
                .build();
        try {
            final Track track = getTrackRequest.execute();
            return track;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves multiple tracks from the Spotify API by their IDs.
     * 
     * @param ids A comma-separated list of IDs.
     * @return    The array of {@code Track} objects representing the requested tracks.
     */
    @GetMapping("get-several-tracks")
    public static Track[] getSeveralTracks(String ids) {
        final GetSeveralTracksRequest getSeveralTracksRequest = spotifyApi
                .getSeveralTracks(ids)
                .build();
        try {
            final Track[] tracks = getSeveralTracksRequest.execute();
            return tracks;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the audio features of several tracks by their IDs.
     * 
     * @param tracks A comma-separated list of IDs.
     * @return       The array of {@code AudioFeatures} objects representing the 
     *               audio features of the tracks.
     */
    @GetMapping("audio-features")
    public static AudioFeatures[] getAudioFeatures(String tracks) {
        final GetAudioFeaturesForSeveralTracksRequest getAudioFeaturesForSeveralTracksRequest = spotifyApi
                .getAudioFeaturesForSeveralTracks(tracks)
                .build();
        try {
            final AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracksRequest.execute();
            return audioFeatures;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * ! Deprecated endpoint
     * Retrieves recommendations from the Spotify API by seed tracks.
     * 
     * @param seed A comma-separated list of track IDs.
     * @return     The {@code Recommendations} object containing recommended tracks.
     */
    @GetMapping("recommendations")
    public static Recommendations getRecommendedTracks(String seed) {
        final GetRecommendationsRequest getRecommendationsRequest = spotifyApi
                .getRecommendations()
                .limit(10)
                .seed_tracks(seed)
                .build();
        try {
            final Recommendations recommendations = getRecommendationsRequest.execute();
            return recommendations;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the recently played tracks of the current authenticated Spotify user.
     * 
     * @return A paging object containing the recently played tracks.
     */
    @GetMapping("recently-played")
    public static PagingCursorbased<PlayHistory> getRecentlyPlayedTracks() {
        final GetCurrentUsersRecentlyPlayedTracksRequest getCurrentUsersRecentlyPlayedTracksRequest = spotifyApi
                .getCurrentUsersRecentlyPlayedTracks()
                .build();
        try {
            final PagingCursorbased<PlayHistory> recentlyPlayedTracks = getCurrentUsersRecentlyPlayedTracksRequest
                    .execute();
            return recentlyPlayedTracks;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the currently playing track of the current authenticated Spotify
     * user.
     * 
     * @return The {@code CurrentlyPlaying} object representing the track being
     *         played.
     */
    @GetMapping("currently-playing")
    public static CurrentlyPlaying getCurrentlyPlaying() {
        final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
                .getUsersCurrentlyPlayingTrack()
                .build();
        try {
            final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
            return currentlyPlaying;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------- Albums --------------------------------------------

    /**
     * Retrieves a specified album from the Spotify API by its ID.
     * 
     * @param id The album ID.
     * @return   The {@code Album} object representing the requested album.
     */
    @GetMapping("get-album-by-id/{id}")
    public static Album getAlbumById(String id) {
        final GetAlbumRequest getAlbumRequest = spotifyApi
                .getAlbum(id)
                .build();
        try {
            final Album album = getAlbumRequest.execute();
            return album;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------- Artists --------------------------------------------

    /**
     * Retrieves a specified artist from the Spotify API by its ID.
     * 
     * @param id The artist ID.
     * @return   The {@code Artist} object representing the requested artist.
     */
    @GetMapping("get-artist-by-id/{id}")
    public static Artist getArtistById(String id) {
        final GetArtistRequest getArtistRequest = spotifyApi
                .getArtist(id)
                .build();
        try {
            final Artist artist = getArtistRequest.execute();
            return artist;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the top artists of the current authenticated Spotify user.
     * 
     * @param timeRange The time range for which to retrieve top artists.
     * @param limit     The number of tracks to retrieve.
     * @param offset    The index of the first entry to return.
     * @return          The array of {@code Artist} objects representing the top artists.
     */
    @GetMapping("user-top-artists/{timeRange}")
    public static Artist[] getTopArtists(TimeRange timeRange, Integer limit, Integer offset) {
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi
                .getUsersTopArtists()
                .time_range(timeRange.getTimeRange())
                .limit(limit != null ? limit : MAX_LIMIT)
                .offset(offset != null ? offset : 0)
                .build();
        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            return artistPaging.getItems();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new Artist[0];
    }

    /**
     * ! Deprecated endpoint
     * Retrieves releated artists for a specified artist from the Spotify API by its ID.
     * 
     * @param seed The artist ID.
     * @return     The array of {@code Artist} objects representing the related artists.
     */
    @GetMapping("related-artists")
    public static Artist[] getRelatedArtists(String seed) {
        final GetArtistsRelatedArtistsRequest getRelatedArtists = spotifyApi
                .getArtistsRelatedArtists(seed)
                .build();
        try {
            final Artist[] relatedArtists = getRelatedArtists.execute();
            return relatedArtists;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------ Playlists -------------------------------------------

    /**
     * Retrieves a specified playlist from the Spotify API by its ID.
     * 
     * @param id The playlist ID.
     * @return   The {@code Playlist} object representing the requested playlist.
     */
    @GetMapping("get-playlist-by-id/{id}")
    public static Playlist getPlaylistById(String id) {
        final GetPlaylistRequest getPlaylistRequest = spotifyApi
                .getPlaylist(id)
                .build();
        try {
            final Playlist playlist = getPlaylistRequest.execute();
            return playlist;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves playlists owned by the current authenticated Spotify user.
     * 
     * @return The array of {@code PlaylistSimplified} objects representing the
     *         user's playlists.
     */
    @GetMapping("user-playlists")
    public static PlaylistSimplified[] getPlaylists() {
        final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                .limit(12)
                .build();
        try {
            final Paging<PlaylistSimplified> playlistPaging = getListOfCurrentUsersPlaylistsRequest.execute();
            return playlistPaging.getItems();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new playlist for the current authenticated Spotify user.
     * 
     * @param name The name of the new playlist.
     * @return     The {@code Playlist} object representing the newly created playlist.
     */
    @PostMapping("create-playlist")
    public static Playlist createPlaylist(String name) {
        final CreatePlaylistRequest createPlaylistRequest = spotifyApi
                .createPlaylist(user.getId(), name)
                .build();
        try {
            final Playlist newPlaylist = createPlaylistRequest.execute();
            return newPlaylist;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds tracks to a specified playlist.
     * 
     * @param id   The playlist ID.
     * @param uris A comma-separated list of track IDs to be added to the playlist.
     * @return     The {@code SnapshotResult object} indicating the status of the
     *             operation.
     */
    @PostMapping("add-items-to-playlist")
    public static SnapshotResult addToPlaylist(String id, String[] uris) {
        final AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(id, uris) /* max length 50 for String[] uris, use JsonArray to add >50 tracks */
                .build();
        try {
            final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
            return snapshotResult;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Reorders tracks in a specified playlist.
     * 
     * @param id The ID of the playlist to be modified.
     * @return   The {@code SnapshotResult} object indicating the status of the
     *           operation.
     */
    @PutMapping("update-playlist-items")
    public static SnapshotResult modifyPlaylist(String id) {
        final ReorderPlaylistsItemsRequest reorderPlaylistsItemsRequest = spotifyApi
                .reorderPlaylistsItems(id, 0, 0)
                .build();
        try {
            final SnapshotResult snapshotResult = reorderPlaylistsItemsRequest.execute();
            return snapshotResult;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
