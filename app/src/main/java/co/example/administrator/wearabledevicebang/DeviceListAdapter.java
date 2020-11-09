package co.example.administrator.wearabledevicebang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.example.administrator.wearabledevicebang.tools.ExtendedBluetoothDevice;

//必须重载几个父类函数，否则会出错
public class DeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    //上下文环境变量
    private final Context mContext;
    //ArrayList集合类型的变量 mListBondedValues和mListValues，集合中放ExtendedBluetoothDevice对象
    private final ArrayList<ExtendedBluetoothDevice> mListBondedValues = new ArrayList<>();
    private final ArrayList<ExtendedBluetoothDevice> mListValues = new ArrayList<>();
    //内部类AddressComparator的对象comparator，用于调用比较函数
    private final ExtendedBluetoothDevice.AddressComparator comparator = new ExtendedBluetoothDevice.AddressComparator();

    //构造方法，没有返回值，与类名同名，一般需要一个context型的参数
    public DeviceListAdapter(Context context) {
        mContext = context;
    }


    public void addBondedDevice(ExtendedBluetoothDevice device) {
        mListBondedValues.add(device);
        notifyDataSetChanged();
    }

    public void updateRssiOfBondedDevice(String address, int rssi) {
        comparator.address = address;
        final int indexInBonded = mListBondedValues.indexOf(comparator);
        if (indexInBonded >= 0) {
            ExtendedBluetoothDevice previousDevice = mListBondedValues
                    .get(indexInBonded);
            previousDevice.rssi = rssi;
            notifyDataSetChanged();//刷新
        }
    }
    public void addOrUpdateDevice(ExtendedBluetoothDevice device) {
        final boolean indexInBonded = mListBondedValues.contains(device);
        if (indexInBonded) {
            return;
        }
        final int indexInNotBonded = mListValues.indexOf(device);
        if (indexInNotBonded >= 0) {
            ExtendedBluetoothDevice previousDevice = mListValues
                    .get(indexInNotBonded);
            previousDevice.rssi = device.rssi;
            notifyDataSetChanged();
            return;
        }
        mListValues.add(device);
        notifyDataSetChanged();
    }

    public void clearDevices() {
        mListValues.clear();
        notifyDataSetChanged();
    }

    //???????????????????????????????????????????????????????
    @Override
    public int getCount() {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        final int availableCount = mListValues.isEmpty() ? 2 : mListValues
                .size() + 1; // 1 for title, 1 for empty text
        if (bondedCount == 1)
            return availableCount;
        return bondedCount + availableCount;
    }

    @Override
    public Object getItem(int position) {
        final int bondedCount = mListBondedValues.size() + 1; // 1 for the title
        if (mListBondedValues.isEmpty()) {
            if (position == 0)
                return R.string.scanner_subtitle__not_bonded;
            else
                return mListValues.get(position - 1);
        } else {
            if (position == 0)
                return R.string.scanner_subtitle_bonded;
            if (position < bondedCount)
                return mListBondedValues.get(position - 1);
            if (position == bondedCount)
                return R.string.scanner_subtitle__not_bonded;
            return mListValues.get(position - bondedCount - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TITLE;
        if (!mListBondedValues.isEmpty()
                && position == mListBondedValues.size() + 1)
            return TYPE_TITLE;

        if (position == getCount() - 1 && mListValues.isEmpty())
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //Adapter中最重要的函数
    @Override
    public View getView(int position, View oldView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);//获得布局填充器
        final int type = getItemViewType(position);//获得位置

        View view = oldView;
        switch (type) {
            case TYPE_EMPTY:  //为空
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_empty, parent,
                            false);
                }
                break;
            case TYPE_TITLE:   //显示标题
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent,
                            false);
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_row, parent, false);
                    //使用ViewHolder对每一个item的name，address，rssi控件进行设置
                    final ViewHolder holder = new ViewHolder(view);
                    holder.name = (TextView) view.findViewById(R.id.name);
                    holder.address = (TextView) view.findViewById(R.id.address);
                    holder.rssi = (ImageView) view.findViewById(R.id.rssi);
                    holder.name.setClickable(false);
                    holder.name.setFocusable(false);
                    holder.address.setClickable(false);
                    holder.address.setFocusable(false);
                    holder.rssi.setClickable(false);
                    holder.rssi.setFocusable(false);
                    view.setTag(holder);//将holder添加到view中
                }
                //获得当前位置的item，获取值，进行显示
                final ExtendedBluetoothDevice device = (ExtendedBluetoothDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                holder.name.setText(device.device.getName());
                holder.address.setText(device.device.getAddress());
                if (!device.isBonded || device.rssi != -1000) {
                    //根据rssi设置不同图片
                    final int rssiPercent = (int) (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f));//得到RSSi 信号级别0-100
                    holder.rssi.setImageLevel(rssiPercent);
                    holder.rssi.setVisibility(View.VISIBLE);
                } else {
                    holder.rssi.setVisibility(View.GONE);
                }
                break;
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.rssi)
        ImageView rssi;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.address)
        TextView address;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
