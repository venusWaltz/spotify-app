package com.bebopt.app.objects;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * The {@code TrackCard} class represents a UI component to display track
 * information.
 */
public class TrackCard extends HorizontalLayout {

    private Track track;
    private int index;

    /**
     * Constructor for the {@code TrackCard} class.
     * Initializes the UI component for a track card.
     * 
     * @param track The track object containing the track's information.
     * @param index The index of the track in the full list of tracks.
     */
    public TrackCard(Track track, int index) {
        this.track = track;
        this.index = index;
        initializeCard();
    }

    /**
     * Initializes the UI component for a track card.
     */
    private void initializeCard() {
        addClassNames("card-bar");
        Div div = new Div();

        Image image = new Image();
        image.setSrc(track.getAlbum().getImages()[0].getUrl());

        Span num = new Span(String.valueOf(index + 1));
        num.addClassNames("num");
        Span title = new Span(track.getName());
        title.addClassName("title");
        Span artist = new Span(track.getArtists()[0].getName());
        artist.addClassName("artist-name");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addClassName("h-layout");
        horizontalLayout.setSpacing(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassNames("v-layout");
        verticalLayout.setSpacing(false);

        add(num);
        verticalLayout.add(title, artist);
        horizontalLayout.add(image, verticalLayout);
        div.add(horizontalLayout);
        add(div);
    }
}
