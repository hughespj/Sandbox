package com.sandbox.parker.sandboxapi.dto;

import java.util.List;

/**
 * Created by parker on 10/6/17.
 */

public class SongCollection {
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
