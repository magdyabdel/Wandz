package be.magdyabdel.wandz;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class Start extends AppCompatActivity implements View.OnClickListener {

    private Boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ConstraintLayout constraintLayout = findViewById(R.id.start);
        constraintLayout.setOnClickListener(this);

//        GradientDrawable gradientDrawable = new GradientDrawable(
//                GradientDrawable.Orientation.TOP_BOTTOM,
//                new int[]{ContextCompat.getColor(this, R.color.start_grad_end),
//                        ContextCompat.getColor(this, R.color.start_grad_84),
//                        ContextCompat.getColor(this, R.color.start_grad_71),
//                        ContextCompat.getColor(this, R.color.start_grad_59),
//                        ContextCompat.getColor(this, R.color.start_grad_46),
//                        ContextCompat.getColor(this, R.color.start_grad_30),
//                        ContextCompat.getColor(this, R.color.start_grad_24),
//                        ContextCompat.getColor(this, R.color.start_grad_top)});
//        findViewById(R.id.start).setBackground(gradientDrawable);

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
            Intent intent = new Intent(this, Intro.class);
            startActivity(intent);
        }
    }
}