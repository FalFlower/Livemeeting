package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;


public class ShowAddFriendActivity extends AppCompatActivity {
    private ImageView mAvatarImg;
    private TextView mNickName;
    private TextView mId;
    private EditText mEditText;
    private Button mNextBtn;

    private List<String> users ;
    private String userId;
    public static ShowAddFriendActivity instance=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_add_friend);
        instance=this;

        users= new ArrayList<String>();
        Intent intent=getIntent();
        if (intent != null) {
            userId=intent.getStringExtra("userId");
        }
        users.add(userId);

        findViews();
        getSelfInfo();
        setClick();
    }

    private void setClick() {
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ShowAddFrienDdetailedActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("friend_validation",mEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void getSelfInfo() {
//TODO 获取信息
        //getFriendsProfile()获取好友的
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
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
        mNickName.setText(timUserProfile.getNickName());
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        if (TextUtils.isEmpty(faceUrl)) {
            if (genderValue==1){
                ImgUtils.loadRound(R.drawable.right_ava, mAvatarImg);

            }else {
                ImgUtils.load(R.drawable.left_ava, mAvatarImg);
            }
        } else {
            ImgUtils.loadRound(faceUrl, mAvatarImg);
        }
        mId.setText(timUserProfile.getIdentifier());
    }

    private void findViews() {
        mAvatarImg=findViewById(R.id.avatar_friend);
        mNickName=findViewById(R.id.nick_name_friend);
        mId=findViewById(R.id.id_friend);
        mEditText=findViewById(R.id.edit_wording_friend);
        mNextBtn=findViewById(R.id.next_btn_friend);
    }


}
