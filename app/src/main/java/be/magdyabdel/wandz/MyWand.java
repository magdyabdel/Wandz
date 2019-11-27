package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MyWand extends AppCompatActivity implements View.OnClickListener {

    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_my_wand);
        stub.inflate();

        ImageView profileImageView = findViewById(R.id.profile);
        profileImageView.setOnClickListener(this);
        TextView yourNameTextView = findViewById(R.id.your_name);
        yourNameTextView.setOnClickListener(this);
        Button trainingmode = findViewById(R.id.training_mode);
        trainingmode.setOnClickListener(this);
        Button multiplayer = findViewById(R.id.multiplayer);
        multiplayer.setOnClickListener(this);
        Button myWand = findViewById(R.id.my_wand);
        myWand.setOnClickListener(this);
        /******* Navigation Drawer *******/

        appData = (AppData) getIntent().getSerializableExtra("data");
        yourNameTextView.setText(appData.getName_player());
        appData.setProfileImage(this, profileImageView, -1);
    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Intent intent = null;
        switch (view.getId()) {
            /******* Navigation Drawer *******/
            case R.id.profile:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.training_mode:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.multiplayer:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.my_wand:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            /******* Navigation Drawer *******/

            default:
                break;
        }

        if (intent != null) {
            intent.putExtra("data", appData);
            startActivity(intent);
            finish();
        }
    }
}
