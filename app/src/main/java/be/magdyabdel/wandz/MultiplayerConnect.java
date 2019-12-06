package be.magdyabdel.wandz;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Profile profile;
    private TextView connect_server;
    private TextView game_mode;
    private TextView game_status;
    private Boolean busy = false;
    private Boolean connected = false;
    private Boolean joined = false;
    private Boolean master = false;
    private Boolean started = null;

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

        join = findViewById(R.id.join);
        join.setOnClickListener(this);
        leave = findViewById(R.id.leave);
        leave.setOnClickListener(this);
        join.setClickable(false);
        join.setVisibility(GONE);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        connectionManager = new ConnectionManager("51.83.69.116", 6789);

        new ConnectionThread().start();

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
            case R.id.join: {
                if (connected) {
                    if (!joined) {
                        new WriteThread("JOIN " + profile.getName().replace(" ", "_") + " " + profile.getLayoutNumbers()).start();
                        int i = 0;
                        while (!joined && i < 5) {
                            if (!getInternetAccess()) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                            i++;
                        }
                        if (i == 5) {
                            Toast.makeText(MultiplayerConnect.this, "Something went wrong with the connection to the server. Try again!", Toast.LENGTH_SHORT).show();
                        } else if (joined) {
                            join.setClickable(false);
                            join.setVisibility(View.GONE);
                            leave.setVisibility(View.VISIBLE);
                            leave.setClickable(true);
                        }
                    } else {
                        join.setClickable(false);
                        join.setVisibility(View.GONE);
                        leave.setVisibility(View.VISIBLE);
                        leave.setClickable(true);
                    }
                }
            }
                break;
            case R.id.leave: {
                if (connected) {
                    if (joined) {
                        new WriteThread("LEAVE").start();
                        int i = 0;
                        while (joined && i < 5) {
                            if (!getInternetAccess()) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                            i++;
                        }
                        if (i == 5) {
                            Toast.makeText(MultiplayerConnect.this, "Something went wrong with the connection to the server. Try again!", Toast.LENGTH_SHORT).show();
                        } else if (!joined) {
                            join.setClickable(true);
                            join.setVisibility(View.VISIBLE);
                            leave.setVisibility(View.GONE);
                            leave.setClickable(false);
                        }
                    } else {
                        join.setClickable(true);
                        join.setVisibility(View.VISIBLE);
                        leave.setVisibility(View.GONE);
                        leave.setClickable(false);
                    }
                }
            }
                break;
            case R.id.menu: {
                if (connected) {
                    if (joined) {
                        new WriteThread("LEAVE").start();
                        int i = 0;
                        while (joined && i < 5) {
                            if (!getInternetAccess()) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
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
            }
                break;
        }
    }

    private Boolean getInternetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MultiplayerConnect.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });*/
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
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
    protected void onDestroy() {
        super.onDestroy();
        connected = false;
        joined = false;
    }

    private void stopIt() {
        profile.setId(-1);
        connected = false;
        joined = false;
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
                        case "ID":
                            profile.setId(Integer.parseInt(splittedCommand[1]));
                            break;
                        case "SETGAMEMODE":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game_mode.setText(splittedCommand[1] + " MODE");
                                    connectionManager.setGamemode(splittedCommand[1]);
                                }
                            });
                            break;
                        case "STATUS":
                            if (splittedCommand[1].equals("STARTED")) {
                                started = true;
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        started = false;
                                        game_status.setText(splittedCommand[1]);
                                    }
                                });
                            }
                            break;
                        case "PLAYERJOINED":
                            addProfile(Integer.parseInt(splittedCommand[2]), splittedCommand[1], Integer.parseInt(splittedCommand[3]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = true;
                            }
                            break;
                        case "PLAYERLEAVE":
                            removeProfile(Integer.parseInt(splittedCommand[2]));
                            if (Integer.parseInt(splittedCommand[2]) == profile.getId()) {
                                joined = false;
                            }
                            break;
                        case "START":
                            if (joined) {
                                connected = false;
                                joined = false;
                                Intent intent = new Intent(MultiplayerConnect.this, Multiplayer.class);
                                intent.putExtra("profile", profile);
                                intent.putExtra("conman", connectionManager);
                                intent.putExtra("profiles", profiles);
                                intent.putExtra("master", master);
                                startActivity(intent);
                                finish();
                            }
                            break;
                        case "STOP":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game_status.setText("STOP");
                                }
                            });
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

    class ConnectionThread extends Thread {
        ConnectionThread() {
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
            connectionManager.connect();
            busy = false;
            connected = true;
            new ReadThread().start();

            int i = 0;
            while (started == null && i < 5) {
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
                i++;
            }
            if (started) {
                stopIt();
                Intent intent = new Intent(MultiplayerConnect.this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
            } else if (i != 5) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect_server.setVisibility(GONE);
                    }
                });
                if (!joined) {
                    new WriteThread("JOIN " + profile.getName().replace(" ", "_") + " " + profile.getLayoutNumbers()).start();
                    int j = 0;
                    while (!joined && j < 5) {
                        if (!getInternetAccess()) {
                            j = 5;
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        j++;
                    }
                    if (j == 5) {
                        if (connected) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MultiplayerConnect.this, "Something went wrong with the connection to the server. Try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            stopIt();
                            Intent intent = new Intent(MultiplayerConnect.this, Menu.class);
                            intent.putExtra("profile", profile);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                }

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

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            } else {
                if (connected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MultiplayerConnect.this, "Unable to connect to the server.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    stopIt();
                    Intent intent = new Intent(MultiplayerConnect.this, Menu.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}
