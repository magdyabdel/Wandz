package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Start extends AppCompatActivity implements View.OnClickListener {

    private Boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ConstraintLayout constraintLayout = findViewById(R.id.start);
        constraintLayout.setOnClickListener(this);

        final Animation in = new AlphaAnimation(0.1f, 1.0f);
        in.setDuration(2000);
        final Animation out = new AlphaAnimation(1.0f, 0.1f);
        out.setDuration(2000);
        final TextView textView = findViewById(R.id.start_touch_continue);
        textView.startAnimation(out);
        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // nothing
            }
        });
        in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // nothing
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            click = true;
            Intent intent = new Intent(this, ChooseYourWand.class);
            startActivity(intent);
        }
    }
}