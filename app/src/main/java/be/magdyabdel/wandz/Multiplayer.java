package be.magdyabdel.wandz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Multiplayer extends AppCompatActivity implements View.OnClickListener {

    private AppData appData;
    private DrawerLayout drawer;
    private ProgressBar offensiveProgressBar;
    private ProgressBar defensiveProgressBar;
    private ProgressBar utilityProgressBar;
    private Button offensiveButton;
    private Button defensiveButton;
    private Button utilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_multiplayer);
        stub.inflate();

        ImageView profileImageView = findViewById(R.id.profile);
        profileImageView.setOnClickListener(this);

        TextView yourNameTextView = findViewById(R.id.your_name);
        yourNameTextView.setOnClickListener(this);

        TextView trainingmodeTextView = findViewById(R.id.training_mode);
        trainingmodeTextView.setOnClickListener(this);

        ImageView trainingmodeImageView = findViewById(R.id.training_mode_image);
        trainingmodeImageView.setOnClickListener(this);

        TextView multiplayerTextView = findViewById(R.id.multiplayer);
        multiplayerTextView.setOnClickListener(this);

        ImageView multiplayerImageView = findViewById(R.id.multiplayer_image);
        multiplayerImageView.setOnClickListener(this);

        TextView myWandTextView = findViewById(R.id.my_wand);
        myWandTextView.setOnClickListener(this);

        ImageView myWandImageView = findViewById(R.id.my_wand_image);
        myWandImageView.setOnClickListener(this);
        /******* Navigation Drawer *******/

        appData = (AppData) getIntent().getSerializableExtra("data");
        yourNameTextView.setText(appData.getName_player());
        appData.setProfileImage(this, profileImageView, -1);

        ImageView profile_image = findViewById(R.id.profile_image);
        appData.setProfileImage(this, profile_image, -1);
        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText((appData.getName_player()));

        drawer = findViewById(R.id.drawer_layout);
        offensiveProgressBar = findViewById(R.id.offensive_progressBar);
        defensiveProgressBar = findViewById(R.id.defensive_progressBar);
        utilityProgressBar = findViewById(R.id.utility_progressBar);
        offensiveButton = findViewById(R.id.offensive_button);
        defensiveButton = findViewById(R.id.defensive_button);
        utilityButton = findViewById(R.id.utility_button);

        offensiveButton.setOnClickListener(this);
        defensiveButton.setOnClickListener(this);
        utilityButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        drawer = findViewById(R.id.drawer_layout);
        offensiveProgressBar = findViewById(R.id.offensive_progressBar);
        defensiveProgressBar = findViewById(R.id.defensive_progressBar);
        utilityProgressBar = findViewById(R.id.utility_progressBar);
        offensiveButton = findViewById(R.id.offensive_button);
        defensiveButton = findViewById(R.id.defensive_button);
        utilityButton = findViewById(R.id.utility_button);

        Intent intent = null;
        switch (view.getId()) {
            /******* Navigation Drawer *******/
            case R.id.profile:
            case R.id.your_name:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.training_mode:
            case R.id.training_mode_image:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.multiplayer:
            case R.id.multiplayer_image:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.my_wand:
            case R.id.my_wand_image:
                intent = new Intent(this, MyWand.class);
                break;
            /******* Navigation Drawer *******/

            case R.id.offensive_button:
                ProgressBarAnimation anim = new ProgressBarAnimation(offensiveProgressBar, 0, 100);
                anim.setDuration(10000);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        offensiveButton.setClickable(false);
                        offensiveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardWhite)));
                        offensiveButton.setTextColor(getResources().getColor(R.color.wizardBlue));

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        offensiveButton.setClickable(true);
                        offensiveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardBlue)));
                        offensiveButton.setTextColor(getResources().getColor(R.color.wizardDarkBlue));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                offensiveProgressBar.startAnimation(anim);
                break;
            case R.id.defensive_button:
                ProgressBarAnimation anim2 = new ProgressBarAnimation(defensiveProgressBar, 0, 100);
                anim2.setDuration(1000);
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        defensiveButton.setClickable(false);
                        defensiveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardWhite)));
                        defensiveButton.setTextColor(getResources().getColor(R.color.wizardBlue));
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        defensiveButton.setClickable(true);
                        defensiveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardBlue)));
                        defensiveButton.setTextColor(getResources().getColor(R.color.wizardDarkBlue));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                defensiveProgressBar.startAnimation(anim2);
                break;
            case R.id.utility_button:
                ProgressBarAnimation anim3 = new ProgressBarAnimation(utilityProgressBar, 0, 100);
                anim3.setDuration(2000);
                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        utilityButton.setClickable(false);
                        utilityButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardWhite)));
                        utilityButton.setTextColor(getResources().getColor(R.color.wizardBlue));
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        utilityButton.setClickable(true);
                        utilityButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wizardBlue)));
                        utilityButton.setTextColor(getResources().getColor(R.color.wizardDarkBlue));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                utilityProgressBar.startAnimation(anim3);
                break;
            default:
                break;
        }

        if (intent != null) {
            intent.putExtra("data", appData);
            startActivity(intent);
            finish();
        }
    }
}
