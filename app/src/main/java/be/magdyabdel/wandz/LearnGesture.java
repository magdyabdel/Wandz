package be.magdyabdel.wandz;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class LearnGesture extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private GestureDetectorCompat gestureDetectorCompat = null;
    private int[] gestureImages = new int[]{R.drawable.ic_gesture_horizontal_right, R.drawable.ic_gesture_horizontal_left,
            R.drawable.ic_gesture_vertical_down, R.drawable.ic_gesture_vertical_up};
    private int currentGestureImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_gesture);

        imageView = findViewById(R.id.view_gestures);
        imageView.setImageResource(R.drawable.ic_gesture_horizontal_right);

        Typeface textviewTypeFace = Typeface.createFromAsset(getAssets(), "fonts/MagicSchoolOne.ttf");
        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(textviewTypeFace);
        textView.setText("Choose a gesture and practice with your wand!");
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        gestureListener.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    @Override
    public void onClick(View view) {
    }

    public void changeGestureImage(int direction) {
        if (direction == 1) {
            currentGestureImage++;
            if (currentGestureImage >= gestureImages.length) {
                currentGestureImage = 0;
            }
        }
        if (direction == -1) {
            currentGestureImage--;
            if (currentGestureImage < 0) {
                currentGestureImage = gestureImages.length - 1;
            }
        } else {
            Toast toast = Toast.makeText(this, "Swipe to left or right to change the gesture!", Toast.LENGTH_SHORT);
            toast.show();
        }
        imageView.setImageResource(gestureImages[currentGestureImage]);
    }

}
