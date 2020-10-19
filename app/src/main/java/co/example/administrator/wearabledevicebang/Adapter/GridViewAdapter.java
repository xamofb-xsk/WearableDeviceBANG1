package co.example.administrator.wearabledevicebang.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.example.administrator.wearabledevicebang.R;


//必须重载getCount，getItem，getItemId，getView四个基本方法，否则会出错
public class GridViewAdapter extends BaseAdapter {

    private List<Map<String, Object>> data_list;
    Context context;

    //      无论是用户自定义的构造函数还是默认构造函数都主要有以下特点:
    //            ①. 在对象被创建时自动执行;
    //            ②. 构造函数的函数名与类名相同;
    //            ③. 没有返回值类型、也没有返回值;   带返回值会出错
    //            ④. 构造函数不能被显式调用
    //一般需要一个context的参数，其他参数看需要确定
    public  GridViewAdapter( List<Map<String, Object>>data_list, Context context) {
        this.data_list = data_list;
        this.context = context;
    }

    //getCount()：获取数据的总的数量，返回 int 类型的结果；
    @Override
    public int getCount() {
        return data_list.size();
    }

    //getItem(int position) ：获取指定位置的数据，返回该数据；
    @Override
    public Object getItem(int i) {
        return data_list.get(i);
    }

    //getItemId(int position)：获取指定位置数据的id，返回该数据的id，一般以数据所在的位置作为它的id；
    @Override
    public long getItemId(int i) {
        return i;
    }

    //getView(int position,View convertView,ViewGroup parent)：关键方法，用于确定列表项

    //参数1：int position位置，一般BaseAdapter都是很多类型一样的数据展示在界面，该属性是判断显示在
    // 界面上的是第几个，通过position在BaseAdapter自定义的数组或者集合中取值。并展示在界面上。

    //参数2：View converView 展示在界面上的一个item。，是作为缓存的View，因为手机屏幕就那么大，所以一次展示给用户看见的
    // 内容是固定的，如果你List中有1000条数据，不应该new1000个converView，那样内存肯定不足，
    // 应该学会控件重用，滑出屏幕的converView就在下面新进来的item中重新使用，只是修改下他展示的值

    //参数3：ViewGroup parent 这个属性是加载xml视图时使用。
    //inflate(R.layout.adapter__item, parent, false);确定父控件，减少宽高的测算
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        //View中的setTag（Onbect）表示给View添加一个格外的数据，
        // 以后可以用getTag()将这个数据取出来。
        //可以将convertView看成由多个viewHolder组成，viewHolder直接可以访问数据
        //得到viewHolder
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();//不为空，直接用getTag()获得viewHolder
        } else {      //为空，绑定布局生成View，新建ViewHolder，用setTag（）将数据添加到视图中
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_gridview, parent, false);
            viewHolder = new ViewHolder(convertView);//获取View的子组件ViewHolder用于设置控件属性
            convertView.setTag(viewHolder);
        }

        //通过position取值
        Map<String, Object> map = data_list.get(position);

        //设置viewHolder中制定的控件的文字和图片资源
        viewHolder.gridItemTextview.setText(map.get("text").toString());
        viewHolder.gridItemImageview.setBackgroundResource((Integer) map.get("image"));

        //返回得到的视图
        return convertView;
    }

    //创建 ViewHolder静态内部类 （包含列表项的控件。），通过这个类来设置item控件的内容
    //为了节省资源提高运行效率，一般自定义类 ViewHolder 来减少 findViewById() 的使用以及避免过多地
    // inflate view，从而实现目标。
    //ViewHolder模式，常常用在ListView与Adapter结合时，放在getView方法里，写一个静态内部类，有实例变量。
    // 相当于用该对象作为一个容器，持有View，免得还要用容器类
    //目的：优化资源，节省空间，避免重复绘制view而引起的不必要的内存损耗。
    //操作方法：由上方的fragment_gridview右键黄油刀产生，选中产生ViewHolder选项
    static class ViewHolder {
        @BindView(R.id.grid_item_imageview)
        ImageView gridItemImageview;
        @BindView(R.id.grid_item_textview)
        TextView gridItemTextview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }



}
