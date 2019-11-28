package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

import static android.view.View.GONE;

public class MasterConnect extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Profile> profiles;
    private ProfileAdapter adapter;
    private Button start;
    private Button stop;
    private ConnectionManager connectionManager;
    private Profile profile;
    private TextView connect_server;
    private TextView game_mode;
    private TextView game_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_connect);

        profiles = new ArrayList<>();
        RecyclerView recyclerview = findViewById(R.id.recycleViewer);
        adapter = new ProfileAdapter(profiles, this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        connect_server = findViewById(R.id.connect_server);
        game_mode = findViewById(R.id.game_mode_value);
        game_status = findViewById(R.id.game_status);

        start = findViewById(R.id.join);
        start.setOnClickListener(this);
        stop = findViewById(R.id.leave);
        stop.setOnClickListener(this);
        stop.setClickable(false);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        connectionManager = new ConnectionManager("51.83.69.116", 6789);

        ConnectionThread connectionThread = new ConnectionThread();
        connectionThread.start();

        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);

    }

    private void addProfile(int ids, String names, int layoutNumberss) {

        final int id = ids;
        final String name = names;
        final int layoutNumbers = layoutNumberss;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                profiles.add(new Profile(id, name, layoutNumbers));
                adapter.notifyDataSetChanged();
            }
        });
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
                        adapter.notifyItemRemoved(i);
                        adapter.notifyItemRangeChanged(i, profiles.size());
                        break;
                    } else {
                        i++;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (connect_server.getVisibility() == GONE) {
                    start.setClickable(false);
                    start.setVisibility(View.GONE);
                    stop.setVisibility(View.VISIBLE);
                    stop.setClickable(true);
                }
                break;
            case R.id.stop:
                if (connectionManager.getJoined()) {
                    WriteThread writeThread2 = new WriteThread("STOP");
                    writeThread2.start();
                }
                start.setClickable(true);
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(GONE);
                stop.setClickable(false);

                break;
            case R.id.menu:
                if (connectionManager.getJoined()) {
                    WriteThread writeThread3 = new WriteThread("LEAVE");
                    writeThread3.start();
                }
                while (connectionManager.getJoined()) { //TODO:Timeout
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                profile.setId(-1);
                connectionManager.setConnected(false);

                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
        }
    }

    class ReadThread extends Thread {
        ReadThread() {
        }

        @Override
        public void run() {
            while (connectionManager.getConnected()) {
                ArrayList<String> data = connectionManager.readAllData();
                Iterator<String> iterator = data.iterator();
                while (iterator.hasNext()) {

                    String command = iterator.next();
                    Log.i("servershit", command);
                    final String[] splittedCommand = command.split(" ");

                    switch (splittedCommand[0]) {
                        case "ID":
                            profile.setId(Integer.parseInt(splittedCommand[1]));
                            break;
                        case "SETGAMEMODE":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game_mode.setText(splittedCommand[1] + " MODE");
                                }
                            });
                            break;
                        case "STATUS":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game_status.setText(splittedCommand[1]);
                                }
                            });
                            break;
                        case "PLAYERJOINED":
                            addProfile(Integer.parseInt(splittedCommand[2]), splittedCommand[1], Integer.parseInt(splittedCommand[3]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                connectionManager.setJoined(true);
                            }
                            break;
                        case "PLAYERLEAVE":
                            removeProfile(Integer.parseInt(splittedCommand[2]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                connectionManager.setJoined(false);
                            }
                            break;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    class WriteThread extends Thread {

        String message;

        WriteThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            connectionManager.sendData(message);
        }
    }

    class ConnectionThread extends Thread {
        ConnectionThread() {
        }

        @Override
        public void run() {

            while (connectionManager.connect() != 0) { //TODO:timout
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connect_server.setVisibility(View.VISIBLE);
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connect_server.setVisibility(GONE);
                }
            });

            connectionManager.setConnected(true);

            ReadThread readThread = new ReadThread();
            readThread.start();

            if (!connectionManager.getJoined()) {
                WriteThread writeThread = new WriteThread("JOIN " + profile.getName().replace(" ", "_") + " " + profile.getLayoutNumbers());
                writeThread.start();
            }

            while (connectionManager.getConnected()) {
                connectionManager.sendData("KEEPALIVE");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
