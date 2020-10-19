package co.example.administrator.wearabledevicebang.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import co.example.administrator.wearabledevicebang.Adapter.GridViewAdapter;
import co.example.administrator.wearabledevicebang.MainActivity;
import co.example.administrator.wearabledevicebang.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    GridView gridView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    @BindView(R.id.main_gridview)
    GridView mainGridview;
    @BindView(R.id.main_stateTv)
    TextView titleTv;
    private List<Map<String, Object>> data_list;
    // TODO: Rename and change types of parameters
    private Context context;
    private co.example.administrator.wearabledevicebang.MainActivity MainActivity;

    //初始化程序入口
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.MainActivity = (MainActivity) context;
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

        }


    }

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //界面调用函数
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FmainHandler = new FmainHandler(mainActivity.mainFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

//    public void onClickedFragment(int id){
//        switch (id){
//            if(null==heartFragment){
//                heartFragment = new HeartFragment();
//            }
//            newFragment = heartFragment;
//            titleTv.setText = new HeartFragment;
//        }
//    }
}