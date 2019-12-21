package be.magdyabdel.wandz;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.System.currentTimeMillis;

public class Multiplayer extends AppCompatActivity implements View.OnClickListener, SensorEventListener, StepListener {

    private Profile profile;
    private ConnectionManager connectionManager;
    private boolean connected = true;
    private Boolean joined = true;
    private boolean busy = false;
    private int score = 0;
    private int power = 1000;
    private int health = 1000;
    private int powerDamage = 200;
    private int powerDamageMyHealth = 150;
    private int powerDamageOtherHealth = 300;
    private int healtSpell1 = 200;
    private int healtSpell2 = 300;
    private int healtSpell3 = 100;
    private int healtSpell4 = 100;
    private ArrayList<Profile> profiles;
    private ProgressBar health_progressBar;
    private ProgressBar energy_progressBar;
    private TextView score_value;
    private TextView game_mode_value;
    private Boolean master;
    private TextView lastHit;
    private ImageView gestureImage1;
    private ImageView gestureImage2;
    private ImageView gestureImage3;
    ColorMatrixColorFilter grayscalefilter;
    private TextView lastHitBy;
    private Button stop;
    private Boolean dead = false;
    public static Boolean shoot = false;
    public static int id = 5;
    private int playersInGame = 0;
    private int playersGotScore = 0;
    private boolean startGetScores = true;
    private MediaPlayer mediaplayer;

    BLEService mService;
    boolean mBound = false;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private int amountsteps;
    private Sensor accel;
    private long lastHitTime;
    private BroadcastReceiver hitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                int gest = intent.getIntExtra("hitCode", 0);
                int spell = (gest & 0x000000FF);
                Log.i("spreuk", "" + spell);
                final int attackerID = (gest & 0x0000FF00) >>> 8;
                Log.i("receivedID", "" + attackerID);
                if (attackerID != profile.getId() && !dead && (currentTimeMillis() > (lastHitTime + 5))) {
                    lastHitTime = currentTimeMillis();
                    Log.i("attackerID", Integer.toString(attackerID));
                    Log.i("profileID", Integer.toString(profile.getId()));
                    setHealth(spell);
                    mService.sendGesture((byte) 100);
                    new HitThread(getProfileById(attackerID)).start();
                    if (health <= 0) {
                        mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.gameover);
                        mediaplayer.start();
                        sendHit(attackerID, spell);
                        new WriteThread("DEAD " + profile.getId() + " " + attackerID + " " + spell).start();
                        dead = true;
                    } else {
                        mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.punch);
                        mediaplayer.start();
                        sendHit(attackerID, spell);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lastHitBy.setText("Last hit by " + getNameById(attackerID));
                        }
                    });
                }
            }
        }
    };

    private BroadcastReceiver gestureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                int gest = intent.getIntExtra("gesture", 0);
                Log.i("testje", "" + gest);
                if (gest == 100) { //spell is fired
                    mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.spell1);
                    mediaplayer.start();
                } else if (gest != 0) {
                    Log.i("hier?", "hallo");
                    shoot = false;
                    switch (gest) {
                        case 1:
                            if (power > powerDamage) {
                                shoot = true;
                                shoot(gest);
                            }
                            break;
                        case 2:
                            if (power > powerDamageMyHealth) {
                                shoot = true;
                                shoot(gest);
                            }
                            break;
                        case 3:
                            if (power > powerDamageOtherHealth) {
                                shoot = true;
                                shoot(gest);
                            }
                            break;
                    }
                }
            }
        }
    };

    private void shoot(int gest) {
        mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.goodgesture);
        mediaplayer.start();
        setPower(gest);
        mService.sendGesture((byte) gest);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.i("send ID", "send ID!!!!!!!");
            mService.sendWizardID(profile.getId());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void addOwnScore(int spell) {
        int addscore = 0;
        switch (spell) {
            case 1:
                addscore = abs(healtSpell1);
                break;
            case 2:
                addscore = abs(healtSpell2);
                break;
            case 3:
                addscore = abs(healtSpell3);
                break;
        }
        Log.i("addscore", " " + addscore);
        score += addscore;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                score_value.setText(Integer.toString(score));
            }
        });
    }

    private void setScore(int score, int ID) {

        Log.i("setscore", " " + score);
        getProfileById(ID).setScore(score);
    }

    private void setHealth(int spell) {
        switch (spell) {
            case 1:
                health -= healtSpell1;
                break;
            case 2:
                health -= healtSpell2;
                break;
            case 3:
                health -= healtSpell3;
                if (health <= 0 && !dead) {
                    mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.gameover);
                    mediaplayer.start();
                    new WriteThread("DEAD " + profile.getId() + " " + profile.getId() + " " + 4).start();
                    dead = true;
                }
                break;
            case 4:
                health += healtSpell4;
                if (health > 1000) {
                    health = 1000;
                }
                break;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                health_progressBar.setProgress(health);
            }
        });

    }

    private void setPower(int spell) {
        switch (spell) {
            case 1:
                power -= powerDamage;
                break;
            case 2:
                power -= powerDamageMyHealth;
                setHealth(3);
                break;
            case 3:
                power -= powerDamageOtherHealth;
                break;
        }
        if (power < 0) {
            power = 0;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                energy_progressBar.setProgress(power);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(Multiplayer.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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
        stop = findViewById(R.id.stop_game);
        stop.setText("Stop The Game");
        stop.setVisibility(View.GONE);
        /******* Navigation Drawer *******/

        ImageView navdraw_button = findViewById(R.id.navdraw_button);
        navdraw_button.setOnClickListener(this);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        id = profile.getId();
        yourNameTextView.setText(profile.getName());
        ImageView profileImageView = findViewById(R.id.profile_image);
        profile.setProfileImage(this, profileImageView);
        profile.setProfileImage(this, profile_image_drawer);
        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText(profile.getName());

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
        lastHitBy.setText("Not Hit Yet!");

        gestureImage1 = findViewById(R.id.gesture_1);
        gestureImage2 = findViewById(R.id.gesture_2);
        gestureImage3 = findViewById(R.id.gesture_3);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        grayscalefilter = new ColorMatrixColorFilter(matrix);

        profiles = (ArrayList<Profile>) getIntent().getSerializableExtra("profiles");
        playersInGame = profiles.size();
        master = (Boolean) getIntent().getSerializableExtra("master");

        if (master) {
            stop.setVisibility(View.VISIBLE);
            stop.setOnClickListener(this);
        } else {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
            }
        }

        power = 1000;
        new ConnectionThread().start();

        mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.countdown);
        mediaplayer.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        new HealthThread().start();

        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(hitReceiver, new IntentFilter("hitUpdate")); //broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(gestureReceiver, new IntentFilter("GestureUpdate"));
        lastHitTime = currentTimeMillis();
        amountsteps = 0;
        Log.i("amount profiles", "" + profiles.size());

        ConstraintLayout background = findViewById(R.id.hityoulayout);
        background.setVisibility(View.INVISIBLE);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    public void step(long timeNs) {
        power += 25;
        amountsteps++;
    }

    private Profile getProfileById(int id) {
        Iterator iterator = profiles.iterator();
        while (iterator.hasNext()) {
            Profile currentProfile = (Profile) iterator.next();
            Log.i("ID's", "" + currentProfile.getId() + " " + id);
            if (currentProfile.getId() == id) {
                return currentProfile;
            }
        }
        return null;
    }

    private String getNameById(int id) {
        try {
            return getProfileById(id).getName();
        } catch (NullPointerException e) {
            return "Unknown Player";
        }
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
        stopIt();
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

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.navdraw_button:
                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if(!navDrawer.isDrawerOpen(Gravity.START)) navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);

                break;
            case R.id.training_mode:
                if (connected) {
                    if (joined) {
                        new WriteThread("LEAVE").start();
                        new WriteThread("DEAD " + profile.getId() + " " + profile.getId()).start();
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
                stopIt();
                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.stop_game:
                if (master) {
                    new Multiplayer.WriteThread("STOP").start();
                }
                break;
        }
    }

    private void stopIt() {
        connected = false;
        joined = false;
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(gestureReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(hitReceiver);
            unbindService(connection);
            mBound = false;
        } catch (RuntimeException e) {
        }
    }

    private void addProfile(int id, String name, int layoutNumbers) {

        final String names = name;
        profiles.add(new Profile(id, name, layoutNumbers));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Multiplayer.this, names + " Has Joined The Game!", Toast.LENGTH_SHORT).show();
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

    public void releasemediaplayer() {
        mediaplayer.release();
        mediaplayer = null;
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
                if (i == 10) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Multiplayer.this, "You've left the game because of problems with the internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    stopIt();
                    Intent intent = new Intent(Multiplayer.this, Menu.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    finish();
                }
            }
            new ReadThread().start();
            new PowerThread().start();

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
                            playersInGame--;
                            removeProfile(Integer.parseInt(splittedCommand[2]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = false;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Multiplayer.this, splittedCommand[1] + " Has Left The Game!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case "PlAYERJOIN":
                            playersInGame++;
                            addProfile(Integer.parseInt(splittedCommand[2]), splittedCommand[1], Integer.parseInt(splittedCommand[3]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = true;
                            }
                            break;
                        case "HIT":
                            Log.i("HIT", "" + Integer.parseInt(splittedCommand[1]) + Integer.parseInt(splittedCommand[2]) + Integer.parseInt(splittedCommand[3]));
                            if (Integer.parseInt(splittedCommand[1]) == profile.getId()) {
                                addOwnScore(Integer.parseInt(splittedCommand[3]));
                                mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.welldone);
                                mediaplayer.start();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastHit.setText("Your Last Hit Is " + getNameById(Integer.parseInt(splittedCommand[2])));
                                        if (Integer.parseInt(splittedCommand[3]) == 3) {
                                            setHealth(4);
                                        }
                                    }
                                });
                            }
                            break;
                        case "SCORE":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stop.setText("Game Finished!");
                                }
                            });
                            if (startGetScores) {
                                startGetScores = false;
                                playersGotScore = profiles.size();
                            }
                            Log.i("received", "" + Integer.parseInt(splittedCommand[1]));
                            playersGotScore--;
                            setScore(Integer.parseInt(splittedCommand[2]), Integer.parseInt(splittedCommand[1]));
                            if (playersGotScore <= 1) {
                                stopIt();
                                Intent intent2 = new Intent(Multiplayer.this, Gameover.class);
                                intent2.putExtra("profile", profile);
                                intent2.putExtra("profiles", profiles);
                                intent2.putExtra("master", master);
                                intent2.putExtra("conman", connectionManager);
                                startActivity(intent2);
                                finish();
                            }
                            break;
                        case "DEAD":
                            if (Integer.parseInt(splittedCommand[1]) == profile.getId() && playersInGame > 1) {
                                dead = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastHit.setText("Game Over! You're Dead!");
                                        Toast.makeText(Multiplayer.this, "You have lost, wait for the end of the game!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                if (Integer.parseInt(splittedCommand[1]) == profile.getId() && !dead) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            lastHit.setText("You've killed " + getNameById(Integer.parseInt(splittedCommand[2])));
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Multiplayer.this, getNameById(Integer.parseInt(splittedCommand[2])) + " Has died!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            playersInGame--;
                            if (playersInGame <= 1) {
                                if (!dead) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            lastHit.setText("You are the only surviver!");
                                            lastHitBy.setText("You are the only survivor                                                                              !");
                                        }
                                    });
                                }
                                if (master) {
                                    new Multiplayer.WriteThread("STOP").start();
                                }
                            }

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

    class PowerThread extends Thread {

        PowerThread() {
        }

        @Override
        public void run() {
            while (connected && joined && !dead) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energy_progressBar.getProgress() < 1000) {
                            power++;
                            energy_progressBar.setProgress(power);
                        }
                        if (power < powerDamage) {
                            gestureImage1.setColorFilter(grayscalefilter);
                            gestureImage2.setColorFilter(grayscalefilter);
                            gestureImage3.setColorFilter(grayscalefilter);
                        } else if (power < powerDamageMyHealth) {
                            gestureImage1.setColorFilter(null);
                            gestureImage2.setColorFilter(grayscalefilter);
                            gestureImage3.setColorFilter(grayscalefilter);
                        } else if (power < powerDamageOtherHealth) {
                            gestureImage1.setColorFilter(null);
                            gestureImage2.setColorFilter(null);
                            gestureImage3.setColorFilter(grayscalefilter);
                        } else {
                            gestureImage1.setColorFilter(null);
                            gestureImage2.setColorFilter(null);
                            gestureImage3.setColorFilter(null);
                        }
                    }
                });
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                }

            }
            power = 0;
        }
    }

    class HealthThread extends Thread {

        HealthThread() {
        }

        @Override
        public void run() {
            while (connected && joined && !dead) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                if (amountsteps < 2) {
                    health -= 40;
                    amountsteps = 0;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        health_progressBar.setProgress(health);
                        if (health <= 0 & !dead) {
                            mediaplayer = MediaPlayer.create(Multiplayer.this, R.raw.gameover);
                            mediaplayer.start();
                            new WriteThread("DEAD " + profile.getId() + " " + profile.getId() + " " + 5).start();
                            dead=true;
                        }
                    }
                });
            }
        }
    }

    class HitThread extends Thread {

        Profile hitProfile;

        HitThread(Profile hitProfile) {
            this.hitProfile = hitProfile;
        }

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConstraintLayout background = findViewById(R.id.hityoulayout);
                    background.setVisibility(View.VISIBLE);

                    ImageView imageView = findViewById(R.id.profile_hit);
                    hitProfile.setProfileImage(Multiplayer.this, imageView);

                    TextView textView = findViewById(R.id.text_hit);
                    textView.setText("You're hit by " + hitProfile.getName());
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConstraintLayout background = findViewById(R.id.hityoulayout);
                    background.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}

