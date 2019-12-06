package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MasterPassword extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        Button enter = findViewById(R.id.enter);
        enter.setOnClickListener(this);
        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);

        profile = (Profile) getIntent().getSerializableExtra("profile");
    }

    @Override
    public void onClick(View view) {
        EditText password = findViewById(R.id.password);
        switch (view.getId()) {
            case R.id.menu:
                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.enter:
                if (password.getText().toString().equals("8765")) {
                    Intent intent2 = new Intent(this, MasterConnect.class);
                    intent2.putExtra("profile", profile);
                    startActivity(intent2);
                    finish();
                } else {
                    Toast.makeText(this, "You are not worth being a master!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, BLEService.class);
        stopService(intent);
    }
}
