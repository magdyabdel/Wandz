package be.magdyabdel.wandz;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SpellsActivity extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spells);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(this);

        profile = (Profile) getIntent().getSerializableExtra("profile");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
        }
    }
}
