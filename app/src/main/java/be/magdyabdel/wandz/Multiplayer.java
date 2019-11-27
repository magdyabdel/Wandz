package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Iterator;

public class Multiplayer extends AppCompatActivity implements View.OnClickListener {

    private AppData appData;
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

        appData = (AppData) getIntent().getSerializableExtra("data");
        yourNameTextView.setText(appData.getName_player());
        appData.setProfileImage(this, profileImageView, -1);

        ImageView profile_image = findViewById(R.id.profile_image);
        appData.setProfileImage(this, profile_image, -1);
        TextView multiplayer_name = findViewById(R.id.multiplayer_name);
        multiplayer_name.setText((appData.getName_player()));

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
                sendTest();
                break;
            case R.id.join:

                gameMethod();
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
            intent.putExtra("data", appData);
            startActivity(intent);
            finish();
        }
    }

    private void sendTest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (connect) {
                    connectionManager.sendData("JOIN " + appData.getName_player());
                }
            }
        });
        thread.start();
    }

    private void gameMethod() {

        Button leave = findViewById(R.id.leave);
        leave.setClickable(true);
        leave.setVisibility(View.VISIBLE);
        Button join = findViewById(R.id.join);
        join.setClickable(false);
        join.setVisibility(View.GONE);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionManager = new ConnectionManager("51.83.69.116", 6789);
                    while (connectionManager.connect() != 0) {
                        //TODO: implement timeout
                    }
                    connect = true;
                    Log.i("servershit", "connected");
                    Looper.prepare();
                    while (connect) {
                        final ArrayList<String> data = connectionManager.readAllData();
                        Iterator iterator = data.iterator();
                        while (iterator.hasNext()) {

                            final String command = (String) iterator.next();
                            Log.i("servershit", command);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView gamemode = findViewById(R.id.game_mode_value);
                                    gamemode.setText(command);
                                }
                            });

                        }

                        Log.i("servershit", "KEEPALIVE");
                        connectionManager.sendData("KEEPALIVE");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }
}
