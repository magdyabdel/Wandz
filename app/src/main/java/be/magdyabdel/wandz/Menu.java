package be.magdyabdel.wandz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;
    private Boolean demo;
    private Boolean off = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        profile = (Profile) getIntent().getSerializableExtra("profile");

        Button tutorial = findViewById(R.id.tutorial);
        tutorial.setOnClickListener(this);
        Button training = findViewById(R.id.training);
        training.setOnClickListener(this);
        Button spells = findViewById(R.id.spells);
        spells.setOnClickListener(this);
        Button multiplayer = findViewById(R.id.stop_game);
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
        ImageView closeApp = findViewById(R.id.app_close);
        closeApp.setOnClickListener(this);

        welcome.setText("Welcome " + profile.getName());
        profile.setProfileImage(this, profile_image);
        if (profile.getDefaultOutfit()) {
            profile.changeProfileByButton(R.id.random);
            profile.setProfileImage(this, profile_image);
            profile.setDefaultOutfit(false);
        }

        multiplayer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(profile.getOutfitColors()[profile.getOutfit_color_array_id()])));
        multiplayer.setTextColor(Color.parseColor(profile.getOutfitColorsTwo()[profile.getOutfit_color_array_id()]));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Menu.class);
        switch (view.getId()) {
            case R.id.tutorial:
                intent = new Intent(this, WandzExplanation_Activity.class);
                profile.setSkip(false);
                break;
            case R.id.training:
                intent = new Intent(this, LearnTheGestures.class);
                profile.setSkip(true);
                break;
            case R.id.spells:
                intent = new Intent(this, SpellsActivity.class);
                profile.setSkip(true);
                break;
            case R.id.stop_game:
                intent = new Intent(this, MultiplayerConnect.class);
                break;
            case R.id.setup_wand:
                intent = new Intent(this, ChooseYourWand.class);
                profile.setSkip(true);
                break;
            case R.id.profile_image:
            case R.id.change_profile:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.master:
                intent = new Intent(this, MasterPassword.class);
                break;
            case R.id.app_close:
                if (!off) {
                    new powerThread().start();
                } else {
                    Intent intent2 = new Intent(this, BLEService.class);
                    stopService(intent2);
                    finishAffinity();
                    System.exit(1);
                }
                break;
        }
        if (view.getId() != R.id.app_close) {
            intent.putExtra("profile", profile);
            startActivity(intent);
        }
    }

    private class powerThread extends Thread {

        powerThread() {
        }

        @Override
        public void run() {
            off = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Menu.this, "Press again to confirm!", Toast.LENGTH_SHORT).show();
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            off = false;
        }
    }
}
