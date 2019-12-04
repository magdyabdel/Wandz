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
            R.drawable.ic_profile_intro};
    private String[] textViews = new String[]{"Nice To Meet You &!", "Let's Start With The Tutorial.", "First Of All, Every Wizard Has It's Own Wand.",
            "You Hold This Wand Like This, At The Bottom", "Never Touch The Top Of The Wand Because Then The Magic Will Not Work!",
            "To Do A Gesture", "Press The Button.", "Don't Release The Button While Doing The Gesture", "When The Gesture Is Done", "Release The Button",
            "You Will Notice That Your Wand Is Loaded When The Gesture Is Done Correctly.", "Lets Practice This!"};

    private ImageView imageview;
    private TextView textView;
    private Profile profile;
    private Button practice;
    private Boolean skip = false;
    private Button skipButton;

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.practice:
                break;
            case R.id.skip:
                skip = true;
                break;
        }
        Intent intent = new Intent(this, ChooseYourWand.class);
        intent.putExtra("profile", profile);
        intent.putExtra("skip", skip);
        startActivity(intent);
    }

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
                final int image = imageViews[i];
                char[] splitString = textViews[i].toCharArray();
                String text = "";
                for (int j = 0; j < splitString.length; j++) {
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
                            delay(60);
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
                        delay(60);
                    }
                    if (skip) break;

                }
                if (skip) break;
                delay(1000);
            }
            if (!skip) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        practice.setVisibility(View.VISIBLE);
                        practice.setClickable(true);
                        skipButton.setVisibility(View.GONE);
                        skipButton.setClickable(false);
                    }
                });
                delay(100);
            }
        }
    }
}
