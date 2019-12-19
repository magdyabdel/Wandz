package be.magdyabdel.wandz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Gameover extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Profile> profiles;
    private ScoreAdapter adapter;
    private Profile profile;
    private Boolean master;
    private ConnectionManager connectionManager;
    private static MediaPlayer play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        profiles = (ArrayList<Profile>) getIntent().getSerializableExtra("profiles");
        master = (Boolean) getIntent().getSerializableExtra("master");
        connectionManager = (ConnectionManager) getIntent().getSerializableExtra("conman");

        profiles.sort(new ScoreComparator());
        Collections.reverse(profiles);

        RecyclerView recyclerview = findViewById(R.id.score_recycleViewer);
        adapter = new ScoreAdapter(profiles, this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        ImageView first = findViewById(R.id.first);
        ImageView second = findViewById(R.id.second);
        ImageView third = findViewById(R.id.third);
        TextView firstName = findViewById(R.id.mvp_name);
        TextView secondName = findViewById(R.id.second_name);
        TextView thirdName = findViewById(R.id.third_name);
        try {
            profiles.get(0).setProfileImage(this, first);
            firstName.setText(profiles.get(0).getName());
        } catch (IndexOutOfBoundsException e) {
            first.setVisibility(View.GONE);
            firstName.setVisibility(View.GONE);
        }
        try {
            profiles.get(1).setProfileImage(this, second);
            secondName.setText(profiles.get(1).getName());
        } catch (IndexOutOfBoundsException e) {
            second.setVisibility(View.GONE);
            secondName.setVisibility(View.GONE);
        }
        try {
            profiles.get(2).setProfileImage(this, third);
            thirdName.setText(profiles.get(2).getName());
        } catch (IndexOutOfBoundsException e) {
            third.setVisibility(View.GONE);
            thirdName.setVisibility(View.GONE);
        }
        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);

        Log.i("gameover ID's", ""+profile.getId() +" "+profiles.get(0).getId());
        if(play == null){
        play = MediaPlayer.create(Gameover.this,R.raw.fanfare); //sound have wrong name
        play.setLooping(true);
        play.start();}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                play.stop();
                play.reset();
                play.release();
                play = null;
                Intent intent = new Intent(Gameover.this, Menu.class);
                profile.setScore(0);
                profile.setId(-1);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
        }
    }

    public boolean releasemediaplayer(){
        try {
            play.stop();
            play.reset();
            play.release();
            play = null;
            return true;
        } catch (NullPointerException e) {
        }
        return false;
    }

    private class ScoreComparator implements Comparator<Profile> {
        @Override
        public int compare(Profile o1, Profile o2) {
            return Integer.compare(o1.getScore(), o2.getScore());
        }
    }
}
