package cn.edu.sdnu.i.livemeeting.relationship;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.sdnu.i.livemeeting.R;

public class InGroupShowFirendsListView extends LinearLayout {

    private ImageView mAvatar;
    private TextView mName;

    public InGroupShowFirendsListView(Context context) {
        super(context);
        init();
    }

    public InGroupShowFirendsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InGroupShowFirendsListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_friends_group_list, this
                , true);
//        this.mGroupFriendsList=null;
        findAllViews();
    }



    private void findAllViews() {
        mAvatar=findViewById(R.id.friends_avar);
        mName=findViewById(R.id.friends_name);
    }

    public void setmAvatar(int resId) {
        this.mAvatar .setImageResource(resId);
    }

    public void setmName(String name) {
        this.mName .setText(name);
    }

    public ImageView getmAvatar() {
        return mAvatar;
    }

    public TextView getmName() {
        return mName;
    }
}
