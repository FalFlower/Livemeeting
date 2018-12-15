package cn.edu.sdnu.i.livemeeting.activity.bmob;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import cn.edu.sdnu.i.livemeeting.R;

/**
 * Created by Royal pioneer on 2018/4/10.
 * 自定义从底部弹出的Dialog，对应了相应的dialog_vote_and_lottery.xml,还有在activity中相应的实现
 */
public class ChooseBottomDialog extends Dialog {
    private Button vote,lottery,cancel;
    //第二个参数是dialog适配的style，可以自定义（NormalDialogStyle）
    public ChooseBottomDialog(@NonNull Context context, int style) {
        super(context, style);
    }

    //创建点击接口和点击事件（即接口实例）
    public interface onVoteOnclickListener {
        public void onVoteClick();
    }
    public interface onLotteryOnclickListener {
        public void onLotteryClick();
    }
    public interface onCancelOnclickListener {
        public void onCancelClick();
    }
    private onVoteOnclickListener voteOnclickListener;
    private onLotteryOnclickListener lotteryOnclickListener;
    private onCancelOnclickListener cancelOnclickListener;
    //接口相应接口设置器
    public void setVoteOnclickListener(onVoteOnclickListener onNoOnclickListener) {
        this.voteOnclickListener= onNoOnclickListener;
    }
    public void setLotteryOnclickListener(onLotteryOnclickListener onNoOnclickListener) {
        this.lotteryOnclickListener= onNoOnclickListener;
    }
    public void setCancelOnclickListener(onCancelOnclickListener onNoOnclickListener) {
        this.cancelOnclickListener= onNoOnclickListener;
    }
    //Dialog也有onCreate()方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面控件
        initEvent(); //初始化界面控件的事件
    }
    //将相应的点击事件转换为接口中的抽象方法
    private void initEvent() {
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(voteOnclickListener!=null){
                    voteOnclickListener.onVoteClick();
                }
            }
        });
        lottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lotteryOnclickListener!=null){
                    lotteryOnclickListener.onLotteryClick();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cancelOnclickListener!=null){
                    cancelOnclickListener.onCancelClick();
                }
            }
        });
    }
    //初始化dialog的界面
    private void initView() {
        vote=findViewById(R.id.single);
        lottery=findViewById(R.id.multiple);
        cancel=findViewById(R.id.cancel_out_choose);
    }
}
