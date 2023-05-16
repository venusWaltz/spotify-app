package com.bebopt.app.data.controller;

import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;


@Service
public class SpotifyService {

    public static User getCurrentUser() {
        return AuthController.getProfile();
    }
    public static void logout() {
        RedirectController.redirect("logout");
    }

// ---------------------------------------- Tracks ----------------------------------------

    public static Track[] getTopTracks(String timeRange) {
        return AuthController.getTopTracks(timeRange);
    }
    public static Track getTrackById(String id) {
        return AuthController.getTrackById(id);
    }
    public static CurrentlyPlaying getCurrentlyPlayingItem() {
        return AuthController.getCurrentlyPlaying();
    }
    public static PagingCursorbased<PlayHistory> getRecentlyPlayedTracks() {
        return AuthController.getRecentlyPlayedTracks();
    }

// ---------------------------------------- Artists ----------------------------------------

    public static Artist[] getTopArtists(String timeRange) {
        return AuthController.getTopArtists(timeRange);
    }

// ---------------------------------------- Albums ----------------------------------------

    public static Album getAlbumById(String id) {
        return AuthController.getAlbumById(id);
    }

// ---------------------------------------- Playlists ----------------------------------------

    public static PlaylistSimplified[] getPlaylists() {
        return AuthController.getPlaylists();
    }
    public static Playlist getPlaylistById(String id) {
        return AuthController.getPlaylistById(id);
    }
    public static AudioFeatures[] getAudioFeatures(String tracks) {
        return AuthController.getAudioFeatures(tracks);
    }
    public static Playlist createPlaylist() {
        return AuthController.createPlaylist();
    }
    public static SnapshotResult addToPlaylist(String id, String[] uris) {
        return AuthController.addToPlaylist(id, uris);
    }
    public static SnapshotResult modifyPlaylist(String id) {
        return AuthController.modifyPlaylist(id);
    }


// ---------------------------------------- Recommendations ----------------------------------------

    public static Recommendations getRecommendations(String seed) {
        return AuthController.getRecommendations(seed);
    }
}