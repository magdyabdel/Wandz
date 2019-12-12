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
    private Boolean busy = false;
    private Boolean connected = false;
    private Boolean joined = false;
    private Boolean started = false;
    private String[] modes = new String[]{"ELIMINATION", "TEAMELIMINATION", "CORRUPTEDWIZARD", "SPIRALDEFENSE"};
    private int mode = 0;
    private boolean master = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_connect);

        profiles = new ArrayList<>();
        RecyclerView recyclerview = findViewById(R.id.score_recycleViewer);
        adapter = new ProfileAdapter(profiles, this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        connect_server = findViewById(R.id.connect_server);
        game_mode = findViewById(R.id.game_mode_value);
        game_status = findViewById(R.id.game_status);

        start = findViewById(R.id.start);
        start.setOnClickListener(this);
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(this);
        stop.setClickable(false);

        profile = (Profile) getIntent().getSerializableExtra("profile");
        connectionManager = new ConnectionManager("51.83.69.116", 6789);

        ConnectionThread connectionThread = new ConnectionThread();
        connectionThread.start();

        Button menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);
        Button previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);
        Button enter = findViewById(R.id.enter);
        enter.setOnClickListener(this);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(this);
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
        int i = 0;
        switch (view.getId()) {
            case R.id.start:

                while (!joined && i < 10) {
                    if (!getInternetAccess()) {
                        i = 10;
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    i++;
                }
                if (i == 10) {
                    Toast.makeText(MasterConnect.this, "Something went wrong with the connection to the server. Game not started!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!started) {
                        new WriteThread("START").start();

                        i = 0;
                        while (!started && i < 10) {
                            if (!getInternetAccess()) {
                                i = 10;
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                            i++;
                        }
                        if (i == 10) {
                            Toast.makeText(MasterConnect.this, "Something went wrong with the connection to the server. Game not started!", Toast.LENGTH_SHORT).show();
                        } else {
                            start.setClickable(false);
                            start.setVisibility(View.GONE);
                            stop.setVisibility(View.VISIBLE);
                            stop.setClickable(true);
                        }
                    }
                }
                break;
            case R.id.stop:
                if (started) {
                    WriteThread writeThread3 = new WriteThread("STOP");
                    writeThread3.start();
                    i = 0;
                    while (started && i < 10) {
                        if (!getInternetAccess()) {
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        i++;
                    }
                    if (i == 10) {
                        Toast.makeText(MasterConnect.this, "Something went wrong with the connection to the server. Game not stopped!", Toast.LENGTH_SHORT).show();
                    } else {
                        start.setClickable(true);
                        start.setVisibility(View.VISIBLE);
                        stop.setVisibility(GONE);
                        stop.setClickable(false);
                    }
                }
                break;
            case R.id.menu:

                if (started) {
                    WriteThread writeThread4 = new WriteThread("STOP");
                    writeThread4.start();
                }

                i = 0;
                while (started && i < 10) {
                    if (!getInternetAccess()) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    i++;
                }
                if (i == 10) {
                    Toast.makeText(MasterConnect.this, "Something went wrong with the connection to the server. Game not stopped!", Toast.LENGTH_SHORT).show();
                }
                if (connected) {
                    if (joined) {
                        new WriteThread("LEAVE").start();
                        i = 0;
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

                joined = false;
                connected = false;
                profile.setId(-1);

                Intent intent = new Intent(this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
                break;
            case R.id.previous:
                mode--;
                if (mode < 0) {
                    mode = modes.length - 1;
                }
//                game_mode.setText(modes[mode] + "\n(submit To Set)");
                game_mode.setText(modes[mode]);
                game_mode.setTextColor(getResources().getColor(R.color.start_grad_46));
                break;
            case R.id.next:
                mode++;
                if (mode >= modes.length) {
                    mode = 0;
                }
                game_mode.setText(modes[mode]);
                game_mode.setTextColor(getResources().getColor(R.color.start_grad_46));
//                game_mode.setText(modes[mode] + "\n(submit To Set)");
//                game_mode.setTextSize(20);
                break;
            case R.id.enter:
                WriteThread writeThread6 = new WriteThread("SETGAMEMODE " + modes[mode]);
                writeThread6.start();//TODO: check mode & Timeout
                break;
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
                        case "ID":
                            profile.setId(Integer.parseInt(splittedCommand[1]));
                            break;
                        case "SETGAMEMODE":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    game_mode.setText(splittedCommand[1]);
//                                    game_mode.setText(splittedCommand[1] + " MODE");
                                    game_mode.setTextColor(getResources().getColor(R.color.wizardGreen));
                                    connectionManager.setGamemode(splittedCommand[1]);
                                }
                            });
                            break;
                        case "STATUS":
                            if (splittedCommand[1].equals("STARTED")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        started = true;
                                        start.setClickable(false);
                                        start.setVisibility(View.GONE);
                                        stop.setVisibility(View.VISIBLE);
                                        stop.setClickable(true);
                                    }
                                });
                            } else if (splittedCommand[1].equals("NOTSTARTED")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        started = false;
                                        start.setClickable(true);
                                        start.setVisibility(View.VISIBLE);
                                        stop.setVisibility(GONE);
                                        stop.setClickable(false);
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
                            started = true;
                            connected = false;
                            Intent intent = new Intent(MasterConnect.this, Multiplayer.class);
                            intent.putExtra("profile", profile);
                            intent.putExtra("conman", connectionManager);
                            intent.putExtra("profiles", profiles);
                            intent.putExtra("master", master);
                            startActivity(intent);
                            finish();
                            break;
                        case "STOP":
                            started = false;
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

    private Boolean getInternetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MasterConnect.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connected = false;
        joined = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        connected = false;
        joined = false;
        Intent intent = new Intent(this, BLEService.class);
        stopService(intent);
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
            int i = 0;
            while (connectionManager.connect() != 0 && i < 10) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect_server.setVisibility(View.VISIBLE);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                i++;
            }
            busy = false;

            if (i != 10) {
                connected = true;
                new ReadThread().start();

                if (!joined) {
                    new WriteThread("JOIN " + profile.getName().replace(" ", "_") + "(master) " + profile.getLayoutNumbers()).start();
                    int j = 0;
                    while (!joined && j < 10) {
                        if (!getInternetAccess()) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        j++;
                    }
                    if (j == 10) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MasterConnect.this, "Something went wrong with the connection to the server. Try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        connected = false;
                        joined = false;
                        profile.setId(-1);
                        Intent intent = new Intent(MasterConnect.this, Menu.class);
                        intent.putExtra("profile", profile);
                        startActivity(intent);
                        finish();
                    }
                } else {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect_server.setVisibility(GONE);
                    }
                });

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MasterConnect.this, "Unable to connect to the server.", Toast.LENGTH_SHORT).show();
                    }
                });
                connected = false;
                joined = false;
                profile.setId(-1);
                Intent intent = new Intent(MasterConnect.this, Menu.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
            }
        }
    }
}
