package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Intro extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageview;
    private TextView textView;
    private int[] imageViews = new int[]{R.drawable.ic_profile_intro, R.drawable.ic_profile_intro, R.drawable.ic_join_me, R.drawable.ic_profile_intro};
    private String[] textViews = new String[]{"Welcome To Wandz!", "The World Where You Want To Be The Most Powerful Wizard.", "Join Me To Learn The Basics Of WizardWorld.$", "First, I Have To Know Who You Are!"};
    private Button button;
    private Boolean joined = false;
    private TextView skipButton;
    private Profile profile;
    private ConstraintLayout faster;
    private Boolean fastertext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imageview = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.join);
        button.setOnClickListener(this);
        button.setClickable(false);
        button.setVisibility(View.GONE);

        profile = (Profile) getIntent().getSerializableExtra("profile");

        AnimThread animThread = new AnimThread();
        animThread.start();

        skipButton = findViewById(R.id.skip);
        skipButton.setOnClickListener(this);
        faster = findViewById(R.id.faster);
        faster.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join:
                joined = true;
                break;
            case R.id.skip:
                profile.setSkip(true);
                Intent intent = new Intent(Intro.this, ChooseName.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.faster:
                fastertext = true;
                break;
        }
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
                fastertext = false;
                final int image = imageViews[i];
                char[] splitString = textViews[i].toCharArray();
                String text = "";
                for (int j = 0; j < splitString.length; j++) {
                    if (profile.getSkip()) {
                        break;
                    }
                    if (splitString[j] == '$') {
                        delay(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setClickable(true);
                                button.setVisibility(View.VISIBLE);
                                skipButton.setClickable(false);
                                skipButton.setVisibility(View.GONE);
                            }
                        });
                        while (!joined) {
                            delay(50);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setClickable(false);
                                button.setVisibility(View.GONE);
                            }
                        });
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
                        if (!fastertext) {
                            delay(70);
                        }
                    }

                }
                if (profile.getSkip()) {
                    break;
                }
                delay(1000);
            }
            if (!profile.getSkip()) {
                Intent intent = new Intent(Intro.this, ChooseName.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
            }
        }
    }

}
