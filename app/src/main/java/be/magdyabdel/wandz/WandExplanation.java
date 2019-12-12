package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WandExplanation extends AppCompatActivity implements View.OnClickListener {

    private int[] imageViews = new int[]{R.drawable.ic_profile_intro, R.drawable.ic_profile_intro, R.drawable.ic_the_wand_without_hand,
            R.drawable.ic_the_wand, R.drawable.ic_dont_touch, R.drawable.ic_the_wand_button_zoom, R.drawable.ic_the_wand_button_zoom_pressed,
            R.drawable.ic_intro_small, R.drawable.ic_the_wand_button_zoom_pressed, R.drawable.ic_the_wand_button_zoom, R.drawable.ic_the_wand_without_hand,
            R.drawable.ic_profile_intro,
            R.drawable.ic_multi_step1, R.drawable.ic_multi_step2, R.drawable.ic_multi_step3, R.drawable.ic_multi_step4, R.drawable.ic_multi_step5, R.drawable.ic_multi_step6};
    private String[] textViews = new String[]{
            "Nice To Meet You &!", "Let's Start With The Tutorial.", "First Of All, Every Wizard Has It's Own Wand.",
            "You Hold This Wand Like This, At The Bottom", "Never Touch The Top Of The Wand Because Then The Magic Will Not Work!",
            "To Do A Gesture", "Press The Button.", "Don't Release The Button While Doing The Gesture", "When The Gesture Is Done", "Release The Button",
            "Your wand lights up when its loaded", "Lets Practice This!",
            "Each wizard has a wand", "get a higher score by killing opponents", "go hide yourself", "energy is generated over time", "standing still for too long lowers your health", "so move regularly, it generates energy faster too!"};

    private ImageView imageview;
    private TextView textView;
    private Profile profile;
    private Button practice;
    private Button skipButton;
    private Speed speed = Speed.NORMAL;

    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch (view.getId()) {
            case R.id.practice:
                intent = new Intent(this, LearnTheGestures.class);
                break;
            case R.id.skip:
                intent = new Intent(this, Menu.class);
                profile.setSkip(true);
                break;
            case R.id.faster:
                speed = Speed.FASTER;
                break;
            case R.id.slower:
                speed = Speed.SLOWER;
                break;
            default:
                intent = new Intent(this, WandExplanation.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("profile", profile);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wand_explanation);

        imageview = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        profile = (Profile) getIntent().getSerializableExtra("profile");
        practice = findViewById(R.id.practice);
        practice.setOnClickListener(this);
        practice.setVisibility(View.GONE);
        practice.setClickable(false);

        skipButton = findViewById(R.id.skip);
        skipButton.setOnClickListener(this);

        AnimThread animThread = new AnimThread();
        animThread.start();
    }

    private enum Speed {SLOWER, NORMAL, FASTER}

    private void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    class AnimThread extends Thread {

        AnimThread() {
        }

        @Override
        public void run() {
            for (int i = 0; i < textViews.length; i++) {

                if (i < 0) i = 0;
                final int image = imageViews[i];
                final String displayText = textViews[i];
                char[] splitString = displayText.toCharArray();
                String text = "";
                for (int j = 0; j < splitString.length && speed.equals(Speed.NORMAL); j++) {
                    if (splitString[j] == '&') {
                        char[] splitName = profile.getName().toCharArray();
                        for (int k = 0; k < splitName.length; k++) {
                            text += splitName[k];
                            final String textToView = text;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(textToView);
                                }
                            });
                            if (profile.getSkip()) break;
                            delay(100);
                        }
                    } else {
                        text += splitString[j];
                        final String textToView = text;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(textToView);
                                imageview.setImageResource(image);
                            }
                        });
                        delay(70);
                    }
                    if (profile.getSkip()) break;

                }
                if (profile.getSkip()) break;
                else if (speed.equals(Speed.FASTER)) {
                    speed = Speed.NORMAL;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(displayText);
                            imageview.setImageResource(image);
                        }
                    });
                } else if (speed.equals(Speed.SLOWER)) {
                    speed = Speed.NORMAL;
                    i = i - 2;
                } else {
                    delay(1000);
                }
            }
            if (!profile.getSkip()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        practice.setVisibility(View.VISIBLE);
                        practice.setClickable(true);
                        skipButton.setVisibility(View.GONE);
                        skipButton.setClickable(false);
                    }
                });
            }
        }
    }
}
