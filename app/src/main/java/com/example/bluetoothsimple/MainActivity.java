 package com.example.bluetoothsimple;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.ListView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

 public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter BA;
    Button btEnableDisableVisible;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ArrayList<String> ListDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED.
     private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
         public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
             assert action != null;
             if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                 final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                 switch (state){
                     case BluetoothAdapter.STATE_OFF:
                         Log.d(TAG, "onReceive STATE OFF");
                         break;
                     case BluetoothAdapter.STATE_TURNING_OFF:
                         Log.d(TAG, "onReceive STATE TURNING OFF");
                         break;
                     case BluetoothAdapter.STATE_ON:
                         Log.d(TAG, "onReceive STATE ON");
                         break;
                     case BluetoothAdapter.STATE_TURNING_ON:
                         Log.d(TAG, "onReceive STATE TURNING ON");
                         break;
                 }
             }
            /*if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }*/
         }
     };


     // Create a BroadcastReceiver for ACTION_STATE_CHANGED.
     private final BroadcastReceiver myReceiver2 = new BroadcastReceiver() {
         public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
             assert action != null;
             if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                 final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                 switch (state){
                     case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                         Log.d(TAG, "onReceive2 VISIBLE ON ");
                         break;
                     case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                         Log.d(TAG, "onReceive2 VISIBLE able to connect");
                         break;
                     case BluetoothAdapter.SCAN_MODE_NONE:
                         Log.d(TAG, "onReceive2 VISIBLE OFF not able to connect");
                         break;
                     case BluetoothAdapter.STATE_CONNECTING:
                         Log.d(TAG, "onReceive2 Connecting...");
                         break;
                     case BluetoothAdapter.STATE_CONNECTED:
                         Log.d(TAG, "onReceive2 Connected");
                         break;
                 }
             }
            /*if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }*/
         }
     };

     private BroadcastReceiver myReceiver3 = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             final String action = intent.getAction();
             Log.d(TAG, "onReceive: ACTION FOUND.");

             if (action.equals(BluetoothDevice.ACTION_FOUND)){
                 BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                 int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                // mBTDevices.add(device);
                 Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() + " = " + rssi + "dBm/n");
                // mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                 ListDevices.add(device.getName() + ": " + device.getAddress() + " = " + rssi + "dBm/n");
                 //mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices, rssi);
                 //lvNewDevices.setAdapter(mDeviceListAdapter);
                 ArrayAdapter myArrayAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,ListDevices);
                 lvNewDevices.setAdapter(myArrayAdapter);
             }
         }
     };

     @Override
     protected void onDestroy() {
         Log.d(TAG, "onDestroy: Called");
         super.onDestroy();
         unregisterReceiver(myReceiver);
         unregisterReceiver(myReceiver2);
         unregisterReceiver(myReceiver3);
     }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_onoff = (Button) findViewById(R.id.bt_onoff);
        btEnableDisableVisible = (Button) findViewById(R.id.bt_visible);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        BA = BluetoothAdapter.getDefaultAdapter();

        bt_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Enable/Disable");
                enableDisableBT();
            }
        });

    }

    public void enableDisableBT(){
        if (BA == null){
            Log.d(TAG, "enableDisableBT: does nor have Bluetooth");
        }
        if (!BA.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myReceiver,BTIntent);
        }
        if (BA.isEnabled()){
            BA.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myReceiver,BTIntent);
        }
    }

     public void btEnableDisableVisible(View view){
         Log.d(TAG, "enableDisableVisible: making device visible 300s");
         Intent visibleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         visibleIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
         startActivity(visibleIntent);

         IntentFilter BTVisibleFilter = new IntentFilter(BA.ACTION_SCAN_MODE_CHANGED);
         registerReceiver(myReceiver2,BTVisibleFilter);
         lvNewDevices.setAdapter(null);

         mBTDevices.clear();
         ListDevices.clear();
     }

     @RequiresApi(api = Build.VERSION_CODES.M)
     public void enableLook(View view){
         Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

         if(BA.isDiscovering()){
             BA.cancelDiscovery();
             Log.d(TAG, "btnDiscover: Canceling discovery.");
/*
             //check BT permissions in manifest
             checkBTPermissions();

             BA.startDiscovery();
             IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
             registerReceiver(myReceiver3, discoverDevicesIntent);*/
         }
         if(!BA.isDiscovering()){
             Log.d(TAG, "btnDiscover: Looking for devices.");

             //check BT permissions in manifest
             checkBTPermissions();

             BA.startDiscovery();
             IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
             registerReceiver(myReceiver3, discoverDevicesIntent);
         }
     }

     /**
      * This method is required for all devices running API23+
      * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
      * in the manifest is not enough.
      *
      * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
      */
     @RequiresApi(api = Build.VERSION_CODES.M)
     private void checkBTPermissions() {
         if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
             int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
             permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
             if (permissionCheck != 0) {

                 this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
             }
         }else{
             Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
         }
     }
 }

