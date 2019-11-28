package be.magdyabdel.wandz;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Trainingmode extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private int[] gestureImages = new int[]{R.drawable.ic_gesture_horizontal_right, R.drawable.ic_gesture_vertical_up, R.drawable.ic_gesture_round};
    private int currentGestureImage = 0;
    Vibrator v;


    private Profile profile;

    BLEService mService;
    boolean mBound = false;

    ImageView bad;
    ImageView good;

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
        stub.setLayoutResource(R.layout.activity_training_mode);
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
        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);
        /******* Navigation Drawer *******/

        Button next = findViewById(R.id.next);
        next.setOnClickListener(this);

        Button previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);

        imageView = findViewById(R.id.view_gestures);
        imageView.setImageResource(R.drawable.ic_gesture_horizontal_right);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        yourNameTextView.setText(profile.getName());
        profile.setProfileImage(this, profileImageView);

        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("GestureUpdate")); //broadcast receiver
        v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);

        good = findViewById(R.id.groen);
        good.setVisibility(View.GONE);
        bad = findViewById(R.id.rood);
        bad.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Intent intent = null;
        switch (view.getId()) {

            /******* Navigation Drawer *******/
            case R.id.profile:
            case R.id.your_name:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.training_mode:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.multiplayer:
                intent = new Intent(this, MultiplayerConnect.class);
                break;
            case R.id.my_wand:
                intent = new Intent(this, MyWand.class);
                break;
            /******* Navigation Drawer *******/

            case R.id.previous:
                changeGestureImage(1);
                break;
            case R.id.next:
                changeGestureImage(-1);
                break;
            case R.id.menu:
                intent = new Intent(this, Menu.class);
                break;

            default:
                break;
        }

        if (intent != null) {
            intent.putExtra("profile", profile);
            unbindService(connection); //unbind bluetooth service
            mBound = false;
            startActivity(intent);
            finish();
        }
    }

    public void changeGestureImage(int direction) {
        if (direction == 1) {
            currentGestureImage++;
            if (currentGestureImage >= gestureImages.length) {
                currentGestureImage = 0;
            }
        }
        if (direction == -1) {
            currentGestureImage--;
            if (currentGestureImage < 0) {
                currentGestureImage = gestureImages.length - 1;
            }
        } else {
        }
        imageView.setImageResource(gestureImages[currentGestureImage]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.i("yess", "jaaaaaaa");
            Byte gest = intent.getByteExtra("gesture",(byte)0);
            if(gest == (currentGestureImage+1)){
                good.setVisibility(View.VISIBLE);
                bad.setVisibility(View.INVISIBLE);
            }
            else{
                // Vibrate for 500 milliseconds
                bad.setVisibility(View.VISIBLE);
                good.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                //Toast.makeText(getApplicationContext(), gest, Toast.LENGTH_SHORT).show();
            }
        }
    };

}
