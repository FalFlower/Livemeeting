package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.sdnu.i.livemeeting.R;

public class ShowAddFriendsInformationView extends LinearLayout {
    private ImageView mAvatar;
    private TextView mNickNameAndId;
    private ImageView mGender;
    private TextView mLocation;
    public ShowAddFriendsInformationView(Context context) {
        super(context);
        init();
    }

    public ShowAddFriendsInformationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowAddFriendsInformationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_add_friends_information, this
                , true);
        findViews();
    }

    private void findViews() {
        mAvatar=findViewById(R.id.friend_ava);
        mNickNameAndId=findViewById(R.id.friend_nick_and_id);
        mGender=findViewById(R.id.friend_gender);
        mLocation=findViewById(R.id.friend_location);
    }

    public void setmAvatar( int resId) {
        this.mAvatar.setImageResource(resId);
    }

    public ImageView getmAvatar() {
        return mAvatar;
    }

    public void setmNickNameAndId(String nameAndId) {
        this.mNickNameAndId.setText(nameAndId);
    }

    public void setmGender( int resId) {
        this.mGender.setImageResource(resId);
    }

    public ImageView getmGender() {
        return mGender;
    }

    public void setmLocation(String location) {
        this.mLocation.setText(location);
    }
}
