package com.sandbox.parker.sandbox.song;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.sandbox.parker.sandbox.R;
import com.sandbox.parker.sandboxapi.dto.Song;
import com.sandbox.parker.sandboxapi.helper.JSONHelper;
import com.sandbox.parker.sandboxapi.http.HTTPRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by parker on 10/10/17.
 */

public class SongActivity extends AppCompatActivity {
    private ImageView collectionImage;
    private TextView artistName;
    private TextView collectionName;
    private TextView trackName;
    private TextView lyricBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_fragment_layout);

        Bundle bundle = getIntent().getExtras();
        final Song song = (Song) bundle.get("song");

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

                String artistParam = params.get("artist").replace(" ", "_");
                String titleParam = params.get("title").replace(" ", "_");
                String testURL = ("https://api.lyrics.ovh/v1/" + artistParam + "/" + titleParam);

                return request.get(artistParam + "/" + titleParam);


            }

            @Override
            protected void onPostExecute(String result) {
                String formattedResult = JSONHelper.readLyricStreamAsJSON(result);
                lyricBody.setText(formattedResult);
            }


        }.execute();
    }

}
