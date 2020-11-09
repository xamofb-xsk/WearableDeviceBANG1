package co.example.administrator.wearabledevicebang.Adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothUtil {
    private static final String TAG ="Main";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothStateBroadcastReceive mReceiver;

    public BluetoothUtil(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public boolean getBluetoothState(){
        return bluetoothAdapter.isEnabled();
    }
    public boolean openBluetooth(){
        if(getBluetoothState()) return true;
        return bluetoothAdapter.enable();
    }

    public boolean colseBlueTooth(){
        if(!getBluetoothState())return true;
        return bluetoothAdapter.disable();
    }
    public void gotoSystem(Context context){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivity(intent);
    }

    public void registerBluetoothReceiver(Context context){
        if(mReceiver == null){
            mReceiver = new BluetoothStateBroadcastReceive();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction("android.bluetooth.Bluetooth.STATE_OFF");
        filter.addAction("android.bluetooth.Bluetooth.STATE_ON");
        context.registerReceiver(mReceiver, filter);
    }
    public void unregisterBluetoothReceiver(Context context){
        if(mReceiver != null){
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    class BluetoothStateBroadcastReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        }
    }
}

