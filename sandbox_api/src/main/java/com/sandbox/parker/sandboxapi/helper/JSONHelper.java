package com.sandbox.parker.sandboxapi.helper;

import android.os.Parcel;

import com.sandbox.parker.sandboxapi.dto.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parker on 10/6/17.
 */

public class JSONHelper {

    public static String readLyricStreamAsJSON(String jsonData) {


        String lyrics = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            lyrics = jsonObject.getString("lyrics");


        } catch (JSONException e) {
            lyrics = e.getMessage();
        }
        return lyrics;

    }

    public static ArrayList<Song> readSongStreamAsJSON(String jsonData) {

        ArrayList<Song> songs = new ArrayList<>();

        try {

            jsonData = jsonData.replace("\n", "");

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray songList = jsonObject.getJSONArray("results");

            Song song;

            for (int i = 0; i < songList.length(); i ++){

                JSONObject jsonSongObj = songList.getJSONObject(i);

                song = new Song();

                song.setWrapperType(jsonSongObj.getString("wrapperType"));
                song.setKind(jsonSongObj.getString("kind"));

                song.setArtistId(jsonSongObj.getLong("artistId"));
                song.setCollectionId(jsonSongObj.getLong("collectionId"));
                song.setTrackId(jsonSongObj.getLong("trackId"));

                song.setArtistName(jsonSongObj.getString("artistName"));
                song.setCollectionName(jsonSongObj.getString("collectionName"));
                song.setTrackName(jsonSongObj.getString("trackName"));
                song.setCollectionCensoredName(jsonSongObj.getString("collectionCensoredName"));
                song.setTrackCensoredName(jsonSongObj.getString("trackCensoredName"));
                song.setArtistViewUrl(jsonSongObj.getString("artistViewUrl"));
                song.setCollectionViewUrl(jsonSongObj.getString("collectionViewUrl"));
                song.setTrackViewUrl(jsonSongObj.getString("trackViewUrl"));

                song.setPreviewUrl(jsonSongObj.getString("previewUrl"));

                song.setArtworkUrl30(jsonSongObj.getString("artworkUrl30"));
                song.setArtworkUrl60(jsonSongObj.getString("artworkUrl60"));
                song.setArtworkUrl100(jsonSongObj.getString("artworkUrl100"));

                song.setCollectionPrice(jsonSongObj.getDouble("collectionPrice"));
                song.setTrackPrice(jsonSongObj.getDouble("trackPrice"));

                song.setReleaseDate(jsonSongObj.getString("releaseDate"));
                song.setCollectionExplicitness(jsonSongObj.getString("collectionExplicitness"));
                song.setTrackExplicitness(jsonSongObj.getString("trackExplicitness"));

                song.setDiscCount(jsonSongObj.getInt("discCount"));
                song.setDiscNumber(jsonSongObj.getInt("discNumber"));
                song.setTrackCount(jsonSongObj.getInt("trackCount"));
                song.setTrackNumber(jsonSongObj.getInt("trackNumber"));

                song.setTrackTimeMillis(jsonSongObj.getLong("trackTimeMillis"));

                song.setCountry(jsonSongObj.getString("country"));
                song.setCurrency(jsonSongObj.getString("currency"));
                song.setPrimaryGenreName(jsonSongObj.getString("primaryGenreName"));
                song.setStreamable(jsonSongObj.getBoolean("isStreamable"));

                songs.add(song);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return songs;
    }

}
