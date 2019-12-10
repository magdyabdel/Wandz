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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LearnTheGestures extends AppCompatActivity implements View.OnClickListener {

    private int[] imagesRoundAnim = new int[]{R.drawable.ic_round1, R.drawable.ic_round2, R.drawable.ic_round3, R.drawable.ic_round4, R.drawable.ic_round5, R.drawable.ic_round6};
    private int[] imagesInfinityAnim = new int[]{R.drawable.ic_infinity1, R.drawable.ic_infinity2, R.drawable.ic_infinity3, R.drawable.ic_infinity4, R.drawable.ic_infinity5, R.drawable.ic_infinity6, R.drawable.ic_infinity7};
    private int[] imagesSpinAnim = new int[]{R.drawable.ic_spin1, R.drawable.ic_spin2, R.drawable.ic_spin3, R.drawable.ic_spin4, R.drawable.ic_spin5, R.drawable.ic_spin6};

    Vibrator v;

    private int gesture = 0;
    private Button showAgain;
    private Button next;
    private TextView well_done;

    private Profile profile;
    private ImageView gestureImage;

    private BLEService mService;
    private boolean mBound = false;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                int gest = intent.getIntExtra("gesture", 0);
                if (gest == (gesture + 1)) {
                    mService.sendGesture((byte) gest);
                    setCorrectOrNot(true);
                } else {
                    setCorrectOrNot(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(500);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_the_gestures);

        showAgain = findViewById(R.id.show_again);
        showAgain.setOnClickListener(this);

        next = findViewById(R.id.next);
        next.setOnClickListener(this);

        well_done = findViewById(R.id.well_done);
        well_done.setText("");

        LearnTheGestures.AnimThread animThread = new LearnTheGestures.AnimThread(gesture);
        animThread.start();

        profile = (Profile) getIntent().getSerializableExtra("profile");
        gestureImage = findViewById(R.id.gesture_image);

        Intent intent1 = new Intent(this, BLEService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("GestureUpdate")); //broadcast receiver
        v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.show_again:
                LearnTheGestures.AnimThread animThread2 = new LearnTheGestures.AnimThread(gesture);
                animThread2.start();
                break;
            case R.id.next:
                gesture++;
                if (gesture == 2) {
                    next.setText("Done");
                }
                if (gesture == 3) {
                    try {
                        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                        unbindService(connection);
                        mBound = false;
                    } catch (RuntimeException e) {
                    }
                    Intent intent = new Intent(this, Menu.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                } else {
                    LearnTheGestures.AnimThread animThread = new LearnTheGestures.AnimThread(gesture);
                    animThread.start();
                }
                break;
        }
    }

    private void setCorrectOrNot(Boolean correctOrNot) {

        final Boolean correct = correctOrNot;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (correct) {
                    well_done.setText("Well Done!");
                } else {
                    well_done.setText("Try Again!");
                }
                DelayThread delayThread = new DelayThread();
                delayThread.start();
            }
        });
    }

    class DelayThread extends Thread {

        DelayThread() {
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    well_done.setText("");
                }
            });
        }
    }

    class AnimThread extends Thread {

        int gesture;

        AnimThread(int gesture) {
            this.gesture = gesture;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setClickable(false);
                    showAgain.setClickable(false);
                }
            });

            final int[] array;

            switch (gesture) {
                case 0:
                    array = imagesRoundAnim;
                    break;
                case 1:
                    array = imagesInfinityAnim;
                    break;
                case 2:
                    array = imagesSpinAnim;
                    break;
                default:
                    array = new int[0];
                    break;
            }

            for (int i = 0; i < array.length; i++) {
                final int j = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gestureImage.setImageResource(array[j]);
                    }
                });
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setClickable(true);
                    showAgain.setClickable(true);
                }
            });
        }
    }
}
