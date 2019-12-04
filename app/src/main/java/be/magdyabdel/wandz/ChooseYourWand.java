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
import android.os.Handler;
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

public class ChooseYourWand extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final long SCAN_PERIOD = 5000;

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

    private Handler mHandler = new Handler();

    private WandAdapter adapter;
    private Boolean scanning = true;
    private Profile profile;
    private Button demo;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!inList(result.getDevice().getName())) {
                devicesDiscovered.add(result.getDevice());
                adapter.notifyDataSetChanged();
            }
        }
    };

    private boolean skip = false;
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

        profile = (Profile) getIntent().getSerializableExtra("profile");
        skip = (Boolean) getIntent().getSerializableExtra("skip");
        demo = findViewById(R.id.demo);
        demo.setOnClickListener(this);

        if (profile.getDemo()) {
            Intent intent = new Intent(this, BLEService.class);
            startService(new Intent(this, BLEService.class));
            bindService(intent, connection, Context.BIND_AUTO_CREATE);

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
            checkEnable();

            ChooseYourWand.ScanThread scanThread = new ChooseYourWand.ScanThread();
            scanThread.start();

            devicesDiscovered = new ArrayList<>();
            RecyclerView recyclerview = findViewById(R.id.wand_recycler);
            adapter = new WandAdapter(devicesDiscovered, this);
            recyclerview.setAdapter(adapter);
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            recyclerview.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            connectToDeviceSelected(position);
                            scanning = false;
                            Intent intent;
                            if (skip) {
                                intent = new Intent(ChooseYourWand.this, Menu.class);
                            } else {
                                intent = new Intent(ChooseYourWand.this, LearnTheGestures.class);
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
        } else {
            demo.setClickable(false);
            demo.setVisibility(View.GONE);
            TextView already = findViewById(R.id.already);
            already.setVisibility(View.VISIBLE);
            Button menu = findViewById(R.id.menu);
            menu.setVisibility(View.VISIBLE);
            menu.setOnClickListener(this);
        }
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
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            /*new AlertDialog.Builder(this)
                    .setMessage("Please enable your bluetooth!")
                    .setPositiveButton("Open bluetooth settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                }
                            }
                    ).setNegativeButton("Cancel",null).show();*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
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
        //Toast.makeText(this, "Service Un-Binded", Toast.LENGTH_LONG).show();
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
        mService.disconnect();
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
        switch (view.getId()) {
            case R.id.demo:
                profile.setDemo(true);
                break;
            case R.id.menu:
                profile.setDemo(false);
                break;
        }
        Intent intent;
        if (skip) {
            intent = new Intent(ChooseYourWand.this, Menu.class);
        } else {
            intent = new Intent(ChooseYourWand.this, LearnTheGestures.class);
        }
        scanning = false;
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
}
