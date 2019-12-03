package be.magdyabdel.wandz;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

public class BLEService extends Service {

    private final IBinder binder = new LocalBinder();      // interface for clients that bind
    BluetoothGatt bluetoothGatt;
    boolean disconnectBeforeConnecting = false;
    ArrayList<BluetoothDevice> devicesDiscovered;
    int deviceIndexInput;
    ArrayList<BluetoothGattCharacteristic> acceleroChars = new ArrayList<>();
    BluetoothGattCharacteristic spell;
    BluetoothGattCharacteristic wizardID;
    BluetoothGattCharacteristic gotHit;
    int charIndex = 0;
    /*data for the DTW*/
    Float[] waarden;
    int position;
    int dummy;
    float[] gravity;
    int aantal;
    int training;
    private int mData;
    private List<Float[]>[] trainingsets;
    private List<Float[]> rlist;
    private DTW dtw;
    private Trainingdata data;
    byte gesture = 0;

    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            // BLEService.this.runOnUiThread(new Runnable() {
            //public void run() {
            // peripheralTextView.append("device read or wrote to\n");

            byte[] val = characteristic.getValue();
            if (UUID.fromString("00004ad1-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
                float x = ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                aantal++;
                addValue(x);
            } else if (UUID.fromString("00004ad2-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
                float y = ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                addValue(y);
            } else if (UUID.fromString("00004ad3-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
                float z = ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                addValue(z);
            }
             else if (UUID.fromString("00004ad6-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
                 Log.i("hitupdate", "characteristic gelezen");
                 int hitvalue = ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getInt();
                Log.i("hitupdate", ""+ gotHit);
                sendHitMessageToActivity(hitvalue);
             }

            //}
            // });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            System.out.println(newState);
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    bluetoothGatt.close();
                    acceleroChars.clear();
                    bluetoothGatt = null;
                 /*   BLEparcelable.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //  peripheralTextView.append("device disconnected\n");
                            //connectToDevice.setVisibility(View.VISIBLE);
                            //disconnectDevice.setVisibility(View.INVISIBLE);

                        }
                    });*/

                    if (disconnectBeforeConnecting) {
                        bluetoothGatt = devicesDiscovered.get(deviceIndexInput).connectGatt(BLEService.this, false, btleGattCallback);
                    }
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                   /* BLEparcelable.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //  peripheralTextView.append("device connected\n");
                            //   connectToDevice.setVisibility(View.INVISIBLE);
                            // disconnectDevice.setVisibility(View.VISIBLE);
                        }
                    });*/

                    // discover services and characteristics for this device
                    bluetoothGatt.discoverServices();

                    break;
                default:
                   /* BLEparcelable.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //peripheralTextView.append("we encounterned an unknown state, uh oh\n");
                        }
                    });*/
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
          /*  BLEparcelable.this.runOnUiThread(new Runnable() {
                public void run() {
                    //peripheralTextView.append("device services have been discovered\n");
                }
            });*/
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor,
                                      int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ++charIndex;
                if (charIndex < 3) {
                    //peripheralTextView.append("SUCCESS DESCRIPTOR\n");
                    BluetoothGattCharacteristic c = acceleroChars.get(charIndex);
                    bluetoothGatt.setCharacteristicNotification(c, true);
                    BluetoothGattDescriptor d = c.getDescriptor(
                            UUID.fromString("000002902-0000-1000-8000-00805f9b34fb"));
                    d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(d);
                } else {
                    charIndex = 0;
                }
            }
        }
    };

    private static final float[][] primitive(final List<Float[]> pList) {
        // Declare the Array.
        final float[][] lT = new float[pList.size()][3];
        // Iterate the List.
        for (int i = 0; i < pList.size(); i++) {
            // Buffer the Element.
            lT[i][0] = pList.get(i)[0];
            lT[i][1] = pList.get(i)[1];
            lT[i][2] = pList.get(i)[2];
        }
        Log.i("array", Arrays.deepToString(lT)); //print multi dimension array
        // Return the Array.
        return lT;
    }



    public void connect(ArrayList<BluetoothDevice> devices, int device) {
        devicesDiscovered = devices;
        deviceIndexInput = device;
        if ((bluetoothGatt != null)) {
            bluetoothGatt.disconnect();
            disconnectBeforeConnecting = true;
        } else {
            bluetoothGatt = devices.get(device).connectGatt(this, false, btleGattCallback);
        }
    }

    public void disconnect() {
        bluetoothGatt.disconnect();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            System.out.println("Service discovered: " + uuid);
            /*BLEparcelable.this.runOnUiThread(new Runnable() {
                public void run() {
                    //  peripheralTextView.append("Service discovered: " + uuid + "\n");
                }
            });*/

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

//                bluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);

                if (UUID.fromString("00004ad1-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    bluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);

                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(
                            UUID.fromString("000002902-0000-1000-8000-00805f9b34fb"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                    acceleroChars.add(gattCharacteristic);
                } else if (UUID.fromString("00004ad2-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    acceleroChars.add(gattCharacteristic);
                } else if (UUID.fromString("00004ad3-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    acceleroChars.add(gattCharacteristic);
                } else if (UUID.fromString("00004ad4-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                spell = gattCharacteristic;
                 }
                 else if (UUID.fromString("00004ad5-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    wizardID = gattCharacteristic;
                }
                else if (UUID.fromString("00004ad6-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    gotHit = gattCharacteristic;
                }

                final String charUuid = gattCharacteristic.getUuid().toString();
                System.out.println("Characteristic discovered for service: " + charUuid);
              /*  BLEService.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //peripheralTextView.append("Characteristic discovered for service: " + charUuid + "\n");
                    }
                });*/

            }
        }
    }

    public void addValue(float value) {
        // Toast toast;
        final float alpha = 0.70f;
        boolean gesturerecognised = false;
        gravity[position] = alpha * gravity[position] + (1 - alpha) * value;
        waarden[position] = value - gravity[position];
        if (value == 500f) {
            String text = "";
            float[][] sample = primitive(rlist);
            double[] error = {100.0, 100.0, 100.0};
            double minimum = 10;
            int index;
            for (int i = 0; i < 10; i++) {
                float[][] training = data.getTrainingset1()[i];
                double test = dtw.computeDTWError(sample, training);
                if (test < error[0]) {
                    error[0] = test;
                    if (error[0] < 0.11) {
                        text = "RIGHT-LEFT-RIGHT";
                        index = i;
                        gesture = 1;
                        gesturerecognised = true;
                    }
                }
            }
            for (int i = 0; i < 10; i++) {
                float[][] training = data.getTrainingset2()[i];
                double test = dtw.computeDTWError(sample, training);
                if (test < error[1]) {
                    error[1] = test;
                    if (error[1] < 0.17) {
                        text = "UP-DOWN-UP";
                        index = i;
                        gesture = 2;
                        gesturerecognised = true;
                    }
                }
            }
            for (int i = 0; i < 10; i++) {
                float[][] training = data.getTrainingset3()[i];
                double test = dtw.computeDTWError(sample, training);
                if (test < error[2]) {
                    error[2] = test;
                    if (error[2] < 0.13) {
                        text = "CIRCLE";
                        index = i;
                        gesture = 3;
                        gesturerecognised = true;
                    }
                }
            }

            if (gesturerecognised) {
                sendGestureMessageToActivity(gesture);
                spell.setValue(gesture, FORMAT_UINT8, 0); //true if succes
                boolean b = bluetoothGatt.writeCharacteristic(spell);           //true if succes
                //  toast = Toast.makeText(getApplicationContext(),"Gesture " + text + " with errors " + error[0] + " "+ error[1] + " "+error[2], Toast.LENGTH_SHORT);
            } else {
                sendGestureMessageToActivity((byte) 0);
                // toast = Toast.makeText(getApplicationContext(),"No gesture was recogised " + error[0] + " "+ error[1] + " "+error[2] , Toast.LENGTH_SHORT);
            }
            // toast.show();
            rlist.clear();
            gravity = new float[]{0, 0.25f, 0.75f};
            aantal = 0;
        } else if (position == 2) {
            rlist.add(waarden);
            //acceleroZTextView.setText(String.format("%.2f",waarden[position]));
            waarden = new Float[3];
            position = 0;
        } else {
            if (position == 0) {
                //acceleroXTextView.setText(String.format("%.2f",waarden[position]));
            }
            if (position == 1) {
                //acceleroYTextView.setText(String.format("%.2f",waarden[position]));
            }
            position++;
        }
        aantal++;

        //////////////////////////////////////TRAINING///////////////////////////////////////

/*
        if(value ==500f) {
            if (dummy != 9) {
                dummy++;
                toast = Toast.makeText(getApplicationContext(), "training completed with " + aantal + " samples", Toast.LENGTH_SHORT);
                toast.show();
                gravity = new float[]{0.50f, 0.0f, 0.85f}; //reset gravity
                aantal = 0;
            } else {
                for (int i = 0; i < 10; i++) {
                    float training[][] = primitive(trainingsets[i]);
                }
            }
        }
        else if(position==2){
            trainingsets[dummy].add(waarden);
            acceleroZTextView.setText(String.format("%.2f",waarden[position]));
            waarden = new Float[3];
            position = 0;
        }
        else{
            if(position==0){
            acceleroXTextView.setText(String.format("%.2f",waarden[position]));}
            if(position==1){
                acceleroYTextView.setText(String.format("%.2f",waarden[position]));}
            position++;
        }
        aantal++;*/
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BLEService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BLEService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    public void onCreate() {
        training = 0;
        position = 0;
        dummy = 0;
        aantal = 0;
        waarden = new Float[3];
        dtw = new DTW();
        data = new Trainingdata();
        rlist = new ArrayList<>(); //initialise recognition list
        trainingsets = new ArrayList[10]; //initialise array of training sets
        for (int i = 0; i < 10; i++) {
            trainingsets[i] = new ArrayList<>();
        }
        gravity = new float[]{0, 0, 0};
    }

    private  void sendGestureMessageToActivity(byte b) {
        Intent intent = new Intent("GestureUpdate");
        // You can also include some extra data.
        intent.putExtra("gesture", b);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i("message", "message is send");
    }

    private  void sendHitMessageToActivity(int a) {
        Intent intent = new Intent("hitUpdate");
        // You can also include some extra data.
        intent.putExtra("hitCode", a);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i("message", "message is send");
    }

    public void sendWizardID(int ID){
        wizardID.setValue(ID, FORMAT_UINT32, 0); //true if succes
        boolean b = bluetoothGatt.writeCharacteristic(wizardID);//true if succes
    }
}

