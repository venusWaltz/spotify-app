package com.bebopt.app.objects;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import se.michaelthelin.spotify.model_objects.specification.Artist;

/**
 * The {@code ArtistCard} class represents a UI component to display artist 
 * information.
 */
public class ArtistCard extends ListItem {

    private Artist artist;

    /**
     * Constructor for the {@code ArtistCard} class.
     * 
     * @param artist The artist object containing the artist information.
     */
    public ArtistCard(Artist artist) {
        this.artist = artist;
        initializeCard();
    }

    /**
     * Initializes the UI component for an artist card.
     */
    private void initializeCard() {
        addClassNames("card-square", "artist");
        Div div = new Div();
        div.addClassNames("card-container");

        Image image = new Image();
        if (artist.getImages()[0].getHeight() < artist.getImages()[0].getWidth()) {
            image.setHeight("100%");
        } else {
            image.setWidth("100%");
        }
        image.setSrc(artist.getImages()[0].getUrl());

        Span header = new Span();
        header.addClassNames("artist-name");
        header.setText(artist.getName());

        div.add(image);
        add(div, header);
    }
}
