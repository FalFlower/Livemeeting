package cn.edu.sdnu.i.livemeeting.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.ChooseBottomDialog;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MessageEvent;
import cn.edu.sdnu.i.livemeeting.activity.bmob.PathGetter;
import cn.edu.sdnu.i.livemeeting.activity.bmob.ScreenSizeUtils;
import cn.edu.sdnu.i.livemeeting.activity.bmob.Vote;
import cn.edu.sdnu.i.livemeeting.activity.bmob.VoteResult;

/*
* 创建投票流程如下：
* 1.填写投票相关信息
* 2.上传到数据库（上传成功后退出页面）
* 3.退出页面同时，将该投票的key压入voteStack中，等待群聊页面加载时请求
* （异步操作时，主线程均要显示等待进度框）
* */
public class VoteActivity extends AppCompatActivity {

    private static final int IMAGE_CODE =0x00 ;
    private static final int RESIZE_CODE =0x01 ;
    private EditText content;
    private EditText item[]=new EditText[10];
    private static int item_num=0;
    private LinearLayout layout[]=new LinearLayout[8];
    private Uri uri;
    private static int count=0;
    private boolean isContinueElse=true;
    private boolean isNoName=false,isSingle=true;
    private TextView textView0;
    private TextView textView1;
    public static String voteId;
    public static String voteResultId;
    public static String voteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent){
        voteId=messageEvent.getVoteId();
        voteResultId=messageEvent.getVoteResultId();
        voteName=messageEvent.getVoteName();
        Log.e("asffsa",messageEvent.getVoteId());
        Log.e("8736478678wre6",messageEvent.getVoteResultId());
//        EventBus.getDefault().post(messageEvent);
        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("VOTE_KEY",voteId + "," + voteResultId+","+content.getText().toString());
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void initView() {

        textView0=findViewById(R.id.notification_time_text);
        textView1=findViewById(R.id.end_time_text);
        content=findViewById(R.id.content);
        item[0]=findViewById(R.id.item0);
        item[1]=findViewById(R.id.item1);
        item[2]=findViewById(R.id.item2);
        item[3]=findViewById(R.id.item3);
        item[4]=findViewById(R.id.item4);
        item[5]=findViewById(R.id.item5);
        item[6]=findViewById(R.id.item6);
        item[7]=findViewById(R.id.item7);
        item[8]=findViewById(R.id.item8);
        item[9]=findViewById(R.id.item9);

        layout[0]=findViewById(R.id.layout2);
        layout[1]=findViewById(R.id.layout3);
        layout[2]=findViewById(R.id.layout4);
        layout[3]=findViewById(R.id.layout5);
        layout[4]=findViewById(R.id.layout6);
        layout[5]=findViewById(R.id.layout7);
        layout[6]=findViewById(R.id.layout8);
        layout[7]=findViewById(R.id.layout9);

    }

    /*
    * 填写投票相关信息
    * 以下分别是：添加选项，删除选项，选择单选和多选，选择结束时间，提醒时间，和是否匿名，添加投票的图片
    * */
    public void addVoteItem(View view) {
        if(item_num<=7){
            layout[item_num].setVisibility(View.VISIBLE);
            item_num+=1;
            Log.e("sdsfddsfsdf",item_num+"");
        }
    }

    public void deletVoteItem(View view) {
        if(item_num>0){
            layout[item_num-1].setVisibility(View.GONE);
            item_num-=1;

        }
    }
    public void single_or_multiple(View view0) {
        final ChooseBottomDialog mDialog=new ChooseBottomDialog(VoteActivity.this,R.style.NormalDialogStyle);
        View view = View.inflate(VoteActivity.this, R.layout.dialog_choose_single_or_multipe, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(VoteActivity.this).getScreenHeight() * 0.23f));
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(VoteActivity.this).getScreenWidth() * 0.9f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        mDialog.setCancelOnclickListener(new ChooseBottomDialog.onCancelOnclickListener() {
            @Override
            public void onCancelClick() {
                mDialog.cancel();
                mDialog.dismiss();
            }
        });
        mDialog.setVoteOnclickListener(new ChooseBottomDialog.onVoteOnclickListener() {
            @Override
            public void onVoteClick() {
                TextView textView=findViewById(R.id.vote_style);
                textView.setText("单选");
                isSingle=true;
                mDialog.dismiss();
            }
        });
        mDialog.setLotteryOnclickListener(new ChooseBottomDialog.onLotteryOnclickListener() {
            @Override
            public void onLotteryClick() {
                TextView textView=findViewById(R.id.vote_style);
                textView.setText("多选");
                isSingle=false;
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void setEndTime(View view) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(VoteActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {

                        textView1.setText(""+hourOfDay + ":" + minute);

                    }
                }
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                true).show();
    }

    public void notificate(View view) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(VoteActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {

                        textView0.setText(""+hourOfDay + ":" + minute);

                    }
                }
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                true).show();
    }

    public void isNoName(View view) {
        count+=1;
        if(count%2!=0){
            isNoName=true;
            Toast.makeText(this, "已开启匿名投票", Toast.LENGTH_SHORT).show();
        }else{
            isNoName=false;
        }


    }

    public void addVotePic(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");//图片
        startActivityForResult(galleryIntent,IMAGE_CODE);   //跳转，传递打开相册请求码

    }


    /*
    * 发布投票，上传到数据库（在这应该实现返回结果给上一个页面）
    * startActivityForResult（）
    * */
    public void commit_vote(View view) {

        final Vote vote=new Vote();
        vote.setContent(content.getText().toString());
        vote.setItem0(item[0].getText().toString());
        vote.setItem1(item[1].getText().toString());
        vote.setItem2(item[2].getText().toString());
        vote.setItem3(item[3].getText().toString());
        vote.setItem4(item[4].getText().toString());
        vote.setItem5(item[5].getText().toString());
        vote.setItem6(item[6].getText().toString());
        vote.setItem7(item[7].getText().toString());
        vote.setItem8(item[8].getText().toString());
        vote.setItem9(item[9].getText().toString());

        vote.setNoName(isNoName);
        vote.setNotificationTime(textView0.getText().toString());
        vote.setEndTime(textView1.getText().toString());
        vote.setSingle(isSingle);
        vote.setVotePic(null);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        vote.setStartTime(sdf.format(date));

        vote.save(this,new SaveListener() {
            @Override
            public void onSuccess() {
                final VoteResult voteResult=new VoteResult();
                if(vote.getItem0()+""!=""){ voteResult.setResult0(0); }else{voteResult.setResult0(null);}
                if(vote.getItem1()+""!=""){ voteResult.setResult1(0); }else{voteResult.setResult1(null);}
                if(vote.getItem2()+""!=""){ voteResult.setResult2(0); }else{voteResult.setResult2(null);}
                if(vote.getItem3()+""!=""){ voteResult.setResult3(0); }else{voteResult.setResult3(null);}
                if(vote.getItem4()+""!=""){ voteResult.setResult4(0); }else{voteResult.setResult4(null);}
                if(vote.getItem5()+""!=""){ voteResult.setResult5(0); }else{voteResult.setResult5(null);}
                if(vote.getItem6()+""!=""){ voteResult.setResult6(0); }else{voteResult.setResult6(null);}
                if(vote.getItem7()+""!=""){ voteResult.setResult7(0); }else{voteResult.setResult7(null);}
                if(vote.getItem8()+""!=""){ voteResult.setResult8(0); }else{voteResult.setResult8(null);}
                if(vote.getItem9()+""!=""){ voteResult.setResult9(0); }else{voteResult.setResult9(null);}
                voteResult.setVoteId(vote.getObjectId());
                voteResult.save(VoteActivity.this,new SaveListener() {
                    @Override
                    public void onSuccess() {


                        EventBus.getDefault().post(new MessageEvent(vote.getObjectId(),voteResult.getObjectId(),vote.getContent()));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(VoteActivity.this, "发起投票失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }});
                isContinueElse=false;

                Toast.makeText(VoteActivity.this, "投票发布成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(VoteActivity.this, "发布投票失败，请检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

