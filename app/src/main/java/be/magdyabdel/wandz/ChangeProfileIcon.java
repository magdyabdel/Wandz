package be.magdyabdel.wandz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.devs.vectorchildfinder.VectorChildFinder;

import java.util.Random;

public class ChangeProfileIcon extends AppCompatActivity implements View.OnClickListener {

    private int outfit_color_array_id = 0;
    private int skin_color_array_id = 0;
    private int hair_color_array_id = 0;
    private int eye_color_array_id = 0;
    private String[] outfitColors;
    private String[] skinColors;
    private String[] hairColors;
    private String[] eyeColors;
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
        outfitColors = getResources().getStringArray(R.array.outfit_array);
        skinColors = getResources().getStringArray(R.array.skin_array);
        hairColors = getResources().getStringArray(R.array.hair_array);
        eyeColors = getResources().getStringArray(R.array.eye_array);

        appData = (AppData) getIntent().getSerializableExtra("data");
        TextView yourNameTextView = findViewById(R.id.name);
        yourNameTextView.setText(appData.getName_player());
    }

    @Override
    public void onClick(View view) {

        VectorChildFinder vector = new VectorChildFinder(this, R.drawable.ic_profile, profile);
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_light = vector.findPathByName("cape_light");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_dark1 = vector.findPathByName("cape_dark1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_dark2 = vector.findPathByName("cape_dark2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hat_path_light = vector.findPathByName("hat_light");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hat_path_dark = vector.findPathByName("hat_dark");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path1 = vector.findPathByName("hair1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path2 = vector.findPathByName("hair2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path3 = vector.findPathByName("hair3");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path1 = vector.findPathByName("skin1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path2 = vector.findPathByName("skin2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path3 = vector.findPathByName("skin3");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath eye_path_left = vector.findPathByName("eyeLeft");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath eye_path_right = vector.findPathByName("eyeRight");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path1 = vector.findPathByName("details1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path2 = vector.findPathByName("details2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path3 = vector.findPathByName("details3");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path4 = vector.findPathByName("details4");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path5 = vector.findPathByName("details5");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path6 = vector.findPathByName("details6");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path7 = vector.findPathByName("details7");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path8 = vector.findPathByName("details8");

        switch (view.getId()) {
            case R.id.outfit_left:

                outfit_color_array_id = outfit_color_array_id - 2;
                if (outfit_color_array_id < 0) {
                    outfit_color_array_id = outfitColors.length - 2;
                }
                break;
            case R.id.outfit_right:

                outfit_color_array_id = outfit_color_array_id + 2;
                if (outfit_color_array_id >= outfitColors.length) {
                    outfit_color_array_id = 0;
                }
                break;
            case R.id.hair_left:

                hair_color_array_id--;
                if (hair_color_array_id < 0) {
                    hair_color_array_id = hairColors.length - 1;
                }
                break;
            case R.id.hair_right:

                hair_color_array_id++;
                if (hair_color_array_id >= hairColors.length) {
                    hair_color_array_id = 0;
                }
                break;
            case R.id.skin_left:

                skin_color_array_id = skin_color_array_id - 2;
                if (skin_color_array_id < 0) {
                    skin_color_array_id = skinColors.length - 2;
                }
                break;
            case R.id.skin_right:

                skin_color_array_id = skin_color_array_id + 2;
                if (skin_color_array_id >= skinColors.length) {
                    skin_color_array_id = 0;
                }
                break;
            case R.id.eye_left:

                eye_color_array_id--;
                if (eye_color_array_id < 0) {
                    eye_color_array_id = eyeColors.length - 1;
                }
                break;
            case R.id.eye_right:

                eye_color_array_id++;
                if (eye_color_array_id >= eyeColors.length) {
                    eye_color_array_id = 0;
                }
                break;
            case R.id.random:
                outfit_color_array_id = new Random().nextInt((outfitColors.length - 1) / 2) * 2;
                skin_color_array_id = new Random().nextInt((skinColors.length - 1) / 2) * 2;
                hair_color_array_id = new Random().nextInt((hairColors.length));
                eye_color_array_id = new Random().nextInt((eyeColors.length - 1));

                break;
            case R.id.ready:
                Intent intent = new Intent(this, Trainingmode.class);
                intent.putExtra("data", appData);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

        cape_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        hat_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        cape_path_dark1.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));
        cape_path_dark2.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));
        hat_path_dark.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));

        hair_path1.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path2.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path3.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));

        skin_path1.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path2.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path3.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));

        details_path1.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path2.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path3.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path4.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path5.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path6.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path7.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path8.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));

        eye_path_left.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));
        eye_path_right.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));


    }

}
