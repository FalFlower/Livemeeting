package cn.edu.sdnu.i.livemeeting.profile;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.sdnu.i.livemeeting.R;

public class ProfileSimpleTextView extends LinearLayout {
    private TextView mTitle;
    private TextView mContent;

    public ProfileSimpleTextView(Context context) {
        super(context);
        init();
    }

    public ProfileSimpleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfileSimpleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_profile, this
                , true);
        findAllViews();
    }

    private void findAllViews() {
        mTitle=findViewById(R.id.title_simple);
        mContent=findViewById(R.id.content_simple);
    }
    public void set(String title,String content) {
        mTitle.setText(title);
        mContent.setText(content);
    }

    public void setmTitle(String title) {
       mTitle.setText(title);
    }

    public void setmContent(String content){
        mContent.setText(content);
    }

    public String getmContent() {
        return mContent.getText().toString();
    }
}
