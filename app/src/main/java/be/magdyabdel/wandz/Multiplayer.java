package be.magdyabdel.wandz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

    /**
     *  Variables for bluetooth binding
     **/
    BLEService mService;
    boolean mBound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.sendWizardID(profile.getId()); //Send the ID of the player to the wand
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_multiplayer);
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
        Button leave = findViewById(R.id.leave);
        leave.setOnClickListener(this);
        leave.setClickable(false);
        Button join = findViewById(R.id.join);
        join.setOnClickListener(this);
        join.setVisibility(View.VISIBLE);
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

        /**
         * bind to the bluetooth service and send the ID
         **/
        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
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
            case R.id.join:
                break;
            case R.id.leave:
                connect = false;
                Button leave = findViewById(R.id.leave);
                leave.setClickable(false);
                leave.setVisibility(View.GONE);
                Button join = findViewById(R.id.join);
                join.setClickable(true);
                join.setVisibility(View.VISIBLE);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
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
