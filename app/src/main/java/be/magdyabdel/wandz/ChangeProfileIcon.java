package be.magdyabdel.wandz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeProfileIcon extends AppCompatActivity implements View.OnClickListener {

    private ImageView profile_image;
    private Profile profile;

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
        ImageView random = findViewById(R.id.random);
        random.setOnClickListener(this);

        profile_image = findViewById(R.id.profile_vector);
        profile = (Profile) getIntent().getSerializableExtra("profile");
        TextView yourNameTextView = findViewById(R.id.name);
        yourNameTextView.setText(profile.getName());
        profile.setProfileImage(this, profile_image);

        TextView outfit = findViewById(R.id.outfit);
        outfit.setTextColor(Color.parseColor(profile.getOutfitColors()[profile.getOutfit_color_array_id()]));
        TextView skin = findViewById(R.id.skin);
        skin.setTextColor(Color.parseColor(profile.getSkinColors()[profile.getSkin_color_array_id()]));
        TextView hair = findViewById(R.id.hair);
        hair.setTextColor(Color.parseColor(profile.getHairColors()[profile.getHair_color_array_id()]));
        TextView eye = findViewById(R.id.eye);
        eye.setTextColor(Color.parseColor(profile.getEyeColors()[profile.getEye_color_array_id()]));
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ready) {
            Intent intent = new Intent(this, Menu.class);
            intent.putExtra("profile", profile);
            startActivity(intent);
            finish();
        } else {
            profile.changeProfileByButton(view.getId());
            profile.setProfileImage(this, profile_image);

            TextView outfit = findViewById(R.id.outfit);
            outfit.setTextColor(Color.parseColor(profile.getOutfitColors()[profile.getOutfit_color_array_id()]));
            TextView skin = findViewById(R.id.skin);
            skin.setTextColor(Color.parseColor(profile.getSkinColors()[profile.getSkin_color_array_id()]));
            TextView hair = findViewById(R.id.hair);
            hair.setTextColor(Color.parseColor(profile.getHairColors()[profile.getHair_color_array_id()]));
            TextView eye = findViewById(R.id.eye);
            eye.setTextColor(Color.parseColor(profile.getEyeColors()[profile.getEye_color_array_id()]));
        }
    }
}
