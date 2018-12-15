package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.util.AddFriendUtils;

public class ShowAddFrienDdetailedActivity extends AppCompatActivity {
    private TextView mBackUp;
    private EditText mRemark;
    private TextView mSetFriendGroup;
    private Button mSendRequest;
    private String mFriendValidation;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_add_frien_ddetailed);
        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        mFriendValidation=intent.getStringExtra("friend_validation");
        findViews();
        setClick();
    }

    private void setClick() {
        mBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(mFriendValidation,mRemark.getText().toString());
            }
        });
        mSetFriendGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加分组
            }
        });
    }

    private void sendRequest(String friendValidation,String remark) {
        //TODO 发送好友请求
        AddFriendUtils addFriendUtils=new AddFriendUtils(this,userId);
        addFriendUtils.doAddFriend(friendValidation,remark);
        this.finish();
        ShowAddFriendActivity.instance.finish();
    }


    private void findViews() {
        mBackUp=findViewById(R.id.back);
        mRemark=findViewById(R.id.set_friend_remark);
        mSetFriendGroup=findViewById(R.id.set_friend_group);
        mSendRequest=findViewById(R.id.send_request);
    }

}
