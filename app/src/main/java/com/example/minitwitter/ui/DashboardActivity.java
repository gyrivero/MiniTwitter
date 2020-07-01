package com.example.minitwitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ImageView avatarIV;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
            return true;
            case R.id.navigation_tweets_like:
            return true;
            case R.id.navigation_profile:

            return true;
        }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fab = findViewById(R.id.fab);
        avatarIV = findViewById(R.id.imageViewToolbarPhoto);

        getSupportActionBar().hide();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new TweetListFragment())
                .commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTweetDialogFragment dialogFragment = new NewTweetDialogFragment();
                dialogFragment.show(getSupportFragmentManager(),"NewTweetDialogFragment");
            }
        });

        String photoUrl = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_PHOTOURL);

        if (!photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(Constantes.API_MINITWITTER_FILES_URL + photoUrl)
                    .into(avatarIV);
        }

    }

}
