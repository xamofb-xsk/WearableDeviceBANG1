package co.example.administrator.wearabledevicebang.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import co.example.administrator.wearabledevicebang.Adapter.GridViewAdapter;
import co.example.administrator.wearabledevicebang.MainActivity;
import co.example.administrator.wearabledevicebang.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    @BindView(R.id.main_stateTv)
    TextView main_stateTv;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    @BindView(R.id.main_gridview)
    GridView mainGridview;
    private List<Map<String, Object>> data_list;
    // TODO: Rename and change types of parameters
    private Context context;
    Unbinder unbinder;

    private MainActivity mainActivity;
    public Handler FmainHandler;

    //初始化程序入口
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.mainActivity = (MainActivity) context;
    }

    private void initGridView() {
        data_list = new ArrayList<>();
        int[] icon = {R.mipmap.item_1, R.mipmap.item_2, R.mipmap.item_3,
                R.mipmap.item_4, R.mipmap.item_5, R.mipmap.item_6,
                R.mipmap.item_7, R.mipmap.item_8, R.mipmap.item_3,
                R.mipmap.item_4, R.mipmap.item_3, R.mipmap.item_3,
                R.mipmap.item_3, R.mipmap.item_8, R.mipmap.item_8};
        String[] iconName = new String[]{"心率计数", "触感提醒", "运动轨迹", "体温检测", "脑电波检测", "坐姿纠正", "脚底压力", "紫外线检测", "跌倒检测", "体温与运动", "腿部计步", "联合计步", "腕部计步", "颈部提醒", "运动感测"};
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
            GridViewAdapter gridViewAdapter = new GridViewAdapter(data_list, context);
            mainGridview.setAdapter(gridViewAdapter);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FmainHandler = new FmainHandler(mainActivity.mainFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this,view);
        initGridView();
//        FmainHandler = new Fmain
        return view;
    }

    private static class FmainHandler extends Handler {
        private MainActivity mainActivity;
        private WeakReference<MainFragment> mActivityReference;
        FmainHandler(MainFragment activity){
            mActivityReference = new WeakReference<>(activity);}
        public void handleMessage(Message msg){
            final MainFragment activity = mActivityReference.get();
            if (activity != null){
                switch (msg.what){
                    case 1:
                        activity.main_stateTv.setText("已连接".concat(activity.mainActivity.connectDevice.getName()));
                        break;
                    case 2:
                        activity.main_stateTv.setText("未连接");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}