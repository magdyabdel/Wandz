package be.magdyabdel.wandz;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

public class BLEService extends Service {

    private final IBinder binder = new LocalBinder();
    BluetoothGatt bluetoothGatt;
    boolean disconnectBeforeConnecting = false;
    ArrayList<BluetoothDevice> devicesDiscovered;
    int deviceIndexInput;
    ArrayList<BluetoothGattCharacteristic> acceleroChars = new ArrayList<>();
    BluetoothGattCharacteristic spell;
    BluetoothGattCharacteristic wizardID;
    BluetoothGattCharacteristic gotHit;
    int charIndex = 0;

    Float[] waarden;
    int position;
    int dummy;
    int aantal;
    int training;
    private int mData;
    private List<Double>[] trainingsets;
    private List<Integer> trainingTime;
    private List<Float[]> rlist;
    private List<Long> rlistTime;
    private double[] anglechange;
    private DTW dtw;
    private Trainingdata data;
    int amount_training = 20;

    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            byte[] val = characteristic.getValue();
            if (UUID.fromString("00004ad1-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
                float x = ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getFloat();
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
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            System.out.println(newState);
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    bluetoothGatt.close();
                    acceleroChars.clear();
                    bluetoothGatt = null;

                    if (disconnectBeforeConnecting) {
                        bluetoothGatt = devicesDiscovered.get(deviceIndexInput).connectGatt(BLEService.this, false, btleGattCallback);
                    }
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    bluetoothGatt.discoverServices();

                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ++charIndex;
                if (charIndex < 4) {
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
        //Log.i("array", Arrays.deepToString(lT)); //print multi dimension array
        return lT;
    }
    private static final double[]primitivesingle(final List<Double> pList) {
        // Declare the Array.
        final double[] lT = new double[pList.size()];
        // Iterate the List.
        for (int i = 0; i < pList.size(); i++) {
            // Buffer the Element.
            lT[i] = pList.get(i);
        }
        //Log.i("array", Arrays.deepToString(lT)); //print multi dimension array
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

        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            System.out.println("Service discovered: " + uuid);

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

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
                } else if (UUID.fromString("00004ad5-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    wizardID = gattCharacteristic;
                } else if (UUID.fromString("00004ad6-0000-1000-8000-00805f9b34fb").equals(gattCharacteristic.getUuid())) {
                    gotHit = gattCharacteristic;
                    acceleroChars.add(gattCharacteristic);
                }

                final String charUuid = gattCharacteristic.getUuid().toString();
                System.out.println("Characteristic discovered for service: " + charUuid);
            }
        }
    }

    public void addValue(float value) {
        byte gesture = 0;
        boolean gesturerecognised = false;
        waarden[position] = value ;
        if (value == 500f) {  //end the gesture detection
            float[][] sample = primitive(rlist);
            anglechange = new double[rlist.size()];
            anglechange[0] = 0;
            float xchange =0;
            float ychange =0;
            float zchange= 0 ;
            for(int i=1; i< sample.length ;i++){
                xchange += sample[i][0]*(rlistTime.get(i)-rlistTime.get(i-1));
                ychange += sample[i][0]*(rlistTime.get(i)-rlistTime.get(i-1));
                zchange += sample[i][0]*(rlistTime.get(i)-rlistTime.get(i-1));
                //String tijd = Long.toString(rlistTime.get(i)-rlistTime.get(i-1));
               // Log.i("tijdspanne", ""+ tijd);
                anglechange[i] = Math.sqrt(Math.pow(xchange,2) + Math.pow(ychange,2) + Math.pow(zchange,2));
            }
            Log.i("array", Arrays.toString(anglechange));
            writeToFile(Arrays.toString(anglechange),this);
            double error[] = new double[10];
            error[0] = dtw.computeDTWError(anglechange, data.getTrainingset1());
            error[1] = dtw.computeDTWError(anglechange, data.getTrainingset2());
            error[2] = dtw.computeDTWError(anglechange, data.getTrainingset3());

            if(error[0]<8500){//cirkel
                gesturerecognised = true;
                gesture = 1;
            }
            else if(error[1]<10000&&error[0]>14000){//eight // && error[2]>6500
                gesturerecognised = true;
                gesture = 2;
            }
            else if(error[2]<10000){//eight  && error[0]>25000 && error[1]>10000
                gesturerecognised = true;
                gesture = 3;
            }

            if (gesturerecognised) {
                Log.i("recognised!", Integer.toString(gesture) +" "+ Double.toString(error[0])+" "+ Double.toString(error[1])+" "+ Double.toString(error[2]));
                sendGesture(gesture);
                //  toast = Toast.makeText(getApplicationContext(),"Gesture " + text + " with errors " + error[0] + " "+ error[1] + " "+error[2], Toast.LENGTH_SHORT);
            } else {
                Log.i("NOT recognised!", Double.toString(error[0])+" "+ Double.toString(error[1])+" "+ Double.toString(error[2]) );
                sendGestureMessageToActivity((byte) 0);
                // toast = Toast.makeText(getApplicationContext(),"No gesture was recogised " + error[0] + " "+ error[1] + " "+error[2] , Toast.LENGTH_SHORT);
            }
            // toast.show();
            rlist.clear();
            rlistTime.clear();
            aantal = 0;
        } else if (position == 2) {            rlist.add(waarden);
            rlistTime.add(System.currentTimeMillis());
            waarden = new Float[3];
            position = 0;
        } else {
            position++;
        }
        aantal++;
    }

    public void addtValue() {
        int total_samples = 20;
        /**
         * Make every time sample the same length
         */
        double[][] normalized = new double[total_samples][data.getallS()[0].length];
        for(int i=0;i<total_samples;i++){
            normalized[i] = dtw.normalize(data.getallS()[i],data.getallS()[0]);
            Log.i("result", " " + normalized[i].length + Arrays.toString(normalized[i]));
        }
        /**
         * Calculate average template
         */
        double[] template = new double[data.getallS()[0].length];
        for(int j = 0; j<data.getallS()[0].length; j++) {
            for (int i = 0; i < total_samples; i++){
                template[j] += normalized[i][j];
            }
            template[j] /= total_samples;
        }
        Log.i("result", " " + normalized[0].length + Arrays.toString(template));
        /**
         * determining all errors with optimal sample
         */
         double[] erroronoptimal = new double[total_samples];
         double sum = 0;
         for (int i = 0; i < total_samples; i++) {
              erroronoptimal[i] = dtw.computeDTWError(template, data.getallS()[i]);
              sum+= erroronoptimal[i];
             Log.i("error","" + erroronoptimal[i]);
         }
         sum /= total_samples;
          Log.i("averageerroronoptimalsample","" + Double.toString(sum));



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void sendWizardID(int ID) {
        try {
            wizardID.setValue(ID, FORMAT_UINT32, 0); //true if succes
            boolean b = bluetoothGatt.writeCharacteristic(wizardID);//true if succes
        } catch (NullPointerException e) {
        }
    }

    public void sendGesture(byte gesture){
        sendGestureMessageToActivity(gesture);
        spell.setValue(gesture, FORMAT_UINT8, 0); //true if success
        boolean b = bluetoothGatt.writeCharacteristic(spell);           //true if success
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
        rlistTime = new ArrayList<>();
        trainingTime = new ArrayList<>();
        trainingsets = new ArrayList[amount_training]; //initialise array of training sets
        for (int i = 0; i < amount_training; i++) {
            trainingsets[i] = new ArrayList<>();
        }
        addtValue();
    }

    private  void sendGestureMessageToActivity(byte b) {
        Intent intent = new Intent("GestureUpdate");
        intent.putExtra("gesture", b);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i("message", "message is send");
    }

    private  void sendHitMessageToActivity(int a) {
        Intent intent = new Intent("hitUpdate");
        intent.putExtra("hitCode", a);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.i("message", "message is send");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //unregister listeners
        //do any other cleanup if required
        stopService(rootIntent);
        Log.i("stop", "stop");
        //stop service
        stopSelf();
    }

    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }


    private void writeToFile(String data, Context context) { //look in device file explorer, data/data/be.magdyabel.wandz/files
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("rechtslinkslang.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
            Log.i("waar?", " " + BLEService.this.getFilesDir().getAbsolutePath());
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}

