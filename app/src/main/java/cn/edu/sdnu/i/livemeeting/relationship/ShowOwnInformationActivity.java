package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.LoginActivity;
import cn.edu.sdnu.i.livemeeting.info.CustomProfile;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.profile.ProfileSimpleTextView;

public class ShowOwnInformationActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ImageView mImageView;

    private ProfileSimpleTextView mId;
    private ProfileSimpleTextView mNickName;
    private ProfileSimpleTextView mSign;
    private ProfileSimpleTextView mGender;
    private ProfileSimpleTextView mLocation;
    private ProfileSimpleTextView mPositon;

    private Button mQuitLogin;

    private TIMUserProfile mUserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_show_own_information);
        findViews();
        setDefalutView();
        getSelfInfo();
    }
    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getParent().getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                mUserProfile = timUserProfile;
                updateViews(timUserProfile);
            }
        });
    }
    private void updateViews(TIMUserProfile timUserProfile) {
        //更新界面
        String faceUrl = timUserProfile.getFaceUrl();

        String name=timUserProfile.getNickName();
        mCollapsingToolbarLayout.setTitle(name);
        mNickName.setmContent(name);
        mCollapsingToolbarLayout.setTitle(name);
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";

        if (TextUtils.isEmpty(faceUrl)) {
            if (genderValue==1){
                ImgUtils.load(R.drawable.right_ava, mImageView);
            }else {
                ImgUtils.load(R.drawable.left_ava, mImageView);
            }
        } else {
            ImgUtils.load(faceUrl, mImageView);
        }
        mGender.setmContent(genderStr);
        mSign.setmContent(timUserProfile.getSelfSignature());
        mLocation.setmContent(timUserProfile.getLocation());
        mId.setmContent(timUserProfile.getIdentifier());
        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        mPositon.setmContent(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知"));

    }
    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null) {
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }
    public void instance(TIMUserProfile mUserProfile){
        this.mUserProfile=mUserProfile;
    }

    private void setDefalutView() {
        mId.setmTitle("账号");
        mNickName.setmTitle("昵称");
        mSign.setmTitle("个性签名");
        mGender.setmTitle("性别");
        mLocation.setmTitle("地区");
        mPositon.setmTitle("职位");

        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));

        mQuitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登出
                //TODO 偶尔出现Bug，需要点两次才能退出
                logout();
                MainActivity.instance.finish();
            }
        });
    }

    private void logout() {
        //登出
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Toast.makeText(getApplicationContext(),"登出失败 错误码："+code+"错误描述："+desc,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess() {
                //登出成功
                Toast.makeText(getApplicationContext(),"已注销",Toast.LENGTH_SHORT).show();
                //取消自动登录
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("auto_login",false);
                editor.apply();
                //转到登录界面
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        mCollapsingToolbarLayout=findViewById(R.id.collasing);
        mToolbar=findViewById(R.id.toolbar);
        mImageView=findViewById(R.id.avatar_background);
        mId=findViewById(R.id.simple_id);
        mNickName=findViewById(R.id.simple_nickname);
        mGender=findViewById(R.id.simple_gender);
        mLocation=findViewById(R.id.simple_location);
        mPositon=findViewById(R.id.simple_position);
        mSign=findViewById(R.id.simple_sign);
        mQuitLogin=findViewById(R.id.quit_login);
    }


}
