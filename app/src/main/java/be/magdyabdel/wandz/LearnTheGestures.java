package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LearnTheGestures extends AppCompatActivity implements View.OnClickListener {

    private int[] imagesRoundAnim = new int[]{R.drawable.ic_round1, R.drawable.ic_round2, R.drawable.ic_round3, R.drawable.ic_round4, R.drawable.ic_round5, R.drawable.ic_round6};
    private int[] imagesCrossAnim = new int[]{R.drawable.ic_cross1, R.drawable.ic_cross2, R.drawable.ic_cross3, R.drawable.ic_cross4, R.drawable.ic_cross5, R.drawable.ic_cross6};

    private int gesture = 0;
    private Button showAgain;
    private Button next;
    private TextView well_done;

    private Profile profile;
    private ImageView gestureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_the_gestures);

        showAgain = findViewById(R.id.show_again);
        showAgain.setOnClickListener(this);

        next = findViewById(R.id.next);
        next.setOnClickListener(this);

        well_done = findViewById(R.id.well_done);
        well_done.setText("");

        LearnTheGestures.AnimThread animThread = new LearnTheGestures.AnimThread(gesture);
        animThread.start();

        profile = (Profile) getIntent().getSerializableExtra("profile");
        gestureImage = findViewById(R.id.gesture_image);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.show_again:
                LearnTheGestures.AnimThread animThread2 = new LearnTheGestures.AnimThread(gesture);
                animThread2.start();
                break;
            case R.id.next:
                gesture++;
                if (gesture == 2) {
                    Intent intent = new Intent(this, GameExplanation.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                }
                break;
        }
        LearnTheGestures.AnimThread animThread = new LearnTheGestures.AnimThread(gesture);
        animThread.start();
    }

    private void setCorrectOrNot(Boolean correctOrNot) {
        if (correctOrNot) {
            WriteTextThread writeTextThread = new WriteTextThread("Well Done!");
            writeTextThread.start();
        } else {
            WriteTextThread writeTextThread2 = new WriteTextThread("Try Again!");
            writeTextThread2.start();
        }
    }

    class AnimThread extends Thread {

        int gesture;

        AnimThread(int gesture) {
            this.gesture = gesture;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setClickable(false);
                    showAgain.setClickable(false);
                }
            });

            final int[] array;

            switch (gesture) {
                case 0:
                    array = imagesRoundAnim;
                    break;
                case 1:
                    array = imagesCrossAnim;
                    break;
                default:
                    array = new int[0];
                    break;
            }

            for (int i = 0; i < array.length; i++) {
                final int j = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gestureImage.setImageResource(array[j]);
                    }
                });
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setClickable(true);
                    showAgain.setClickable(true);
                }
            });
        }
    }

    class WriteTextThread extends Thread {

        final String text;

        WriteTextThread(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    well_done.setText(text);
                }
            });
        }
    }
}
