package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeProfileIcon extends AppCompatActivity implements View.OnClickListener {

    private ImageView profile;
    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_icon);

        ImageView outfit_left = findViewById(R.id.outfit_left);
        outfit_left.setOnClickListener(this);
        ImageView outfit_right = findViewById(R.id.outfit_right);
        outfit_right.setOnClickListener(this);

        ImageView skin_left = findViewById(R.id.skin_left);
        skin_left.setOnClickListener(this);
        ImageView skin_right = findViewById(R.id.skin_right);
        skin_right.setOnClickListener(this);

        ImageView hair_left = findViewById(R.id.hair_left);
        hair_left.setOnClickListener(this);
        ImageView hair_right = findViewById(R.id.hair_right);
        hair_right.setOnClickListener(this);

        ImageView eye_left = findViewById(R.id.eye_left);
        eye_left.setOnClickListener(this);
        ImageView eye_right = findViewById(R.id.eye_right);
        eye_right.setOnClickListener(this);

        Button ready = findViewById(R.id.ready);
        ready.setOnClickListener(this);
        Button gender = findViewById(R.id.gender);
        gender.setOnClickListener(this);
        Button random = findViewById(R.id.random);
        random.setOnClickListener(this);

        profile = findViewById(R.id.profile_vector);

        appData = (AppData) getIntent().getSerializableExtra("data");
        TextView yourNameTextView = findViewById(R.id.name);
        yourNameTextView.setText(appData.getName_player());
        appData.setProfileImage(this, profile, -1);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ready) {
            Intent intent = new Intent(this, Trainingmode.class);
            intent.putExtra("data", appData);
            startActivity(intent);
            finish();
        } else {
            appData.setProfileImage(this, profile, view.getId());
        }

    }

}