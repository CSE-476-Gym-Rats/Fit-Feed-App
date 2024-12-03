package com.example.fitfeed.activities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitfeed.R;
import com.example.fitfeed.models.Post;
import com.example.fitfeed.fragments.FeedFragment;
import com.example.fitfeed.fragments.ProfileFragment;
import com.example.fitfeed.fragments.WorkoutsFragment;
import com.example.fitfeed.util.GsonHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String POSTS_LIST_STATE = "MainActivity_postsListState";
    public static final String PREFS_NAME = "MainPrefsFile";
    private static final String PREFS_KEY_POSTS = "posts";

    private BottomNavigationView bottomNavigationView;
    private TextView titleBarTextView;
    private LinearLayoutManager postsLayoutManager;
    private ArrayList<Post> posts;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Prevent rotation to maintain instagram like feed experience
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // Setup bottom nav view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_bar_feed);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Setup main toolbar view and set title
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        titleBarTextView = findViewById(R.id.mainToolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitleBarText(bottomNavigationView.getSelectedItemId());

        // Load posts from shared prefs
        // todo change storage method (clears on app restart for dev purposes)
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        String postsJson = preferences.getString(PREFS_KEY_POSTS, null);
        if (postsJson != null) {
            // deserialize from json string
            ArrayList<Post> postsDeserialized = GsonHelper.getGson().fromJson(postsJson, new TypeToken<List<Post>>(){}.getType());
            if (postsDeserialized != null) {
                // restore post state from loaded posts
                restorePostsState(postsDeserialized);
                //Log.d("MainActivity.onCreate", String.format("Read %d posts from SharedPrefs", postsDeserialized.size()));
            }
        }

        // set default state if no saved state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, FeedFragment.class, null).commit();
            FeedFragment fragment = (FeedFragment)currentFragment;
            if (fragment != null) {
                ArrayList<Post> postList = fragment.getPostsList();
                if (postList != null) {
                    posts = postList;
                }
            }
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_bar_feed);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    /**
     * Helper to handle an uninitialized post list.
     * @return {@link ArrayList} of {@link Post} objects.
     */
    public ArrayList<Post> getPosts() {
        return (this.posts != null) ? this.posts : new ArrayList<>();
    }

    /**
     * Helper to restore posts when loading from SharedPrefs or saved instance.
     * @param posts {@link List} of {@link Post} objects to restore.
     */
    public void restorePostsState(List<Post> posts) {
        this.posts = new ArrayList<>();
        this.posts.addAll(posts);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save menu item selected state
        outState.putInt("bottomNavSelectedItemId", bottomNavigationView.getSelectedItemId());

        // Save posts state
        outState.putParcelableArrayList(POSTS_LIST_STATE, getPosts());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore menu item selected state
        int itemId = savedInstanceState.getInt("bottomNavSelectedItem");
        bottomNavigationView.setSelectedItemId(itemId);
        setTitleBarText(bottomNavigationView.getSelectedItemId());

        // Restore posts state
        ArrayList<Post> restoredPosts = savedInstanceState.getParcelableArrayList(POSTS_LIST_STATE);
        if (restoredPosts != null) { restorePostsState(restoredPosts); }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save posts to SharedPrefs on pause so we can restore after app suspend or activity destroy
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFS_KEY_POSTS, GsonHelper.getGson().toJson(posts));
        //Log.d("MainActivity.onPause", String.format("Stored %d posts in SharedPrefs", posts.size()));
        editor.apply();
    }

    /**
     * Used as OnItemSelectedListener for bottom navigation bar.
     * @param item the nav bar item that was selected
     * @return true on success
     */
    private boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment = getSupportFragmentManager().findFragmentById(bottomNavigationView.getSelectedItemId());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //Log.d("MainActivity.onNavigationItemSelected", String.format("Fragments: %d", getSupportFragmentManager().getFragments().size()));

        // Set fragment based on menu item selected
        if (fragment == null) {
            if (itemId == R.id.nav_bar_feed) {
                // Send posts to FeedFragment via bundle
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(FeedFragment.ARG_PARAM1, posts);
                //Log.d("MainActivity.onNavigationItemSelected", String.format("Bundled %d posts to send to SocialFragment", posts.size()));
                transaction.replace(R.id.mainFragmentContainer, FeedFragment.class, bundle);
            } else if (itemId == R.id.nav_bar_workouts) {
                transaction.replace(R.id.mainFragmentContainer, WorkoutsFragment.class, null);
            } else if (itemId == R.id.nav_bar_profile) {
                transaction.replace(R.id.mainFragmentContainer, ProfileFragment.class, null);
            }
        }

        transaction.commit();
        currentFragment = fragment;
        setTitleBarText(item.getItemId());

        return true;
    }

    /**
     * Helper to set the title bar text when switching tabs.
     * @param itemId menu item id
     */
    private void setTitleBarText(int itemId) {
        int titleId = 0;

        if (itemId == R.id.nav_bar_feed) {
            titleId = R.string.feed_nav_bar_title;
        } else if (itemId == R.id.nav_bar_workouts) {
            titleId = R.string.workouts_nav_bar_title;
        } else if (itemId == R.id.nav_bar_profile) {
            titleId = R.string.profile_nav_bar_title;
        }

        titleBarTextView.setText(titleId);
    }
}