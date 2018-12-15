package cn.edu.sdnu.i.livemeeting.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MessageEvent;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MyUser;
import cn.edu.sdnu.i.livemeeting.adapter.CreateGroupAdaptar;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.info.Meet;
import cn.edu.sdnu.i.livemeeting.info.Meet_User;
import cn.edu.sdnu.i.livemeeting.util.CalendarUtil;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;
import cn.edu.sdnu.i.livemeeting.weiget.CalendarView;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;
import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;

public class CreateMeetingActivity extends AppCompatActivity {
    private EditText title;

    private EditText section;
    private EditText local;
    private RecyclerView groupAva;
    private ImageView plus;
    private Button send;

    private EditText timeYear;
    private EditText timeMonth;
    private EditText timeDay;

    private CreateGroupAdaptar createGroupAdaptar;
    private List<String> times;
    private List<String> avaList;
    private List<String> users;//用户id
    public static Meet meet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_create_meeting);
        avaList = new ArrayList<>();
        users = new ArrayList<>();
        init();
        setClick();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void event(MessageEvent messageEvent){
        meet=messageEvent.getMeet();
        Log.e("this",meet.getStarterId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (avaList.size() != 0) {
                    groupAva.removeAllViews();
                }
                if (resultCode == RESULT_OK) {
                    String userId = data.getStringExtra("choose_id");
                    List<String> stringList = new ArrayList<>();
                    for (String user : users) {
                        if (user.equals(userId))
                            return;
                    }
                    if (!userId.isEmpty()) {
                        stringList.add(userId);
                        users.add(userId);
                        TIMFriendshipManager.getInstance().getUsersProfile(stringList, new TIMValueCallBack<List<TIMUserProfile>>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                                String face = timUserProfiles.get(0).getFaceUrl();
                                if (!face.isEmpty()) {
                                    avaList.add(face);
                                } else {
                                    long genderValue = timUserProfiles.get(0).getGender().getValue();
                                    String genderStr = genderValue == 1 ? "男" : "女";
                                    avaList.add(genderStr);
                                }
                            }
                        });
                        CreateGroupAdaptar createGroupAdaptar = new CreateGroupAdaptar(getContext(), avaList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        groupAva.setLayoutManager(linearLayoutManager);
                        groupAva.setAdapter(createGroupAdaptar);
                    }
                }
                break;
            default:
                break;
        }

    }

    private void setClick() {
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChooseFriendsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDate()) {
                    Intent intent = new Intent(CreateMeetingActivity.this, MainActivity.class);
                    if (users.isEmpty()) {
                        users.add(LiveApplication.getApplication().getSelfProfile().getIdentifier());
                    }
                    createGroup();
                    startActivity(intent);
                    Toast.makeText(MainActivity.instance, "如有数据未及时显示，请手动刷动刷新一两下", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateMeetingActivity.this, "请填写信息完整", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendBmob(String s) {
        String dateS = timeYear.getText().toString() + "-" + timeMonth.getText().toString() + "-" + timeDay.getText().toString();
        meet = new Meet();
        meet.setName(title.getText().toString());
//            meet.setStarter(starter.getText().toString());
        meet.setTime(dateS);
        meet.setId(s);

        meet.setAddress(local.getText().toString());
        meet.setContent(section.getText().toString());
        meet.setStarterId(BmobUser.getCurrentUser(getContext()).getObjectId());
//            meet.setImage(null);
        meet.save(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(new MessageEvent(meet));
                Meet_User meet_user=new Meet_User();
                meet_user.setUserName(BmobUser.getCurrentUser(CreateMeetingActivity.this).getUsername());
                meet_user.setMeetId(meet.getObjectId());
                meet_user.setComing(true);
                meet_user.setUserId(BmobUser.getCurrentUser(CreateMeetingActivity.this).getObjectId());
                meet_user.save(CreateMeetingActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CreateMeetingActivity.this, "创建数据失败：" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        title = findViewById(R.id.create_edt_title);
        section = findViewById(R.id.create_edt_section);
        local = findViewById(R.id.create_edt_local);
        send = findViewById(R.id.create_btn_send);
        groupAva = findViewById(R.id.create_group_avatar);
        plus = findViewById(R.id.create_plus);

        timeYear = findViewById(R.id.time_year);
        timeMonth = findViewById(R.id.time_month);
        timeDay = findViewById(R.id.time_day);
        times = new ArrayList<>();
        if (times.isEmpty()) {
            times.add("2018");
            times.add("04");
            times.add("13");
        }
    }

    private void createGroup() {
        //创建待加入群组的用户列表
        //创建回调
        TIMValueCallBack<String> cb = new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Toast.makeText(CreateMeetingActivity.this, "创建群组会议失败 错误码：" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) { //回调返回创建的群组Id
                sendBmob(s);
                //设置默认群属性
                //加入信息，将*作为分隔符
                String month = "",day="";
                if (timeMonth.length()==1){
                    month="0"+timeMonth.getText().toString();
                }else
                    month=timeMonth.getText().toString();
                if (timeDay.length()==1){
                    day="0"+timeDay.getText().toString();
                }else
                    day=timeDay.getText().toString();
                String date = timeYear.getText().toString() + "-" + month + "-" +day ;
                String info = section.getText().toString() + "," + local.getText().toString() + "," + date+","+CreateMeetingActivity.meet.getObjectId();
                TIMGroupManager.getInstance().modifyGroupIntroduction(s, info, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
                //修改群头像url
                TIMGroupManager.getInstance().modifyGroupFaceUrl(s, "http://p5tgr5sc2.bkt.clouddn.com/iiiiiiii_1523626853699_avatar", new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });

                TIMGroupManager.getInstance().modifyGroupAddOpt(s, TIMGroupAddOpt.TIM_GROUP_ADD_ANY, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
            }
        };
//创建群组
        TIMGroupManager.getInstance().createGroup(
                "ChatRoom",          //群组类型: 目前仅支持私有群
                users,               //待加入群组的用户列表
                title.getText().toString(), //群组名称
                cb);                //回调
    }

    private boolean checkDate() {
        String year = timeYear.getText().toString();
        String month = timeMonth.getText().toString();
        String day = timeDay.getText().toString();
        String yearRegex = "20\\d\\d";
        String monthRegex = "\\d+";
        String dayRegex = "\\d+";
        return (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(month) || !TextUtils.isEmpty(day)) && (year.matches(yearRegex) && month.matches(monthRegex) && day.matches(dayRegex));
    }
}
