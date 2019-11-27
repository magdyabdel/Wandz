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

public class MultiplayerConnect extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Profile> profiles;
    private ProfileAdapter adapter;
    private Button join;
    private Button leave;
    private ConnectionManager connectionManager;
    private Boolean connect = false;
    private Profile profile;
    private TextView connect_server;

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

        join = findViewById(R.id.join);
        join.setOnClickListener(this);
        leave = findViewById(R.id.leave);
        leave.setOnClickListener(this);
        leave.setClickable(false);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        connectionManager = new ConnectionManager("51.83.69.116", 6789);
        connect = true;
        try {
            ConnectionThread connectionThread = new ConnectionThread();
            connectionThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void addProfile(int id, String name, int layoutNumbers) {
        profiles.add(new Profile(id, name, layoutNumbers));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            case R.id.join:
                if (connect_server.getVisibility() == GONE) {
                    join.setClickable(false);
                    join.setVisibility(View.GONE);
                    leave.setVisibility(View.VISIBLE);
                    leave.setClickable(true);

                    WriteThread writeThread = new WriteThread("JOIN " + profile.getName().replace(" ", "_") + " " + profile.getLayoutNumbers());
                    writeThread.start();
                }

                break;
            case R.id.leave:
                connect = false;
                WriteThread writeThread = new WriteThread("LEAVE");
                writeThread.start();
                profile.setId(-1);
                Intent intent = new Intent(this, Trainingmode.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
        }
    }


    class ReadThread extends Thread {

        ReadThread() throws InterruptedException {
        }

        @Override
        public void run() {
            while (connect) {
                ArrayList<String> data = connectionManager.readAllData();
                Iterator<String> iterator = data.iterator();
                while (iterator.hasNext()) {

                    String command = iterator.next();
                    Log.i("servershit", command);
                    String[] splittedCommand = command.split(" ");

                    if (splittedCommand[0].equals("ID")) {
                        profile.setId(Integer.parseInt(splittedCommand[1]));
                    }
                    if (splittedCommand[0].equals("PLAYERJOINED")) {
                        Log.i("servershit", "Join" + splittedCommand[1]);
                        addProfile(Integer.parseInt(splittedCommand[2]), splittedCommand[1], Integer.parseInt(splittedCommand[3]));
                    }
                    if (splittedCommand[0].equals("PLAYERLEAVE")) {
                        if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                            connect = false;
                        } else {
                            removeProfile(Integer.parseInt(splittedCommand[2]));
                        }
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

        ConnectionThread() throws InterruptedException {
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

            try {
                ReadThread readThread = new ReadThread();
                readThread.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (connect) {
                connectionManager.sendData("KEEPALIVE");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
