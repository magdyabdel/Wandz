package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Start extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView imageView = findViewById(R.id.wizard);
        imageView.setImageResource(R.drawable.ic_welcome_wizard);

        Button button = findViewById(R.id.enter);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.enter:
                Intent intent = new Intent(this, ChooseName.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}