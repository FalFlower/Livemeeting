package cn.edu.sdnu.i.livemeeting.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.FriendsGroupAdaptar;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.GroupInfo;
import cn.edu.sdnu.i.livemeeting.relationship.InGroupShowFirendsListView;
import cn.edu.sdnu.i.livemeeting.relationship.ShowOthersInformationActivity;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;

public class ShowGroupMemActivity extends AppCompatActivity {

    private ListView mFirendsGroup;
    private SmartRefreshLayout refreshLayout;

    private String groupId;
    private List<GroupInfo> groupInfos;
    private List<String> memsId;

    private FriendsGroupAdaptar friendsGroupAdaptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_mem);
        findViews();
        setClick();
    }
    private void getGroupMem() {
        //TODO 获取群成员列表
        TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>> () {
            @Override
            public void onError(int code, String desc) {
                Toast.makeText(ShowGroupMemActivity.this, "获取成员列表失败 错误码 "+code, Toast.LENGTH_SHORT).show();
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

    private void doUpdate(List<String> memsId) {
        //TODO 实现群主单独标记（还没写）
            TIMFriendshipManager.getInstance().getUsersProfile(memsId, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(getApplication(), "获取信息失败：" , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    List<InGroupShowFirendsListView> inGroupShowFriendsListViewList=new ArrayList<>();
                    //更新信息
                    for (TIMUserProfile res : timUserProfiles) {
                        Log.e("？？？",res.getIdentifier());
                            InGroupShowFirendsListView inGroupShowFirendsListView=new InGroupShowFirendsListView(getContext());
                            String faceUrl = res.getFaceUrl();
                            long genderValue = res.getGender().getValue();
                            if (TextUtils.isEmpty(faceUrl)) {
                                if (genderValue==1){
                                    ImgUtils.loadRound(R.drawable.right_ava,inGroupShowFirendsListView.getmAvatar());
                                }else {
                                    ImgUtils.loadRound(R.drawable.left_ava,inGroupShowFirendsListView.getmAvatar());
                                }
                            } else {
                                ImgUtils.loadRound(faceUrl, inGroupShowFirendsListView.getmAvatar());
                            }
                           if (!res.getNickName().isEmpty()){
                                inGroupShowFirendsListView.setmName(res.getNickName());
                            }else {
                                inGroupShowFirendsListView.setmName(res.getIdentifier());
                        }
                        inGroupShowFriendsListViewList.add(inGroupShowFirendsListView);
                    }
                    Log.e("long",inGroupShowFriendsListViewList.size()+"");
                    friendsGroupAdaptar=new FriendsGroupAdaptar(inGroupShowFriendsListViewList);
                    mFirendsGroup.setAdapter(friendsGroupAdaptar);
                }
            });

    }

    private void setClick() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //更新好友列表
                refreshlayout.finishRefresh(1500);
                getGroupMem();
            }
        });
        mFirendsGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Intent intent=new Intent(ShowGroupMemActivity.this, ShowOthersInformationActivity.class);
                String id=memsId.get(position);
                intent.putExtra("userId",id);
                startActivity(intent);
            }
        });
    }
    private void findViews() {
        mFirendsGroup = findViewById(R.id.group_mems);
        refreshLayout = findViewById(R.id.group_refresh_Layout);
        Intent intent=getIntent();
        groupId=intent.getStringExtra("group_show_id");
        groupInfos=new ArrayList<>();
        memsId=new ArrayList<>();

        getGroupMem();
    }
}
