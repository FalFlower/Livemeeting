package cn.edu.sdnu.i.livemeeting.activity.bmob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.VoteActivity;

/*
* 用户投票流程：
* 1.根据传入的key请求投票的相应数据
* 2.投票完成点提交，上传相应数据
* 3.上传成功后，关闭该页面，跳转至投票详情页面
* */
public class UsersVoteActivity extends AppCompatActivity{

    private static final int THIS_VOTE_LIST =0x00 ;
    private LinearLayout item_view[]=new LinearLayout[10];
    private CheckBox check[]=new CheckBox[10];
    private TextView this_item[]=new TextView[10];
    private TextView vote_name,vote_start_time,vote_content,vote_is_single;
    private TextView vote_end_time;
    private static Vote vote=new Vote();
    private static List<Vote> sVoteList=new ArrayList<>();

    private VoteResult mVoteResult=new VoteResult();
    private boolean isSingleChoose;
    private RadioGroup group;
    private static int checkCount;
    private static int checkFactNum=0;
    private static int[] checkSign=new int[10];
    private static int[] checkNum=new int[10];
    private Button goToVote;
    private String vote_id;
    private String vote_result_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_vote);
        initView();
        queryAllVotes();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String getResult=bundle.getString("VOTE_ID_RESULT");
        String []list=getResult.split(",");
        vote_id=list[0];
        vote_result_id=list[1];
//        Log.e("哈哈哈哈或或或或或",getResult);
        for(int i=0;i<10;i++){
            checkSign[i]=0;
        }
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 3)
    public void Event(final MessageEvent messageEvent) {
        sVoteList=messageEvent.getList();
        if(sVoteList.size()!=0){
            vote=sVoteList.get(sVoteList.size()-1);
            vote_name.setText("管理员");
            vote_content.setText(vote.getContent());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            vote_start_time.setText(sdf.format(date));
            if(vote.getSingle()==false){
                vote_is_single.setText("多选");
            }else{
                vote_is_single.setText("单选");
            }


            vote_start_time.setText(sdf.format(date));
            if(vote.getItem0()+""!=""){
                this_item[0].setText(vote.getItem0());
            }else{
                item_view[0].setVisibility(View.GONE);
            }
            if(vote.getItem1()+""!=""){
                this_item[1].setText(vote.getItem1());
            }else{
                item_view[1].setVisibility(View.GONE);
            }
            if(vote.getItem2()+""!=""){
                this_item[2].setText(vote.getItem2());
            }else{
                item_view[2].setVisibility(View.GONE);
            }
            if(vote.getItem3()+""!=""){
                this_item[3].setText(vote.getItem3());
            }else{
                item_view[3].setVisibility(View.GONE);
            }
            if(vote.getItem4()+""!=""){
                this_item[4].setText(vote.getItem4());
            }else{
                item_view[4].setVisibility(View.GONE);
            }
            if(vote.getItem5()+""!=""){
                this_item[5].setText(vote.getItem5());
            }else{
                item_view[5].setVisibility(View.GONE);
            }
            if(vote.getItem6()+""!=""){
                this_item[6].setText(vote.getItem6());
            }else{
                item_view[6].setVisibility(View.GONE);
            }
            if(vote.getItem7()+""!=""){
                this_item[7].setText(vote.getItem7());
            }else{
                item_view[7].setVisibility(View.GONE);
            }
            if(vote.getItem8()+""!=""){
                this_item[8].setText(vote.getItem8());
            }else{
                item_view[8].setVisibility(View.GONE);
            }

            if(vote.getItem9()+""!=""){
                this_item[9].setText(vote.getItem9());
            }else{
                item_view[9].setVisibility(View.GONE);
            }
            for(int i=0;i<10;i++){

                final int finalI = i;
                check[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(compoundButton.isChecked()){
                            if(isSingleChoose){
                                for(int j=finalI+1;j<10;j++){
                                    check[j].setChecked(false);
                                }
                                for(int j=finalI-1;j>=0;j--){
                                    check[j].setChecked(false);
                                }
                            }
                            checkSign[finalI]=1;
                        }else{
                            checkSign[finalI]=0;
                        }
                    }
                });
                goToVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vote_User vote_user = new Vote_User();
                        vote_user.setUserId(BmobUser.getCurrentUser(UsersVoteActivity.this).getObjectId());
                        vote_user.setVoteId(vote_id);
                        vote_user.save(UsersVoteActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                            }
                        });

                        VoteResult voteResult = new VoteResult();
                        if (checkSign[0] == 1) voteResult.increment("result0", 1);
                        if (checkSign[1] == 1) voteResult.increment("result1", 1);
                        if (checkSign[2] == 1) voteResult.increment("result2", 1);
                        if (checkSign[3] == 1) voteResult.increment("result3", 1);
                        if (checkSign[4] == 1) voteResult.increment("result4", 1);
                        if (checkSign[5] == 1) voteResult.increment("result5", 1);
                        if (checkSign[6] == 1) voteResult.increment("result6", 1);
                        if (checkSign[7] == 1) voteResult.increment("result7", 1);
                        if (checkSign[8] == 1) voteResult.increment("result8", 1);
                        if (checkSign[9] == 1) voteResult.increment("result9", 1);
                        for(int i=0;i<10;i++){
                            Log.e("快捷方式的和会计师大后方", String.valueOf(checkSign[i]));
                        }



                        voteResult.update(UsersVoteActivity.this,vote_result_id,new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(UsersVoteActivity.this, "投票成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(UsersVoteActivity.this, "投票失败，请检查网络连接", Toast
                                        .LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else{
            Toast.makeText(UsersVoteActivity.this, "当前没有发起任何投票哦", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void Logic(MessageEvent messageEvent){
        sVoteList=messageEvent.getList();
        isSingleChoose=sVoteList.get(sVoteList.size()-1).getSingle();
        if(isSingleChoose){
            checkCount=1;
        }
    }

    private void queryAllVotes() {

        BmobQuery<Vote> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(UsersVoteActivity.this,new FindListener<Vote>() {
            @Override
            public void onSuccess(List<Vote> votes) {
                EventBus.getDefault().post(new MessageEvent(votes));
            }

            @Override
            public void onError(int i, String s) {
                Log.i("bmob","查询数据失败：");
            }

        });
    }

    //绑定控件
    private void initView() {
        item_view[0]=findViewById(R.id.view0);
        item_view[1]=findViewById(R.id.view1);
        item_view[2]=findViewById(R.id.view2);
        item_view[3]=findViewById(R.id.view3);
        item_view[4]=findViewById(R.id.view4);
        item_view[5]=findViewById(R.id.view5);
        item_view[6]=findViewById(R.id.view6);
        item_view[7]=findViewById(R.id.view7);
        item_view[8]=findViewById(R.id.view8);
        item_view[9]=findViewById(R.id.view9);

        this_item[0]=findViewById(R.id.this_item0);
        this_item[1]=findViewById(R.id.this_item1);
        this_item[2]=findViewById(R.id.this_item2);
        this_item[3]=findViewById(R.id.this_item3);
        this_item[4]=findViewById(R.id.this_item4);
        this_item[5]=findViewById(R.id.this_item5);
        this_item[6]=findViewById(R.id.this_item6);
        this_item[7]=findViewById(R.id.this_item7);
        this_item[8]=findViewById(R.id.this_item8);
        this_item[9]=findViewById(R.id.this_item9);

        check[0]=findViewById(R.id.check0);
        check[1]=findViewById(R.id.check1);
        check[2]=findViewById(R.id.check2);
        check[3]=findViewById(R.id.check3);
        check[4]=findViewById(R.id.check4);
        check[5]=findViewById(R.id.check5);
        check[6]=findViewById(R.id.check6);
        check[7]=findViewById(R.id.check7);
        check[8]=findViewById(R.id.check8);
        check[9]=findViewById(R.id.check9);

        vote_name=findViewById(R.id.starter_name);
        vote_start_time=findViewById(R.id.start_time);
        vote_content=findViewById(R.id.vote_content);
        vote_is_single=findViewById(R.id.is_single_get);
//        vote_image=findViewById(R.id.vote_pic_get);
        vote_end_time=findViewById(R.id.end_time_get);
        goToVote=findViewById(R.id.vote_vote_vote);
    }


}
