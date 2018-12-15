package cn.edu.sdnu.i.livemeeting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.CreateGroupAdaptar;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.GroupInfo;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;
import static com.tencent.TIMGroupMemberRoleType.Owner;
import static com.tencent.qalsdk.service.QalService.context;

public class GroupInfoActivity extends AppCompatActivity {
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ImageView mImageView;

    private TextView title;
    private TextView leader;
    private TextView section;
    private TextView local;
    private TextView time;
    private TextView memNum;

    private LinearLayout memLayout;
    private RecyclerView recyclerView;
    private Button manager;

    private Button quit;

    private boolean isOwner=false;
    private String groupId;
    private List<String> avaList;
    private List<GroupInfo> groupInfos;
    private List<String> memsId;
    private CreateGroupAdaptar createGroupAdaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        initViews();
        updateViews();
        setClick();
    }

    private void setClick() {
        memLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 跳转到会议成员信息详细界面
                Intent intent=new Intent(GroupInfoActivity.this,ShowGroupMemActivity.class);
                intent.putExtra("group_show_id",groupId);
                startActivity(intent);
            }
        });
        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 跳转管理会议界面
                 Intent intent=new Intent(GroupInfoActivity.this,ManagerActivity.class);
                 intent.putExtra("manger_group_id",groupId);
                 startActivity(intent);
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 退出会议/解散会议
                if (isOwner){
                       //解散群组
                       TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
                           @Override
                           public void onError(int code, String desc) {
                               Toast.makeText(GroupInfoActivity.this, "解散群组失败 错误码："+code, Toast.LENGTH_SHORT).show();
                           }
                           @Override
                           public void onSuccess() {
                               GroupChatActivity.instance.finish();
                               finish();
                               Toast.makeText(GroupInfoActivity.this, "解散成功", Toast.LENGTH_SHORT).show();
                           }
                       });
                }else {
                    TIMGroupManager.getInstance().quitGroup(
                            groupId,  //群组Id
                            new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    Toast.makeText(GroupInfoActivity.this, "退出群组失败 错误码："+i, Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onSuccess() {
                                    GroupChatActivity.instance.finish();
                                    finish();
                                    Toast.makeText(GroupInfoActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void updateViews() {
//创建待获取信息的群组Id列表
        ArrayList<String> groupList = new ArrayList<String>();
        groupList.add(groupId);
//获取群组详细信息
        TIMGroupManager.getInstance().getGroupDetailInfo(
                groupList, //需要获取信息的群组Id列表
                new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(GroupInfoActivity.this, "获取群组消息失败 错误码："+i, Toast.LENGTH_SHORT).show();
                    }
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                        for(TIMGroupDetailInfo info : timGroupDetailInfos) {
                            if (!info.getGroupOwner().equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                                //创建者不是自己
                                List<String> users=new ArrayList<>();
                                users.add(info.getGroupOwner());
                                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                                    @Override
                                    public void onError(int i, String s) {
                                        Toast.makeText(getApplication(), "获取群主信息失败：" + s, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                                        //更新信息
                                        for(TIMUserProfile timUserProfile : timUserProfiles){
                                            //更新界面
                                            leader.setText(timUserProfile.getNickName());
                                        }
                                    }
                                });
                            }else {
                                leader.setText(LiveApplication.getApplication().getSelfProfile().getNickName());
                                quit.setText("解散会议");
                                isOwner=true;
                            }
                            if (!info.getFaceUrl().isEmpty())
                                Glide.with(getContext())
                                .load(info.getFaceUrl())
                                .into(mImageView);
                            else
                                ImgUtils.load(R.drawable.home_title_1,mImageView);
                            mCollapsingToolbarLayout.setTitle(" ");
                            title.setText(info.getGroupName());
                            memNum.setText(info.getMemberNum()+"人");
                            String [] result=info.getGroupIntroduction().split(",");
                            String sections,locals,times;
                            if (result.length==1){
                                sections=result[0];
                                locals="";
                                times="";
                            }else {
                                sections=result[0];
                                locals=result[1];
                                times=result[2];
                            }
                            section.setText(sections);
                            local.setText(locals);
                            time.setText(times);

                        }
                    }
                });
    }


    private void initViews() {
        mCollapsingToolbarLayout=findViewById(R.id.group_collapsing);
        mToolbar=findViewById(R.id.group_toolbar);
        mImageView=findViewById(R.id.group_avatar);

        title=findViewById(R.id.group_info_title);
        leader=findViewById(R.id.group_info_leader);
        section=findViewById(R.id.group_info_section);
        local=findViewById(R.id.group_info_local);
        time=findViewById(R.id.group_info_time);
        memNum=findViewById(R.id.group_info_mem_num);
        memLayout=findViewById(R.id.group_mum_list);
        recyclerView=findViewById(R.id.group_members_avatar);
        manager=findViewById(R.id.group_info_manager);
        quit=findViewById(R.id.quit_group);

        avaList=new ArrayList<>();
        groupInfos=new ArrayList<>();
        memsId=new ArrayList<>();

        manager.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("group_info_id");

        getGroupMem();

        TIMGroupManager.getInstance().getSelfInfo(groupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(GroupInfoActivity.this, "获取本人所在资料失败 错误码："+i, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                if (timGroupSelfInfo.getRole()== TIMGroupMemberRoleType.Owner)
                    manager.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getGroupMem() {
        //TODO 获取群成员列表
        TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>> () {
            @Override
            public void onError(int code, String desc) {
                Toast.makeText(GroupInfoActivity.this, "获取成员列表失败 错误码 "+code, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息
                for(TIMGroupMemberInfo info : infoList) {
                    GroupInfo groupInfo=new GroupInfo(info.getUser(),info.getRole());
                   groupInfos.add(groupInfo);
                   memsId.add(info.getUser());
                }
                    doUpdate(memsId);
            }
        };
//获取群组成员信息
        TIMGroupManager.getInstance().getGroupMembers(
                groupId, //群组Id
                cb);     //回调
    }
    private void doUpdate(List<String> stringList) {
        for (String s : stringList) {
            if (s.equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                String myFace=LiveApplication.getApplication().getSelfProfile().getFaceUrl();
                if (!myFace.isEmpty()) {
                    avaList.add(myFace);
                } else {
                    long genderValue = LiveApplication.getApplication().getSelfProfile().getGender().getValue();
                    String genderStr = genderValue == 1 ? "男" : "女";
                    avaList.add(genderStr);
                }
            }else {
                TIMFriendshipManager.getInstance().getUsersProfile(stringList, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }
                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        for (TIMUserProfile timUserProfile : timUserProfiles) {
                            if (!timUserProfile.getIdentifier().equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                                if (!timUserProfile.getFaceUrl().isEmpty()) {
                                    avaList.add(timUserProfile.getFaceUrl());
                                } else {
                                    long genderValue = timUserProfile.getGender().getValue();
                                    String genderStr = genderValue == 1 ? "男" : "女";
                                    avaList.add(genderStr);
                                }
                            }
                        }
                        createGroupAdaptar=new CreateGroupAdaptar(getContext(),avaList);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(createGroupAdaptar);
                    }
                });
            }
        }

    }

}
