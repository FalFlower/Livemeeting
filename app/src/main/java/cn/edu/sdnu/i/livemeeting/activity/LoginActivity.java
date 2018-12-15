package cn.edu.sdnu.i.livemeeting.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import cn.bmob.v3.listener.SaveListener;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MyUser;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.dialog.LoadingDialog;

public class LoginActivity extends AppCompatActivity {

    public static LoginActivity instance=null;

    private EditText mAccountEdt;
    private EditText mPasswordEdt;
    private Button mLoginBtn;

    private FloatingActionButton fab;
    private CheckBox mRememberPwd;
    private CheckBox mAutoLogin;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_login);
        instance=this;
        findAllViews();
        setListeners();
        checkIsRememberOrAuto();
    }

    private void checkIsRememberOrAuto() {
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember=pref.getBoolean("remember_password",false);
        boolean isAutoLogin=pref.getBoolean("auto_login",false);
        if (isRemember){
            //将账户密码都设置到文本框中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            mAccountEdt.setText(account);
            mPasswordEdt.setText(password);
            mRememberPwd.setChecked(true);
            if (isAutoLogin){
                doLogin(account,password);
            }
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
                Toast.makeText(instance,"tlsLogin 登录失败 "+module+"   "+errCode+"   "+errMsg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkIsFirstLogin() {
        SharedPreferences sp=getSharedPreferences("FirstLogin",MODE_PRIVATE);
        boolean isFirstLogin=sp.getBoolean("firstLogin",true);
        if (isFirstLogin) {
//            Intent intent = new Intent();
//            intent.setClass(LoginActivity.this,MyInfoFragment.class);//暂时还有问题
//            startActivity(intent);
        }else {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void findAllViews() {
        mAccountEdt = findViewById(R.id.mEditAccount);
        mPasswordEdt = findViewById(R.id.mEditPwd);
        mLoginBtn =  findViewById(R.id.mLoginBtn);
        fab=findViewById(R.id.fab);

        mRememberPwd=findViewById(R.id.check_remember_password);
        mAutoLogin=findViewById(R.id.check_auto_login);

    }

    private void setListeners() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//登录操作
//                UIHelper.showDialogForLoading(getApplication(),"加载中...");
                new LoadingDialog(instance).setMessage("正在加载...").show();
                login();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });
    }

    private void register() {
        //注册新用户，跳转到注册页面。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(null);
            getWindow().setEnterTransition(null);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
            //跳转到注册界面
            startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
        } else {
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

    private void login() {
        final String accountStr = mAccountEdt.getText().toString();
        final String passwordStr = mPasswordEdt.getText().toString();

        //调用腾讯IM登录
        ILiveLoginManager.getInstance().tlsLogin(accountStr, passwordStr, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                //登陆成功。
                //是否保存账户密码
                editor=pref.edit();
                if (mRememberPwd.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",accountStr);
                    editor.putString("password",passwordStr);
                }else {
                    editor.clear();
                }
                if (mAutoLogin.isChecked()){
                    editor.putBoolean("auto_login",true);
                }
                editor.apply();

                loginLive(accountStr, data);

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(instance,"tlsLogin 登录失败 "+module+"   "+errCode+"   "+errMsg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginLive(final String accountStr, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                getSelfInfo();
                new LoadingDialog(getApplication()).setMessage("正在加载...").hide();
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(instance,"iLiveLogin 登录失败",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(LoginActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
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
                bu2.login(LoginActivity.this,new SaveListener() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("bomb_login","登陆失败");
//                        Toast.makeText(MainActivity.instance, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                });
                checkIsFirstLogin();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        new LoadingDialog(getApplication()).setMessage("正在加载...").hide();
    }
}
