package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MultiplayerBusy extends AppCompatActivity implements View.OnClickListener {

    private Button menu;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_busy);

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);
        profile = (Profile) getIntent().getSerializableExtra("profile");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                break;
        }
    }
}
