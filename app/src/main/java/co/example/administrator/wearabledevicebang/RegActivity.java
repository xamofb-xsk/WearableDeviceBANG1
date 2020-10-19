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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import activity.lsen.wearabledevice.entity.User;
import co.example.administrator.wearabledevicebang.tools.HttpSend;
import co.example.administrator.wearabledevicebang.tools.LoadingDialog;

public class RegActivity extends AppCompatActivity {
    User user;
    Button regbtu1;
    EditText regetpwd,regetuser,regetpwd2;
    public String regpwd,regusername,regpwd2, realpwd;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        regbtu1 = (Button)findViewById(R.id.regbtu1);
        regetpwd = (EditText)findViewById(R.id.regetpwd);
        regetpwd2 = (EditText)findViewById(R.id.regetpwd2);
        regetuser = (EditText)findViewById(R.id.regetuser);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        regbtu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regusername = regetuser.getText().toString();
                regpwd = regetpwd.getText().toString();
                regpwd2 = regetpwd2.getText().toString();
//                boolean ise = isEmpty();
//                if(ise){
//                    if(regpwd.equals(regpwd2)) {
                        realpwd = regpwd;
                        User user = new User();
                        user.setType("register");
                        user.setUserName("王娟");
                        user.setUserPassword("123456");
                        Register(user);
//                    }else{
//                        Toast.makeText(RegActivity.this,"密码不一致请重试",Toast.LENGTH_SHORT).show();
//                    }
                }



//            }
        });
    }
    private boolean isEmpty(){
        if(regusername.equals("")){
            Toast.makeText(RegActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
        }
        if(regpwd.equals("")){
            Toast.makeText(RegActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
        }
        if(regpwd2.equals("")){
            Toast.makeText(RegActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
        }
        return !regusername.equals("") && !regpwd.equals("") && !regpwd2.equals("");
    }

    private void Register(final User use){
        LoadingDialog.show(RegActivity.this,"请稍等...");
        new Thread(new Runnable(){
            @Override
            public void run() {
                Looper.prepare();
//                String url = "http://192.168.1.105:9401/WearableDevice/DataServlet";
                String url = "http://192.168.1.115:9401/WearableDevice/DataServlet";
                Object o = HttpSend.SendObject(url, use);
                //System.out.println(Ob);
                if(o != null)
                {
                    user = (User) o;
                    if(user.getType().equals("success")){
//                    if(user == null){
                        LoadingDialog.dismiss(RegActivity.this);
                        Intent intent = new Intent(RegActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else{
                        LoadingDialog.dismiss(RegActivity.this);
                        Toast.makeText(RegActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    }
                }else{
                    LoadingDialog.dismiss(RegActivity.this);
                    LoadingDialog.show(RegActivity.this,"无法连接服务器");
//                            Toast.makeText(LoginActivity.this,"无法连接服务器",Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        }).start();;
    }

}