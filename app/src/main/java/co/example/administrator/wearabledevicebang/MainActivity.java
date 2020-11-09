package co.example.administrator.wearabledevicebang;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import activity.lsen.wearabledevice.entity.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.example.administrator.wearabledevicebang.Fragment.MainFragment;
import co.example.administrator.wearabledevicebang.tools.ExtendedBluetoothDevice;

public class MainActivity extends AppCompatActivity {
//    private static final Object DEVICE_NOT_BONDED = false;
public MainFragment mainFragment;
    private Fragment currentFragment;
    private MainActivity mainActivity;
    //蓝牙相关变量
    private boolean isScanning = false;                 //当前是否扫描蓝牙设备
    private BluetoothDevice bluetoothDevice;            // 已经连接的蓝牙设备
    private BluetoothGatt bluetoothGatt;                //蓝牙连接对象
    private final int REQUEST_LOCATION_PERMISSION = 0x123;  //定位权限请求码

    private DeviceListAdapter deviceListAdapter;
    private Button mScanButton;
    private BluetoothAdapter bluetoothAdapter;          // 蓝牙适配器

    //选择的蓝牙设备是否绑定的标志位，默认为false未绑定
    private static final boolean DEVICE_NOT_BONDED = false;
    private Handler mainHandler;   //  用于延时执行的Handler类
    private boolean isOut = false;
    public boolean isConnectState = false;
    private BluetoothGattCharacteristic txCharacteristic;
    private BluetoothGattCharacteristic rxCharacteristic;
    public BluetoothDevice connectDevice;

    public byte[] sendData;                  //发送缓冲区，将要发送的数据写入此数组进行发送
    private byte[] value = new byte[20];    //接收缓冲区，接受到的数据存在此数组中，供此程序分析处理，20个字节
    public boolean isSend = true;           //是否正在发送数据的标志，没有正在发送时，才能开始发送s

    private final static UUID SERVICE_UUID = UUID
            .fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private final static UUID TX_SERVICE_UUID = UUID
            .fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final static UUID RX_SERVICE_UUID = UUID
            .fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");


    DeviceListAdapter devicelistadapter;
    User user;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.menu_title)
    TextView menuTitle;
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.main_framement)
    FrameLayout mainFramement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainFragment = new MainFragment();
        currentFragment = mainFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.main_framement, currentFragment).commit();
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this,"该设备不支持蓝牙BLE通信",Toast.LENGTH_SHORT).show();
            finish();                                   //不能进行后面实验，结束
        }

        //检查蓝牙是否开启
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScanning == false && bluetoothDevice == null){     //！isScanning 同isScaning==false
                    mayRequestLocation();       //获取5.0定位权限，并显示扫描对话框
                }
                else if(bluetoothDevice == null){       //正在扫描，提示
                    showToast("正在扫描……");
                }
                else{       //已经连接了蓝牙设备，单击加号键，是要断开蓝牙连接，先提示确认
                    showAlertDialog();
                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBleEnabled()){
                    enableBluetooth();  //如没有开启，则开启
                }
                if(!isScanning && bluetoothDevice == null){
                    mayRequestLocation();
                }else if(bluetoothDevice == null){
                    showToast("正在扫描....");
            }else{
                    showAlertDialog();
                }
            }
        });
    }

    private boolean isBleEnabled(){
        //得到蓝牙管理器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //得到蓝牙适配器，private BluetoothAdapter bluetoothAdapter
        bluetoothAdapter = bluetoothManager.getAdapter();
        //返回蓝牙是否开启，判断条件bluetoothAdapter != null && bluetoothAdapter.enable();
        return bluetoothAdapter!= null && bluetoothAdapter.isEnabled();
    }

    //开启蓝牙
    private void enableBluetooth(){
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            //用Intent开启蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,2);//2表示请求的功能是蓝牙
        }
    }

    //获取5.0定位权限，并显示扫描对话框
    //   private final int REQUEST_LOCATION_PERMISSION = 0x123;  //定位权限请求码
    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {//是android 5.0以上版本，使用蓝牙需开启定位权限
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //没有，则请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(this, "使用蓝牙需要开启定位权限", Toast.LENGTH_SHORT).show();
                }
            } else {//有权限，直接显示扫描对话框
                scannerDialog();
            }
        } else {//不是5.0，直接显示扫描对话框
            scannerDialog();
        }
    }


    //蓝牙扫描对话框
    private void scannerDialog() {
        //使用AlertDialog.Builder设置AlertDialog对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //使用LayoutInflater布局填充器生成视图view
        View dialogView = LayoutInflater.from(this).inflate(
                R.layout.fragment_device_selection, null);
        //使用findViewById获得布局中的ListView控件
        final ListView listview = (ListView) dialogView
                .findViewById(android.R.id.list);
        //设置空视图
        listview.setEmptyView(dialogView.findViewById(android.R.id.empty));
        //设置listview的适配器
        listview.setAdapter(deviceListAdapter = new DeviceListAdapter(MainActivity.this));
        //设置对话框标题
        builder.setTitle("选择设备");
        //使用findViewById获得布局中的按钮控件
        //    private Button mScanButton;
        mScanButton = (Button) dialogView.findViewById(R.id.action_cancel);
        //创建对话框dialog
        final AlertDialog dialog = builder.setView(dialogView).create();
        //监听listview item 点击事件
        //选中某个item，停止扫描，获得设备，进行连接，关闭对话框
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //mainHandler.sendEmptyMessage(7);//震动100ms
                stopScan();//停止扫描
                bluetoothDevice = ((ExtendedBluetoothDevice) deviceListAdapter
                        .getItem(position)).device;//获得用户单击的蓝牙设备
                connectGatt(bluetoothDevice);//连接选定的蓝牙
                dialog.cancel();//关闭对话框
            }
        });
        //监听扫描按钮点击事件，如果正在扫描，就关闭对话框；如没在扫描，则开始扫描
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.action_cancel) {
                    // mainHandler.sendEmptyMessage(7);//震动100ms
                    if (isScanning) {   //如果正在扫描
                        if (dialog != null)   //关闭对话框
                            dialog.cancel();
                    } else {     //没有正在扫描
                        startScan(false);      //开始扫描
                    }
                }
            }
        });
        //显示对话框dialog
        dialog.show();
        //开始扫描，向对话框添加内容
        startScan(false);
    }

    //连接蓝牙
    private void connectGatt(BluetoothDevice bluetoothDevice) {
        if (bluetoothGatt != null) {    //已经有连接，就关闭连接
            closeBluetoothGatt();
        }
        if (bluetoothGatt == null) {    //没有连接，则进行连接
            bluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this, false,
                    bluetoothGattCallback);
        }
    }

    //BluetoothGattCallback,实现蓝牙连接、发现服务、接收数据的类
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        //蓝牙连接
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {   //连接成功
                if (newState == BluetoothProfile.STATE_CONNECTED) {   //已连接
                    bluetoothGatt.discoverServices();//搜索蓝牙服务
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  //断开连接
                    disconnect();
                    closeBluetoothGatt();
                    //    private boolean isOut = false;
                    if (!isOut) {
                        mainFragment.FmainHandler.sendEmptyMessage(2);//设置MainFragment标题“蓝牙未连接”
                    }
                    //    public boolean isConnectState = false;
                    isConnectState = false;
                    mainHandler.sendEmptyMessage(6);//停止发送服务器数据
                }
            } else {   //连接没有成功
                disconnect();
                closeBluetoothGatt();
                mainFragment.FmainHandler.sendEmptyMessage(2);//设置MainFragment标题“蓝牙未连接”
                isConnectState = false;
                mainHandler.sendEmptyMessage(6);//停止发送服务器数据
            }
        }

        //发现服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            boolean isUart = false;
            if (status == BluetoothGatt.GATT_SUCCESS) {  //如连接成功
                //得到所有的服务放到List<BluetoothGattService>中
                List<BluetoothGattService> services = gatt.getServices();
                //foreach循环，service遍历services中所有元素
                for (BluetoothGattService service : services) {
                    isUart = true;
                    //    private BluetoothGattCharacteristic txCharacteristic;
                    //    private BluetoothGattCharacteristic rxCharacteristic;
                    txCharacteristic = service.getCharacteristic(TX_SERVICE_UUID);
                    rxCharacteristic = service.getCharacteristic(RX_SERVICE_UUID);
                }
                if (isUart) {
                    //设置接受数据通知，开启通道自动返回数据
                    boolean is = setCharacteristicNotification(txCharacteristic, true);
                    if (is) {
                        mainHandler.sendEmptyMessage(5);//向服务器发送用户信息
                        //    public boolean isConnectState = false;
                        // public BluetoothDevice connectDevice;
                        connectDevice = bluetoothDevice;
                        isConnectState = true;

                        //获取蓝牙发送和接受服务，开启通知后，才认为蓝牙连接已经完成
                        //向mainfragmen类的句柄类发送消息1，由句柄类接受后显示已连接及其设备号，消息号=1
                        mainFragment.FmainHandler.sendEmptyMessage(1);//设置mainfragment标题已连接
                    }
                }
            }
        }

        //接收蓝牙数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic.getUuid().equals(TX_SERVICE_UUID)) {  //接收服务
                value = new byte[20];   //接收数组，每条信息为20个字节
                for (int i = 0; i < value.length; i++) {
                    value[i] = characteristic.getValue()[i];    //获得数据，收到value数组中，20个字节
                }
                if (!isSend) {  //发送数据
                    writeCharacteristic(sendData);
                    isSend = true;
                }
                // System.out.println(bytesToHexString(value));  //控制台打印输出收到的数据
                mainHandler.sendEmptyMessage(1);  //去处理数据，发送消息4
            }
        }
    };

    // characteristic 通知开启
    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (bluetoothGatt == null) {//没有蓝牙连接，直接返回
            return false;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enable);
        //GATT Descriptors contain additional information and attributes of a GATT characteristic,
        // BluetoothGattCharacteristic. They can be used to describe the characteristic's features
        // or to control certain behaviours of the characteristic.
        //设置描述器，写描述器，完成开启通知
        BluetoothGattDescriptor bluetoothGattDescriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }





    // byte 数组转16进制字符串
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String str = Integer.toHexString(b & 0xFF);
            if (str.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(str.concat(" "));
        }
        return stringBuilder.toString();
    }

    //发送数据  用于马达模块，将数据写到rxCharacteristic中，用writeCharacteristic发送
    public boolean writeCharacteristic(byte[] value) {
        boolean isSend = false;
        if (bluetoothGatt != null && rxCharacteristic != null) {
            rxCharacteristic.setValue(value);
            isSend = bluetoothGatt.writeCharacteristic(rxCharacteristic);
        }
        return isSend;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isScanning) {
                        scannerDialog();
                    }
                }
                break;
        }
    }

    //关闭蓝牙连接
    private void closeBluetoothGatt() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
        bluetoothDevice = null;
        connectDevice = null;
    }


    //蓝牙适配器的扫描回调，在其中实现蓝牙扫描
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
                updateScannedDevice(device, rssi);
                //  private static final boolean DEVICE_NOT_BONDED = false;
                // 选择的蓝牙设备是否绑定的标志位，默认为false未绑定
                addScannedDevice(device, rssi, DEVICE_NOT_BONDED);
            }
        }
    };

    //使用多线程更新被搜索到的设备的RSSI(信号强度)
    private void updateScannedDevice(final BluetoothDevice device,
                                     final int rssi) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.updateRssiOfBondedDevice(device.getAddress(), rssi);
            }
        });
    }

    //添加被扫描到的设备到适配器
    private void addScannedDevice(final BluetoothDevice device, final int rssi,
                                  final boolean isBonded) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.addOrUpdateDevice(new ExtendedBluetoothDevice(device,
                        rssi, isBonded));
            }
        });
    }


    //开始蓝牙扫描
    private void startScan(boolean b) {
        add.setEnabled(false);   //+不可用
        if (b) {     //b=true
            UUID[] uuids = new UUID[1];
            uuids[0] = SERVICE_UUID;
            //    private BluetoothAdapter bluetoothAdapter;          // 蓝牙适配器
            bluetoothAdapter.startLeScan(uuids, scanCallback);
        } else {    //b=false
            bluetoothAdapter.startLeScan(scanCallback);
        }
        mScanButton.setText("取消");
        isScanning = true;
        //    private Handler mainHandler;   //  用于延时执行的Handler类
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    stopScan();
                }
            }
        }, 10000);   //延迟10000ms=10s后会自动停止扫描
    }

    //停止扫描，按钮显示扫描，+按键可见
    private void stopScan() {
        if (isScanning) {
            bluetoothAdapter.stopLeScan(scanCallback);
            isScanning = false;
            showToast("停止扫描");
        }
        mScanButton.setText("扫描");
        add.setEnabled(true);
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                //三个参数分别表示（起点位置，水平向右位移，垂直向下位移）
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
            }
        });
    }

    //提示警告对话框，先构造，再显示
    public void showAlertDialog(){
        AlertDialog WifiDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("是否断开蓝牙")   //设置提示内容文本为“是否断开蓝牙连接”
                .setTitle("提示")            //设置对话框的标题为“提示”
                //设置确定按钮名称为“取消”，并设置他的单击响应方法为断开连接
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //disconnect
                        disconnect();
                    }
                })
                //设置取消按钮名称为“取消”，并设置他的单击响应方法为空
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                })
                .setCancelable(false)
                .create();      //分号结束，设置完成
        WifiDialog.show();      //显示对话框
    }

    //断开蓝牙
    public void disconnect(){
        if(bluetoothGatt == null){
            return ;
        }
        bluetoothGatt.disconnect();
    }

    //MainHandler类，主要用于各个fragment之间的通信
    private static class MainHandler extends Handler {

        private WeakReference<MainActivity> mActivityReference; //弱引用
        //private MainActivity mmm ;//强引用，两者的回收机制不相同，句柄类中通常使用弱引用

        //构造方法
        MainHandler(MainActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }


    }


    }

