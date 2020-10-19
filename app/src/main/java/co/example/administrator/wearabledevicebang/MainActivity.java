package co.example.administrator.wearabledevicebang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import activity.lsen.wearabledevice.entity.User;
import co.example.administrator.wearabledevicebang.Fragment.MainFragment;

public class MainActivity extends AppCompatActivity {
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void init(){
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("User");
        mainHandler = new MainHandler(this);
        if(currentFrament == null){
            mianFragment = new MainFragment();
            currentFrament = mainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_framement, currentFrament).commit();
            frontFragment = currentFrament;
        }
    }
}
