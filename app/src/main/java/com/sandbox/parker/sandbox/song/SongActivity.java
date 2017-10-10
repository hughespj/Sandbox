package com.sandbox.parker.sandbox.song;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sandbox.parker.sandbox.R;
import com.sandbox.parker.sandboxapi.dto.Song;
import com.sandbox.parker.sandboxapi.http.HTTPRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by parker on 10/10/17.
 */

public class SongActivity extends AppCompatActivity {
    private Song song;
    private ImageView collectionImage;
    private TextView artistName;
    private TextView collectionName;
    private TextView trackName;
    private TextView lyricBody;

    public SongActivity(Song song){
        this.song = song;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_fragment_layout);

        collectionImage = (ImageView) findViewById(R.id.collection_image);
        artistName = (TextView) findViewById(R.id.song_artist);
        collectionName = (TextView) findViewById(R.id.song_collection);
        trackName = (TextView) findViewById(R.id.song_track);
        lyricBody = (TextView) findViewById(R.id.lyric_body);

        artistName.setText(song.getArtistName());
        collectionName.setText(song.getCollectionName());
        trackName.setText(song.getTrackName());

        Picasso.with(getBaseContext()).load(
                song.getArtworkUrl100())
                .placeholder(R.mipmap.ic_launcher)
                .into(collectionImage);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {

                HTTPRequest request = new HTTPRequest("https://api.lyrics.ovh/v1/");
                Map<String, String> params = new HashMap<>();
                params.put("artist", song.getArtistName());
                params.put("title", song.getTrackName());
                return request.get(params.get(0) + "/" + params.get(1));

            }

            @Override
            protected void onPostExecute(String result) {
                lyricBody.setText(result);
            }


        }.execute();
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
