package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button training = findViewById(R.id.training);
        training.setOnClickListener(this);
        Button multiplayer = findViewById(R.id.multiplayer);
        multiplayer.setOnClickListener(this);
        Button wand = findViewById(R.id.setup_wand);
        wand.setOnClickListener(this);
        Button profile_button = findViewById(R.id.change_profile);
        profile_button.setOnClickListener(this);
        ImageView profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        TextView welcome = findViewById(R.id.welcome);
        TextView master = findViewById(R.id.master);
        master.setOnClickListener(this);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        welcome.setText("Welcome " + profile.getName());
        profile.setProfileImage(this, profile_image);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Menu.class);
        switch (view.getId()) {
            case R.id.training:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.multiplayer:
                intent = new Intent(this, MultiplayerConnect.class);
                break;
            case R.id.setup_wand:
                intent = new Intent(this, MyWand.class);
                break;
            case R.id.profile_image:
            case R.id.change_profile:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.master:
                intent = new Intent(this, MasterPassword.class);
                break;
        }
        intent.putExtra("profile", profile);
        startActivity(intent);
    }

}
