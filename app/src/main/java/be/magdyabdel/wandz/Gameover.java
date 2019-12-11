package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;

public class Gameover extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Profile> profiles;
    private ScoreAdapter adapter;
    private Profile profile;
    private Boolean master;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        profiles = (ArrayList<Profile>) getIntent().getSerializableExtra("profiles");
        master = (Boolean) getIntent().getSerializableExtra("master");
        connectionManager = (ConnectionManager) getIntent().getSerializableExtra("conman");
        profiles.sort(new ScoreComparator());

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
        if (master) {
            new StopThread().start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                Intent intent = new Intent(this, Menu.class);
                profile.setScore(0);
                profile.setId(-1);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
        }
    }

    class StopThread extends Thread {
        StopThread() {
        }

        @Override
        public void run() {
            connectionManager.sendData("STOP");
        }
    }

    private class ScoreComparator implements Comparator<Profile> {
        @Override
        public int compare(Profile o1, Profile o2) {
            return Integer.compare(o1.getScore(), o2.getScore());
        }
    }
}
