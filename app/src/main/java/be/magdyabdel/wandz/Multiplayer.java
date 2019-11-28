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

public class Multiplayer extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;
    private DrawerLayout drawer;
    private Button utilityButton;
    private ConnectionManager connectionManager;
    private boolean connect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_multiplayer);
        stub.inflate();

        ImageView profileImageView = findViewById(R.id.profile_image);
        TextView yourNameTextView = findViewById(R.id.your_name);
        Button leaveGame = findViewById(R.id.training_mode);
        leaveGame.setOnClickListener(this);
        leaveGame.setText("Leave The Game");
        Button multiplayer = findViewById(R.id.multiplayer);
        multiplayer.setVisibility(View.GONE);
        Button myWand = findViewById(R.id.my_wand);
        myWand.setVisibility(View.GONE);
        Button menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);
        /******* Navigation Drawer *******/

        profile = (Profile) getIntent().getSerializableExtra("profile");
        yourNameTextView.setText(profile.getName());
        profile.setProfileImage(this, profileImageView);

        ImageView profile_image = findViewById(R.id.profile_image);
        profile.setProfileImage(this, profile_image);
        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText(profile.getName());

        drawer = findViewById(R.id.drawer_layout);
        utilityButton = findViewById(R.id.utility_button);
        utilityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        drawer = findViewById(R.id.drawer_layout);
        utilityButton = findViewById(R.id.utility_button);

        Intent intent = null;
        switch (view.getId()) {
            /******* Navigation Drawer *******/
            case R.id.profile:
            case R.id.your_name:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.training_mode:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.multiplayer:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.my_wand:
                intent = new Intent(this, MyWand.class);
                break;
            /******* Navigation Drawer *******/

            case R.id.utility_button:
                break;
            default:
                break;
        }

        if (intent != null) {
            intent.putExtra("profile", profile);
            startActivity(intent);
            finish();
        }
    }
}
