package cn.edu.sdnu.i.livemeeting.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import cn.bmob.v3.listener.SaveListener;
import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MyUser;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;

public class StartActivity extends AppCompatActivity {



    private static final long SPLASH_DELAY_MILLIS = 2200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_start);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        },SPLASH_DELAY_MILLIS);
    }

    private void goHome() {
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember=pref.getBoolean("remember_password",false);
        boolean isAutoLogin=pref.getBoolean("auto_login",false);
        if (isRemember){
            //将账户密码都设置到文本框中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            if (isAutoLogin){
                doLogin(account,password);
            }else {
                Intent intent=new Intent();
                intent.setClass(this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }else {
            Intent intent=new Intent();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void doLogin(final String account, String password) {
        //调用腾讯IM登录
        ILiveLoginManager.getInstance().tlsLogin(account, password, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                //登陆成功。
                loginLive(account, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
            }
        });
    }


    private void loginLive(final String accountStr, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                Toast.makeText(StartActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, MainActivity.class);
                startActivity(intent);
                getSelfInfo();
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败

            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(StartActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                LiveApplication.getApplication().setSelfProfile(timUserProfile);

                //（二）每次启动的时候登陆“用户","123456"
                MyUser bu2 = new MyUser();
                bu2.setUsername(timUserProfile.getIdentifier());
                bu2.setPassword("123456");
                bu2.setUserId(timUserProfile.getIdentifier());
                bu2.login(StartActivity.this,new SaveListener() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
                checkIsFirstLogin();
            }
        });
    }

    private void checkIsFirstLogin() {
        SharedPreferences sp=getSharedPreferences("FirstLogin",MODE_PRIVATE);
        boolean isFirstLogin=sp.getBoolean("firstLogin",true);
        if (isFirstLogin) {
        }else {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
