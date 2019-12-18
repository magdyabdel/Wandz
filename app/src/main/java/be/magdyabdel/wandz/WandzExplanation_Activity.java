package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class WandzExplanation_Activity extends AppCompatActivity implements View.OnClickListener {

    private final int TEXTSPEED = 70;
    private final int SENTENSEPAUSE = 1000;
    private final int BUTTONPAUSE = 100;
    private int[] imageViews = new int[]{R.drawable.ic_profile_intro, R.drawable.ic_profile_intro, R.drawable.ic_the_wand_without_hand,
            R.drawable.ic_the_wand, R.drawable.ic_dont_touch, R.drawable.ic_the_wand_button_zoom, R.drawable.ic_the_wand_button_zoom_pressed,
            R.drawable.ic_intro_small, R.drawable.ic_the_wand_button_zoom_pressed, R.drawable.ic_the_wand_button_zoom, R.drawable.ic_the_wand_without_hand,
            R.drawable.ic_profile_intro,
            R.drawable.ic_multi_step1, R.drawable.ic_multi_step2, R.drawable.ic_multi_step3, R.drawable.ic_multi_step4, R.drawable.ic_multi_step5, R.drawable.ic_multi_step6};
    private String[] textViews = new String[]{
            "Nice To Meet You &!", "Let's Start With The Tutorial.", "First Of All, Every Wizard Has It's Own Wand.",
            "You Hold This Wand Like This, At The Bottom", "Never Touch The Top Of The Wand Because Then The Magic Will Not Work!",
            "To Do A Gesture", "Press The Button.", "Don't Release The Button While Doing The Gesture", "When The Gesture Is Done", "Release The Button",
            "Your wand lights up when its loaded", "Lets Practice This!$",
            "Each wizard has a wand", "get a higher score by killing opponents", "go hide yourself", "energy is generated over time", "standing still for too long lowers your health", "so move regularly, it generates energy faster too!£"};
    private ImageView imageview;
    private TextView textView;
    private Profile profile;
    private Button practice_button;
    private Button skipButton;
    private Speed speed = Speed.NORMAL;
    private Boolean practice = false;
    private int animationstart;
    private ConstraintLayout faster;
    private ConstraintLayout slower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wandz_explanation);

        imageview = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        profile = (Profile) getIntent().getSerializableExtra("profile");
        profile.setSkip(false);
        try {
            animationstart = (int) getIntent().getSerializableExtra("animationstart");
        } catch (NullPointerException e) {
            animationstart = 0;
        }
        practice_button = findViewById(R.id.practice);
        practice_button.setOnClickListener(this);
        practice_button.setVisibility(View.GONE);
        practice_button.setClickable(false);

        skipButton = findViewById(R.id.skip);
        skipButton.setOnClickListener(this);

        faster = findViewById(R.id.faster);
        faster.setOnClickListener(this);
        slower = findViewById(R.id.slower);
        slower.setOnClickListener(this);

        AnimThread animThread = new AnimThread();
        animThread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.practice:
                practice = true;
                break;
            case R.id.skip:
                Intent intent = new Intent(this, Menu.class);
                profile.setSkip(true);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.faster:
                speed = Speed.FASTER;
                break;
            case R.id.slower:
                speed = Speed.SLOWER;
                break;
            default:
                break;
        }
    }

    private void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    private enum Speed {SLOWER, NORMAL, FASTER}

    class AnimThread extends Thread {

        AnimThread() {
        }

        @Override
        public void run() {
            for (int i = animationstart; i < textViews.length; i++) { //for loop for image and text resource adjustment

                if (i < 0) i = 0;
                final int image = imageViews[i];
                final String displayText = textViews[i].replace("&", profile.getName());
                char[] splitString = displayText.toCharArray();
                String text = "";

                if (i == textViews.length - 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            faster.setClickable(false);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            faster.setClickable(true);
                        }
                    });
                }

                for (int j = 0; j < splitString.length && speed.equals(Speed.NORMAL); j++) { //for loop for text and button adjustment
                    if (splitString[j] == '$') { //insert tutorial button
                        delay(BUTTONPAUSE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                practice_button.setVisibility(View.VISIBLE);
                                practice_button.setClickable(true);
                                practice_button.setText(R.string.practice);
                                skipButton.setVisibility(View.GONE);
                                skipButton.setClickable(false);
                            }
                        });
                        while (!practice && speed.equals(Speed.NORMAL)) {
                            if (profile.getSkip()) return;
                            delay(50);
                        }
                        if (practice && speed.equals(Speed.NORMAL)) {
                            Intent intent = new Intent(WandzExplanation_Activity.this, LearnTheGestures.class);
                            intent.putExtra("profile", profile);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    practice_button.setVisibility(View.GONE);
                                    practice_button.setClickable(false);
                                    skipButton.setVisibility(View.VISIBLE);
                                    skipButton.setClickable(true);
                                }
                            });
                            break;
                        }
                    } else if (splitString[j] == '£') { //insert menu button
                        delay(BUTTONPAUSE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                faster.setClickable(false);
                                practice_button.setVisibility(View.VISIBLE);
                                practice_button.setClickable(true);
                                practice_button.setText(R.string.done);
                                skipButton.setVisibility(View.GONE);
                                skipButton.setClickable(false);
                            }
                        });
                        while (!practice && speed.equals(Speed.NORMAL)) {
                            if (profile.getSkip()) return;
                            delay(50);
                        }
                        if (practice && speed.equals(Speed.NORMAL)) {
                            Intent intent = new Intent(WandzExplanation_Activity.this, SpellsActivity.class);
                            intent.putExtra("profile", profile);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    faster.setClickable(true);
                                    practice_button.setVisibility(View.GONE);
                                    practice_button.setClickable(false);
                                    skipButton.setVisibility(View.VISIBLE);
                                    skipButton.setClickable(true);
                                }
                            });
                            break;
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
                        delay(TEXTSPEED);
                    }
                    if (profile.getSkip()) return;
                }

                if (profile.getSkip()) return;
                if (i == textViews.length - 1) {
                    delay(SENTENSEPAUSE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            faster.setClickable(false);
                            practice_button.setVisibility(View.VISIBLE);
                            practice_button.setClickable(true);
                            practice_button.setText(R.string.done);
                            skipButton.setVisibility(View.GONE);
                            skipButton.setClickable(false);
                        }
                    });
                    while (!practice && speed.equals(Speed.NORMAL)) {
                        if (profile.getSkip()) return;
                        delay(50);
                    }
                    if (practice && speed.equals(Speed.NORMAL)) {
                        Intent intent = new Intent(WandzExplanation_Activity.this, SpellsActivity.class);
                        intent.putExtra("profile", profile);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        speed = Speed.NORMAL;
                        i = i - 2;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                faster.setClickable(true);
                                practice_button.setVisibility(View.GONE);
                                practice_button.setClickable(false);
                                skipButton.setVisibility(View.VISIBLE);
                                skipButton.setClickable(true);
                            }
                        });
                    }
                } else if (speed.equals(Speed.FASTER)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(displayText.replace("£", "").replace("$", ""));
                            imageview.setImageResource(image);
                        }
                    });
                    delay(SENTENSEPAUSE);
                    speed = Speed.NORMAL;
                } else if (speed.equals(Speed.SLOWER)) {
                    speed = Speed.NORMAL;
                    i = i - 2;
                } else {
                    delay(SENTENSEPAUSE);
                }
            }
        }
    }
}
