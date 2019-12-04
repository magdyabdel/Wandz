package be.magdyabdel.wandz;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Iterator;

public class Multiplayer extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;
    private ConnectionManager connectionManager;
    private boolean connected = true;
    private Boolean joined = true;
    private boolean busy = false;
    private int score = 0;
    private int power = 1000;
    private int health = 1000;
    private ArrayList<Profile> profiles;
    private ProgressBar health_progressBar;
    private ProgressBar energy_progressBar;
    private TextView score_value;
    private TextView game_mode_value;
    private Boolean master;
    private TextView lastHit;
    private TextView lastHitBy;
    private TextView notification;
    private Button stop;

    BLEService mService;
    boolean mBound = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                int gest = intent.getIntExtra("hitCode", 0);
                byte spell = (byte) (gest & 0x00FF); //8 LSB's
                final byte attackerID = (byte) ((gest >>> 8) & 0x00FF); // 8-16 LSB's
                Log.i("tagshitspell", Integer.toString((int) spell));
                Log.i("tagshitplayer", Integer.toString((int) attackerID));
                if (attackerID != profile.getId()) {
                    sendHit(attackerID, spell);
                    setHealth(spell);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastHitBy.setText(getNameById((int) attackerID));
                        }
                    });
                }
            }
        }
    };
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.sendWizardID(profile.getId());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Force landscape mode on create for this activity
        hideSystemUI();

        connectionManager = (ConnectionManager) getIntent().getSerializableExtra("conman");

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_multiplayer);
        stub.inflate();
        ImageView profile_image_drawer = findViewById(R.id.profile);
        TextView yourNameTextView = findViewById(R.id.your_name);
        Button leaveGame = findViewById(R.id.training_mode);
        leaveGame.setOnClickListener(this);
        leaveGame.setText("Leave The Game");
        stop = findViewById(R.id.multiplayer);
        stop.setText("Stop The Game");
        stop.setVisibility(View.GONE);
        Button myWand = findViewById(R.id.my_wand);
        myWand.setVisibility(View.GONE);
        Button menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);
        /******* Navigation Drawer *******/

        profile = (Profile) getIntent().getSerializableExtra("profile");
        yourNameTextView.setText(profile.getName());
        ImageView profileImageView = findViewById(R.id.profile_image);
        profile.setProfileImage(this, profileImageView);
        profile.setProfileImage(this, profile_image_drawer);
        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText(Integer.toString(profile.getId()));

        health_progressBar = findViewById(R.id.health_progressBar);
        health_progressBar.setProgress(health);
        energy_progressBar = findViewById(R.id.energy_progressBar);
        energy_progressBar.setProgress(power);
        score_value = findViewById(R.id.scorevalue);
        score_value.setText(Integer.toString(score));
        game_mode_value = findViewById(R.id.game_mode_value);
        game_mode_value.setText(connectionManager.getGamemode());
        lastHit = findViewById(R.id.lastHit);
        lastHit.setText("Hit Somebody!");
        lastHitBy = findViewById(R.id.lastHitBy);
        lastHitBy.setText("Not Hitted Yet!");
        notification = findViewById(R.id.notifications);



        profiles = (ArrayList<Profile>) getIntent().getSerializableExtra("profiles");
        master = (Boolean) getIntent().getSerializableExtra("master");

        if (master) {
            stop.setVisibility(View.VISIBLE);
            stop.setOnClickListener(this);
        }

        Multiplayer.ConnectionThread connectionThread = new Multiplayer.ConnectionThread();
        connectionThread.start();

        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("hitUpdate")); //broadcast receiver
    }

    private String getNameById(int id) {
        Iterator iterator = profiles.iterator();
        while (iterator.hasNext()) {
            Profile currentProfile = (Profile) iterator.next();
            if (currentProfile.getId() == id) {
                return currentProfile.getName();
            }
        }
        return "Unknown Player";
    }

    private void setScore(int spell) {
        switch (spell) {
            case 1:
                score = score + 50;
                break;
            case 2:
                score = score + 100;
                break;
            case 3:
                score = score + 200;
                break;
        }
        score_value.setText(Integer.toString(score));
    }

    private void setHealth(int spell) {
        switch (spell) {
            case 1:
                health = health - 100;
                break;
            case 2:
                health = health - 200;
                break;
            case 3:
                health = health - 500;
                break;
        }
        health_progressBar.setProgress(health);
    }

    private void sendHit(int player_id, int spell) {

        Multiplayer.WriteThread writeThreadHit = new Multiplayer.WriteThread("HIT " + player_id + " " + profile.getId() + " " + spell);
        writeThreadHit.start();
    }

    private void removeProfile(int ids) {
        final int id = ids;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Iterator<Profile> iterator = profiles.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    if (iterator.next().getId() == id) {
                        iterator.remove();
                        break;
                    } else {
                        i++;
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            unbindService(connection);
            mBound = false;
        } catch (RuntimeException e) {
        }
        //Toast.makeText(this, "Service Un-Binded", Toast.LENGTH_LONG).show();
    }

    class WriteThread extends Thread {

        String message;

        WriteThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            while (busy) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
            busy = true;
            connectionManager.sendData(message);
            busy = false;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.training_mode:
                if (connected) {
                    if (joined) {
                        new WriteThread("LEAVE").start();
                        int i = 0;
                        while (joined && i < 10) {
                            if (!getInternetAccess()) {
                                break;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            i++;
                        }
                    }
                }

                connected = false;
                joined = false;
                profile.setId(-1);

                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();

                startActivity(intent);
                finish();
                break;
            case R.id.multiplayer:
                if (master) {
                    new Multiplayer.WriteThread("STOP").start();

                }
                break;
        }
    }

    private void addProfile(int id, String name, int layoutNumbers) {

        final String names = name;
        profiles.add(new Profile(id, name, layoutNumbers));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notification.setText(names + " Has Joined The Game!");
            }
        });
    }

    private Boolean getInternetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Multiplayer.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    class ConnectionThread extends Thread {
        ConnectionThread() {
        }

        @Override
        public void run() {

            if (!connected) {
                while (busy) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }

                busy = true;
                int i = 0;
                while (connectionManager.connect() != 0 && i < 20) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    i++;
                    getInternetAccess();
                }
                busy = false;
                if (i == 5) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Multiplayer.this, "You've left the game because of problems with the internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    connected = false;
                    joined = false;
                    profile.setId(-1);
                    Intent intent = new Intent(Multiplayer.this, Menu.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    finish();
                }
            }

            Multiplayer.ReadThread readThread = new Multiplayer.ReadThread();
            readThread.start();



            while (connected) {
                while (busy) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }

                if (!getInternetAccess()) {
                    connected = false;
                    joined = false;
                    new ConnectionThread().start();
                    break;
                }

                busy = true;
                connectionManager.sendData("KEEPALIVE");
                busy = false;

                //Log.i("servershit", "alive" + profile.getId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    class ReadThread extends Thread {
        ReadThread() {
        }

        @Override
        public void run() {
            while (connected) {
                while (busy) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                busy = true;
                ArrayList<String> data = connectionManager.readAllData();
                busy = false;
                Iterator<String> iterator = data.iterator();
                while (iterator.hasNext()) {

                    String command = iterator.next();
                    Log.i("servershit", command);
                    final String[] splittedCommand = command.split(" ");

                    switch (splittedCommand[0]) {
                        case "PLAYERLEAVE":
                            removeProfile(Integer.parseInt(splittedCommand[2]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = false;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notification.setText(splittedCommand[1] + " Has Left The Game!");
                                }
                            });
                            break;
                        case "PlAYERJOIN":
                            addProfile(Integer.parseInt(splittedCommand[2]), splittedCommand[1], Integer.parseInt(splittedCommand[3]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = true;
                            }
                            break;
                        case "HIT":
                            if (Integer.parseInt(splittedCommand[3]) != Integer.parseInt(splittedCommand[2])) {
                                if (Integer.parseInt(splittedCommand[1]) == profile.getId()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setScore(Integer.parseInt(splittedCommand[3]));
                                            lastHit.setText("Your Last Hit Is " + getNameById(Integer.parseInt(splittedCommand[2])));
                                        }
                                    });
                                }
                            }
                            break;
                        case "STOP":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stop.setText("Game Finished!");
                                }
                            });
                            Multiplayer.WriteThread writeThreadLeave = new Multiplayer.WriteThread("LEAVE");
                            writeThreadLeave.start();
                            joined = false;
                            connected = false;
                            Intent intent = new Intent(Multiplayer.this, Menu.class);
                            intent.putExtra("profile", profile);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
