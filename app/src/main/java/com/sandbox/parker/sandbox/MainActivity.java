package com.sandbox.parker.sandbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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
import com.sandbox.parker.sandboxapi.http.HTTPRequest;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.time_view);
        mSearchView = (SearchView) findViewById(R.id.searchView2);

        mToolbar.setTitle("Test title");
        setSupportActionBar(mToolbar);


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... strings) {

                        HTTPRequest request = new HTTPRequest("https://itunes.com/search");
                        Map<String, String> params = new HashMap<>();
                        params.put("term", s);
                        params.put("country", "US");
                        return request.post("groups", params);

                    }

                    @Override
                    protected void onPostExecute(String result) {
                        mTextView.setText("Result: " + result);
                    }


                }.execute();

                return true;
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

//        for (IProfile profile : profileList) {
//            if (profile.getName().toString() == navPrefs.getString("name", null)) {
//                accountHeader.setActiveProfile(profile);
//            }
//
//        }

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
}
