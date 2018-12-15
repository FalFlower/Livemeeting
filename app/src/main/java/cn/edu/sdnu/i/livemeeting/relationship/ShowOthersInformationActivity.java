package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.SingleChatActivity;
import cn.edu.sdnu.i.livemeeting.dialog.EditStrProfileDialog;
import cn.edu.sdnu.i.livemeeting.info.CustomProfile;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.profile.ProfileSimpleTextView;

public class ShowOthersInformationActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ImageView mBacView;

    private ProfileSimpleTextView mId;
    private ProfileSimpleTextView mNickName;
    private ProfileSimpleTextView mRemark;
    private ProfileSimpleTextView mSign;
    private ProfileSimpleTextView mGender;
    private ProfileSimpleTextView mLocation;
    private ProfileSimpleTextView mPositon;
    private ProfileSimpleTextView mGroup;

    private Button mLeftBtn;
    private Button mRightBtn;

    private List<String> users ;
    private String userId;

    private ShowOthersInformationActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_others_information);
        findViews();
        setDefalutView();
        isFriend();
        getSelfInfo();
        setClick();
    }

    private void setClick() {
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 加好友
                Intent intent=new Intent(getApplicationContext(), ShowAddFriendActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 聊天

            Intent intent=new Intent(getApplicationContext(), SingleChatActivity.class);
            intent.putExtra("chat_id",userId);
            startActivity(intent);
            }
        });
        mRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 修改备注
                EditStrProfileDialog dialog = new EditStrProfileDialog(instance);
                dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
                    @Override
                    public void onOk(String title, final String content) {
                        TIMFriendshipManager.getInstance().setFriendRemark(userId, content,
                                new TIMCallBack() {//回调接口
                                    @Override
                                    public void onSuccess() {//成功
                                        getSelfInfo();
                                    }

                                    @Override
                                    public void onError(int code, String desc) {//失败

                                    }
                                });
                    }
                });
                dialog.show("备注", R.drawable.mine,"" );
            }
        });
    }


    private void findViews() {
        instance=this;
        mCollapsingToolbarLayout=findViewById(R.id.collasing_O);
        mToolbar=findViewById(R.id.toolbar_O);
        mBacView=findViewById(R.id.personality_background);

        mId=findViewById(R.id.simple_id_o);
        mNickName=findViewById(R.id.simple_nickname_o);
        mGender=findViewById(R.id.simple_gender_o);
        mRemark=findViewById(R.id.simple_remark_o);
        mLocation=findViewById(R.id.simple_location_o);
        mPositon=findViewById(R.id.simple_position_o);
        mSign=findViewById(R.id.simple_sign_o);
        mGroup=findViewById(R.id.simple_group_o);

        mLeftBtn=findViewById(R.id.btn_left);
        mRightBtn=findViewById(R.id.btn_right);


    }
    private void setDefalutView() {
        //TODO 更新默认信息
        mId.setmTitle("账号");
        mNickName.setmTitle("昵称");
        mRemark.setmTitle("备注");
        mSign.setmTitle("个性签名");
        mGender.setmTitle("性别");
        mLocation.setmTitle("地区");
        mPositon.setmTitle("职位");
        mGroup.setmTitle("分组");

        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        users= new ArrayList<String>();
        Intent intent=getIntent();
        if (intent != null) {
            userId=intent.getStringExtra("userId");
        }
        users.add(userId);
    }
    private void getSelfInfo() {
        //TODO 获取信息
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                //更新信息
                updateViews(timUserProfiles);
            }
        });
    }

    private void updateViews(List<TIMUserProfile> timUserProfiles) {
        for(TIMUserProfile res : timUserProfiles){
            updateTure(res);
        }
    }

    private void updateTure(TIMUserProfile timUserProfile) {
        //更新界面
        String faceUrl = timUserProfile.getFaceUrl();
        String name=timUserProfile.getNickName();
        if (name.isEmpty()){
            mNickName.setmContent("无");
            mCollapsingToolbarLayout.setTitle(" ");
        }else {
            mNickName.setmContent(name);
            mCollapsingToolbarLayout.setTitle(name);
        }

        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        if (TextUtils.isEmpty(faceUrl)) {
            if (genderValue==1){
                ImgUtils.load(R.drawable.right_ava,mBacView);
            }else {
                ImgUtils.load(R.drawable.left_ava,mBacView);
            }
        } else {
            ImgUtils.load(faceUrl, mBacView);
        }
        mGender.setmContent(genderStr);
        if (timUserProfile.getSelfSignature().isEmpty())
            mSign.setmContent("无");
        else
            mLocation.setmContent(timUserProfile.getSelfSignature());

        if (timUserProfile.getSelfSignature().isEmpty())
            mSign.setmContent("无");
        else
            mLocation.setmContent(timUserProfile.getLocation());
        mId.setmContent(timUserProfile.getIdentifier());
        mGroup.setmContent("我的好友");
        if (timUserProfile.getSelfSignature().isEmpty())
            mRemark.setmContent("无");
        else
            mRemark.setmContent(timUserProfile.getRemark());
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

    public void isFriend() {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                mRemark.setVisibility(View.GONE);
                mGroup.setVisibility(View.GONE);
            }
            @Override
            public void onSuccess(List<TIMUserProfile> result){
                for(TIMUserProfile res : result){
                    if (res.getIdentifier().equals(userId)){
                        mLeftBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

}
