package be.magdyabdel.wandz;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
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
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class ChooseYourWand extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;

    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean bluetooth_enabled = false;

    BLEService mService;
    boolean mBound = false;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    Boolean btScanning = false;
    ArrayList<BluetoothDevice> devicesDiscovered;
    ArrayList<Long> discoveredDevicestime;

    private WandAdapter adapter;
    private Boolean scanning = true;
    private Profile profile;
    private Button demo;

    TextView tryConnectText;
    TextView already;
    private boolean tryconnect;
    private int positie;
    private long connectionTimeout;
    private String tryConnectName;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!inList(result.getDevice().getName())) {
                already.setVisibility(View.GONE);
                devicesDiscovered.add(result.getDevice());
                discoveredDevicestime.add(currentTimeMillis());
                adapter.notifyDataSetChanged();
            }
            discoveredDevicestime.set(devicesDiscovered.indexOf(result.getDevice()),currentTimeMillis());
            if(tryconnect && devicesDiscovered.indexOf(result.getDevice())==positie){

            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (mService.getDeviceName()!= null){
                connected();
            TextView wandname = findViewById(R.id.WandName);
            try{
            wandname.setText(mService.getDeviceName());}
            catch(NullPointerException e){
                wandname.setVisibility(View.GONE);
            }
            }
            else{
                notconnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();

        return (bluetoothAdapter != null &&
                bluetoothAdapter.isEnabled() &&
                bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_wand);

        try {
            profile = (Profile) getIntent().getSerializableExtra("profile");
            profile.getDemo();
        } catch (NullPointerException e) {
            profile = new Profile(-1, "wizard", 0000);
        }

        demo = findViewById(R.id.demo);
        demo.setOnClickListener(this);

        Intent intent = new Intent(this, BLEService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    public void connected(){
        demo.setClickable(false);
        demo.setVisibility(View.GONE);
        already = findViewById(R.id.already);
        already.setVisibility(View.VISIBLE);
        Button menu = findViewById(R.id.menu);
        menu.setVisibility(View.VISIBLE);
        menu.setOnClickListener(this);
        Button disconnect = findViewById(R.id.Disconnect);
        disconnect.setVisibility(View.VISIBLE);
        disconnect.setOnClickListener(this);
    }

    public void notconnected(){
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        else{
            ChooseYourWand.ScanThread scanThread = new ChooseYourWand.ScanThread();
            scanThread.start();
        }
        checkEnable();
        ChooseYourWand.timeoutThread timeoutThread = new ChooseYourWand.timeoutThread();
        timeoutThread.start();

        already = findViewById(R.id.already);
        already.setText("No wand found yet, make sure there is one available!");
        already.setVisibility(View.VISIBLE);
        tryconnect = false;

        devicesDiscovered = new ArrayList<>();
        discoveredDevicestime = new ArrayList<>();
        RecyclerView recyclerview = findViewById(R.id.wand_recycler);
        adapter = new WandAdapter(devicesDiscovered, this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        tryconnect = true;
                        positie = position;
                        tryConnectName = devicesDiscovered.get(position).getName();
                        tryConnectText = findViewById(R.id.tryingText);
                        tryConnectText.setText("Trying to connect to "+ tryConnectName);
                        tryConnectText.setVisibility(View.VISIBLE);
                        connectionTimeout = currentTimeMillis();
                        connectToDeviceSelected(positie);
                        Intent intent;
                        if (profile.getSkip()) {
                            intent = new Intent(ChooseYourWand.this, Menu.class);
                        } else {
                            intent = new Intent(ChooseYourWand.this, Intro.class);
                        }
                        scanning = false;
                        try {
                            unbindService(connection);
                        } catch (RuntimeException e) {
                        }
                        profile.setDemo(false);
                        intent.putExtra("profile", profile);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    public void checkEnable() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        gps_enabled = false;
        network_enabled = false;
        bluetooth_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            bluetooth_enabled = bm.getAdapter().isEnabled();
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setMessage("Please enable your location!")
                    .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }
                    ).setNegativeButton("Cancel", null).show();
        }

        if (!bluetooth_enabled) {
            Log.i("hierzo?", "mja00");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                    ChooseYourWand.ScanThread scanThread = new ChooseYourWand.ScanThread();
                    scanThread.start();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanning = false;
        try {
            unbindService(connection);
        } catch (RuntimeException e) {
        }
    }

    public void startScanning() {
        final ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(ParcelUuid.fromString("0da26b35-ef4a-47e8-bbd8-44606fde5eeb"));

        final List<ScanFilter> lfilt = new ArrayList<>();
        lfilt.add(builder.build());

        final ScanSettings.Builder settings = new ScanSettings.Builder();
        settings.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        settings.setMatchMode(ScanSettings.MATCH_MODE_STICKY);

        System.out.println("start scanning");
        btScanning = true;

        if (isBluetoothAvailable()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        btScanner.startScan(lfilt, settings.build(), scanCallback);
                    } catch (NullPointerException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChooseYourWand.this, "Put on your bluetooth and current location!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChooseYourWand.this, "Put on your bluetooth and current location!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void connectToDeviceSelected(int i) {
        Log.i("test", "probeer");
        if (mBound) {
            Log.i("test", "gelukt");
            mService.connect(devicesDiscovered, i);
        }
    }

    public void disconnectDeviceSelected() {
        if(mBound){
            try{
                mService.disconnect();
            }
            catch(NullPointerException e){}
        }
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        btScanning = false;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isBluetoothAvailable()) {
                        btScanner.stopScan(scanCallback);
                    }
                } catch (NullPointerException e) {
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.demo:
                profile.setDemo(true);
                if (profile.getSkip()) {
                    intent = new Intent(ChooseYourWand.this, Menu.class);
                } else {
                    intent = new Intent(ChooseYourWand.this, Intro.class);
                }
                break;
            case R.id.menu:
                profile.setDemo(false);
                intent = new Intent(ChooseYourWand.this, Menu.class);
                break;
            case R.id.Disconnect:
                disconnectDeviceSelected();
                profile.setDemo(true);
                intent = new Intent(ChooseYourWand.this, ChooseYourWand.class);
                break;
            default:
                intent = new Intent(ChooseYourWand.this, ChooseYourWand.class);
                break;
        }
        scanning = false;
        try {
            unbindService(connection);
        } catch (RuntimeException e) {
        }
        intent.putExtra("profile", profile);
        startActivity(intent);
        finish();

    }

    private boolean inList(String name) {
        Iterator iterator = devicesDiscovered.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice currentBluetoothdevice = (BluetoothDevice) iterator.next();
            if (currentBluetoothdevice.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    class ScanThread extends Thread {

        ScanThread() {
        }

        @Override
        public void run() {
            startScanning();
            while (scanning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            stopScanning();
        }
    }

    class timeoutThread extends Thread {

        timeoutThread() {
        }

        @Override
        public void run() {
            boolean removeText = false;
            while (scanning) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                int size = discoveredDevicestime.size();
                for (int i = 0; i < size; i++) {
                    if(discoveredDevicestime.get(i)+4000<currentTimeMillis()){
                        discoveredDevicestime.remove(i);
                        devicesDiscovered.remove(i);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        size--;
                        i--;
                    }
                }
                if(size==0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            already.setVisibility(View.VISIBLE);
                        }
                    });
                }
                if (connectionTimeout + 4000 < currentTimeMillis() && tryconnect == true) {
                    tryconnect = false;
                    removeText = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tryConnectText.setText("Failed to connect to "+ tryConnectName + ", try again or choose another one!");

                        }
                    });
                }
                if (connectionTimeout + 9000 < currentTimeMillis() && removeText == true) {
                    removeText = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tryConnectText.setText("Failed to connect to "+ tryConnectName + ", try again or choose another one!");

                        }
                    });
                }
            }
        }
    }
}
