package com.sandbox.parker.sandbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.sandbox.parker.sandbox.song.SongActivity;
import com.sandbox.parker.sandboxapi.dto.Song;
import com.sandbox.parker.sandboxapi.helper.JSONHelper;
import com.sandbox.parker.sandboxapi.http.HTTPRequest;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SharedPreferences navPrefs;
    private IProfile savedProfile;
    private SearchView mSearchView;
    private TextView mTextView;
    private ListView mListView;
    private boolean isPlayAlreadyClicked = false;

    private long songPlayingId;
    private MediaPlayer mediaPlayer;

    private MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        songPlayingId = 0;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.time_view);
        mSearchView = (SearchView) findViewById(R.id.searchView2);
        mListView = (ListView) findViewById(R.id.listView);

        mToolbar.setTitle("Test title");
        setSupportActionBar(mToolbar);

        setMainActivity(this);



        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {

                        HTTPRequest request = new HTTPRequest("https://itunes.apple.com/");
                        Map<String, String> params = new HashMap<>();
                        params.put("term", s);
                        params.put("country", "US");
                        params.put("media", "music");
                        params.put("entity", "song");
                        params.put("attribute", "artistTerm");
                        return request.post("search", params);

                    }

                    @Override
                    protected void onPostExecute(String result) {

                        final ArrayList<Song> songs = JSONHelper.readSongStreamAsJSON(result);

                        mTextView.setText("Results: " + songs.size());

                        SongListAdapter adapter = new SongListAdapter(getApplicationContext(), songs);
                        mListView.setAdapter(adapter);
                    }


                }.execute();



                return true;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                return false;

            }

        });



        initializeNavDrawer();
    }

    private void initializeNavDrawer() {

        navPrefs = this.getSharedPreferences("com.sandbox.parker.sandbox", Context.MODE_PRIVATE);


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso
                        .with(imageView.getContext())
                        .load(uri)
                        .placeholder(placeholder)
                        .into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso
                        .with(imageView.getContext())
                        .cancelRequest(imageView);
            }
        });

        List<IProfile> profileList = new ArrayList<IProfile>();
        String[] profileNames = getResources().getStringArray(R.array.profile_values);

        for (String name : profileNames) {
            IProfile profile = new ProfileDrawerItem();
            profile.withName(name);
            profileList.add(profile);
        }

        final AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.navdrawer_background)
                .withProfiles(profileList)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        setSavedProfile(profile);
                        return true;
                    }
                })
                .build();

        accountHeader.setActiveProfile(getSavedProfile());

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(accountHeader)
                .withDrawerItems(getDrawerItems())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .build();


    }

    private List<IDrawerItem> getDrawerItems() {

        List<String> mDrawerItems = Arrays.asList(
                "Drawer Item 1",
                "Drawer Item 2",
                "Parker's Firebase Page"
        );

        List<IDrawerItem> drawerItems = new ArrayList<IDrawerItem>();

        for (String drawerItemName : mDrawerItems) {

            IDrawerItem drawerItem = new PrimaryDrawerItem()
                    .withName(drawerItemName)
                    .withTag(drawerItemName)
                    .withIdentifier(mDrawerItems.indexOf(drawerItemName));

            drawerItems.add(drawerItem);
        }

        return drawerItems;
    }

    @Override
    protected void onDestroy() {
        navPrefs.edit().putString("name", getSavedProfile().getName().toString());
        navPrefs.edit().apply();
        super.onDestroy();
    }

    public IProfile getSavedProfile() {
        return savedProfile;
    }

    public void setSavedProfile(IProfile savedProfile) {
        this.savedProfile = savedProfile;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public class SongListAdapter extends BaseAdapter {

        private ArrayList<Song> songs;
        private Context mContext;
        private boolean isPlayingAlready;

        public SongListAdapter (Context context, ArrayList<Song> songList) {
            mContext = context;
            songs = songList;
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int i) {
            return songs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View listView = getLayoutInflater().inflate(R.layout.list_view_layout, viewGroup, false);

            TextView albumName = (TextView) listView.findViewById(R.id.track_collection_name);
            TextView trackName = (TextView) listView.findViewById(R.id.track_name);
            TextView artistName = (TextView) listView.findViewById(R.id.artist_name);
            ImageView albumCover = (ImageView) listView.findViewById(R.id.track_collection_image);
            final ToggleButton playButton = (ToggleButton) listView.findViewById(R.id.play_button);


            albumName.setText("");
            trackName.setText("");

            final Song song = (Song) getItem(i);

            albumName.setText(song.getCollectionName());
            trackName.setText(song.getTrackName());
            artistName.setText(song.getArtistName());

            Picasso.with(mContext).load(
                    song.getArtworkUrl60())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(albumCover);

            playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    try {
                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.stop();
                            mediaPlayer.reset();

                        }
                        if (songPlayingId != song.getTrackId()) {
                            mediaPlayer.setDataSource(song.getPreviewUrl());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            songPlayingId = song.getTrackId();

                        } else {
                            playButton.setChecked(false);
                        }

                    } catch (IOException e) {
                        Log.e(getClass().getSimpleName(), "IOException occured" + e);
                    }


                }
            });



            listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getMainActivity(), SongActivity.class);
                    intent.putExtra("song", song);
                    startActivity(intent);
                }
            });


            return listView;
        }
    }




}
