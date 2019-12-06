package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        new AnimThread().start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            click = true;
            Intent intent = new Intent(this, Intro.class);
            startActivity(intent);
        }
    }

    class AnimThread extends Thread {

        AnimThread() {
        }

        @Override
        public void run() {
            while (!click) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                if (click) {
                    break;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Start.this, "Click The Screen To Enter", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}