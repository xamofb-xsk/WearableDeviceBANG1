package co.example.administrator.wearabledevicebang;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import activity.lsen.wearabledevice.entity.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.example.administrator.wearabledevicebang.tools.HttpSend;
import co.example.administrator.wearabledevicebang.tools.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    public String username, pwd;
    public User user;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.tvlogin)
    TextView tvlogin;
    @BindView(R.id.tvreg)
    TextView tvreg;
    @BindView(R.id.etuser)
    EditText etuser;
    @BindView(R.id.etpwd)
    EditText etpwd;
    private Vibrator vibrator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
//                if(isCheck()){
                    User user = new User();
                    user.setType("login");
                    user.setUserName(etuser.getText().toString().trim());
                    user.setUserPassword(etpwd.getText().toString().trim());
                    login(user);
//                }

            }
        });
//        @OnClick({R.id.tvlogin,R.id.tvreg,R.id.btn1})
//                public void onView
        tvreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void login(final User use){
        LoadingDialog.show(LoginActivity.this,"请稍等...");
        new Thread(new Runnable(){
            @Override
            public void run() {
                Looper.prepare();
//                String url = "http://192.168.1.105:9401/WearableDevice/DataServlet";
                String url = "http://192.168.6.147/WearableDevice/DataServlet";
                Object o = HttpSend.SendObject(url, use);

                //System.out.println(Ob);
                if(o != null)
                {
                    user = (User) o;
                    if(user.getType().equals("success")){
//                    if(user == null){


                        LoadingDialog.dismiss(LoginActivity.this);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else{
                        LoadingDialog.dismiss(LoginActivity.this);
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    }
                }else{
                    LoadingDialog.dismiss(LoginActivity.this);
                    LoadingDialog.show(LoginActivity.this,"无法连接服务器");
//                            Toast.makeText(LoginActivity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                }
            Looper.loop();
            }
            }).start();;
    }

}
