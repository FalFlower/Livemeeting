package cn.edu.sdnu.i.livemeeting.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MyUser;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.info.Meet_User;

public class JoinMeetingActivity extends AppCompatActivity {

    private TextView title;
    private TextView leader;
    private TextView section;
    private TextView local;
    private TextView time;

    private Button cancle;
    private Button ok;
    private String objectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        title=findViewById(R.id.join_meeting_title);
        leader=findViewById(R.id.join_meeting_leader);
        section=findViewById(R.id.join_meeting_section);
        local=findViewById(R.id.join_meeting_local);
        time=findViewById(R.id.join_meeting_time);
        cancle=findViewById(R.id.join_btn_cancle);
        ok=findViewById(R.id.join_btn_ok);

        Intent intent=getIntent();
        final String id=intent.getStringExtra("join_id");
        final String status=intent.getStringExtra("join_status");
        if (status.equals("live")){
            ok.setText("加入");
        }else if (status.equals("never")){
            ok.setText("预约");
        }
        //创建待获取公开信息的群组列表
        List<String> groupList = new ArrayList<String>();
        groupList.add(id);
        //获取群组公开信息
        TIMGroupManager.getInstance().getGroupPublicInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int code, String desc) {
                Toast.makeText(JoinMeetingActivity.this, "获取群组公开信息 错误码："+code, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                //此时TIMGroupDetailInfo只含有群公开资料，其余字段为空
                for (final TIMGroupDetailInfo timGroupDetailInfo : timGroupDetailInfos) {
                       List<String> users=new ArrayList<>();
                       users.add(timGroupDetailInfo.getGroupOwner());
                       TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                           @Override
                       public void onError(int i, String s) {
                       }
                       @Override
                       public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                           for (TIMUserProfile timUserProfile : timUserProfiles) {
                               title.setText( timGroupDetailInfo.getGroupName());
                               leader.setText(timUserProfile.getNickName());
                               String [] result=timGroupDetailInfo.getGroupIntroduction().split(",");
                               String sections,locals,times;
                               if (result.length==1){
                                   sections=result[0];
                                   locals="";
                                   times="";
                                   objectId="";
                               }else {
                                   sections=result[0];
                                   locals=result[1];
                                   times=result[2];
                                   objectId=result[3];
                               }
                               section.setText(sections);
                               local.setText(locals);
                               time.setText(times);
                           }
                       }
                    });
                }
            }
        });



        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinMeetingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加入群
                TIMGroupManager.getInstance().applyJoinGroup(id, "", new TIMCallBack() {
                    @java.lang.Override
                    public void onError(int code, String desc) {
                        Toast.makeText(JoinMeetingActivity.this, "加入群组失败 错误码："+code, Toast.LENGTH_SHORT).show();
                    }
                    @java.lang.Override
                    public void onSuccess() {
                        //TODO 跳转到群组界面
                        if (status.equals("live")){
                            Intent intent=new Intent(getApplication(),GroupChatActivity.class);
                            intent.putExtra("join_ok_id",id);
                            joinInMeet(id);
                            startActivity(intent);
                            finish();
                        }else if (status.equals("never")){
                            finish();
                        }


                    }
                });
            }
        });
    }

    private void joinInMeet(String id) {
        final Meet_User meetUser=new Meet_User();
        meetUser.setMeetId(objectId);
        meetUser.setUserId(BmobUser.getCurrentUser(this).getObjectId());
        meetUser.setComing(false);

        ////进行查询两种情况：1.starterId=当前用户id，2.MEET_USER表中已经存在当前meetId&&当前用户id
        boolean isContinue1=true;

        boolean isContinue2=true;
        BmobQuery<Meet_User> eq1 = new BmobQuery<Meet_User>();
        eq1.addWhereEqualTo("meetId",BmobUser.getCurrentUser(this).getObjectId());
        BmobQuery<Meet_User> eq2 = new BmobQuery<Meet_User>();
        eq2.addWhereEqualTo("userId", BmobUser.getCurrentUser(this).getObjectId());
        List<BmobQuery<Meet_User>> andQuerys = new ArrayList<BmobQuery<Meet_User>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Meet_User> query = new BmobQuery<Meet_User>();
        query.and(andQuerys);
        final boolean finalIsContinue = isContinue1;
        query.findObjects(getApplicationContext(),new FindListener<Meet_User>() {
            @Override
            public void onSuccess(List<Meet_User> list) {
                if(list.size()==0&& finalIsContinue){
                    meetUser.setUserId(BmobUser.getCurrentUser(JoinMeetingActivity.this).getObjectId());
                    meetUser.setComing(false);
                    meetUser.setUserName(BmobUser.getCurrentUser(JoinMeetingActivity.this).getUsername());
                    meetUser.save(JoinMeetingActivity.this,new SaveListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }

                    });
                }else if(list.size()!=0){
                    Toast.makeText(JoinMeetingActivity.this,
                            "你已经参加过这次会议了",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {

            }

        }
        );

    }

}
