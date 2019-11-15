package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_profile);
        stub.inflate();

        ImageView profileImageView = findViewById(R.id.profile);
        profileImageView.setOnClickListener(this);

        TextView trainingmodeTextView = findViewById(R.id.training_mode);
        trainingmodeTextView.setOnClickListener(this);

        ImageView trainingmodeImageView = findViewById(R.id.training_mode_image);
        trainingmodeImageView.setOnClickListener(this);

        TextView multiplayerTextView = findViewById(R.id.multiplayer);
        multiplayerTextView.setOnClickListener(this);

        ImageView multiplayerImageView = findViewById(R.id.multiplayer_image);
        multiplayerImageView.setOnClickListener(this);

        TextView myWandTextView = findViewById(R.id.my_wand);
        myWandTextView.setOnClickListener(this);

        ImageView myWandImageView = findViewById(R.id.my_wand_image);
        myWandImageView.setOnClickListener(this);
        /******* Navigation Drawer *******/

        appData = (AppData) getIntent().getSerializableExtra("data");
        TextView yourNameTextView = findViewById(R.id.your_name);
        yourNameTextView.setText(appData.getName_player());
    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Intent intent = null;
        switch (view.getId()) {
            /******* Navigation Drawer *******/
            case R.id.profile:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.training_mode:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.training_mode_image:
                intent = new Intent(this, Trainingmode.class);
                break;
            case R.id.multiplayer:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.multiplayer_image:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.my_wand:
                intent = new Intent(this, MyWand.class);
                break;
            case R.id.my_wand_image:
                intent = new Intent(this, MyWand.class);
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
