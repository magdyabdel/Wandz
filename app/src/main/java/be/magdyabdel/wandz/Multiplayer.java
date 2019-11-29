package be.magdyabdel.wandz;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Iterator;

public class Multiplayer extends AppCompatActivity implements View.OnClickListener {

    private Profile profile;
    private ConnectionManager connectionManager;
    private boolean connected = false;
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
    private Boolean joined = false;
    private TextView notification;

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

        ImageView profile_image_drawer = findViewById(R.id.profile);
        TextView yourNameTextView = findViewById(R.id.your_name);
        Button leaveGame = findViewById(R.id.training_mode);
        leaveGame.setOnClickListener(this);
        leaveGame.setText("Leave The Game");
        Button stop = findViewById(R.id.multiplayer);
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

        if (master) {
            stop.setVisibility(View.VISIBLE);
            stop.setOnClickListener(this);
        }

        connectionManager = (ConnectionManager) getIntent().getSerializableExtra("conman");
        profiles = (ArrayList<Profile>) getIntent().getSerializableExtra("profiles");
        master = (Boolean) getIntent().getSerializableExtra("master");

        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText(profile.getName());

        /**
         * bind to the bluetooth service and send the ID
         **/
        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("hitUpdate")); //broadcast receiver

        health_progressBar = findViewById(R.id.health_progressBar);
        health_progressBar.setProgress(health);
        energy_progressBar = findViewById(R.id.energy_progressBar);
        energy_progressBar.setProgress(power);
        score_value = findViewById(R.id.score_value);
        score_value.setText(score);
        game_mode_value = findViewById(R.id.game_mode_value);
        game_mode_value.setText(connectionManager.getGamemode());
        lastHit = findViewById(R.id.lastHit);
        lastHit.setText("Hit Somebody!");
        lastHitBy = findViewById(R.id.lastHitBy);
        lastHitBy.setText("Not Hitted Yet!");
        notification = findViewById(R.id.notifications);

        Multiplayer.ConnectionThread connectionThread = new Multiplayer.ConnectionThread();
        connectionThread.start();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int gest = intent.getIntExtra("hitCode", 0);
            //cal function with gesture int
            byte spell = (byte) (gest & 0x00FF); //8 LSB's
            byte attackerID = (byte) ((gest >>> 8) & 0x00FF); // 8-16 LSB's

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.training_mode:
                Multiplayer.WriteThread writeThread1 = new Multiplayer.WriteThread("Leave");
                writeThread1.start();
                while (joined) {//TODO:TIMEOUT
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                connected = false;
                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                unbindService(connection); //unbind bluetooth service
                mBound = false;
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.stop:
                if (master) {
                    Multiplayer.WriteThread writeThreadStop = new Multiplayer.WriteThread("STOP");
                    writeThreadStop.start();
                }
                break;

            default:
                break;
        }
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
            case 0:
                score = score + 50;
                break;
            case 1:
                score = score + 100;
                break;
            case 2:
                score = score + 200;
                break;
        }
        score_value.setText(score);
    }

    private void setHealth(int spell) {
        switch (spell) {
            case 0:
                health = health - 100;
                break;
            case 1:
                health = health - 200;
                break;
            case 2:
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

    class ConnectionThread extends Thread {
        ConnectionThread() {
        }

        @Override
        public void run() {

            connected = true;

            Multiplayer.ReadThread readThread = new Multiplayer.ReadThread();
            readThread.start();

            joined = true;

            while (connected) {
                while (busy) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                busy = true;
                connectionManager.sendData("KEEPALIVE");
                busy = false;
                Log.i("servershit", "alive" + profile.getId());

                try {
                    Thread.sleep(5000);
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
                                    notification.setText(splittedCommand[1] + "Has Left The Game!");
                                }
                            });
                            break;
                        case "HIT":
                            if (Integer.parseInt(splittedCommand[1]) == profile.getId()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setScore(Integer.parseInt(splittedCommand[3]));
                                        lastHit.setText("Your Last Hit Is " + getNameById(Integer.parseInt(splittedCommand[2])));
                                    }
                                });
                            }
                            break;
                        case "STOP":
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
