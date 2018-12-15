package cn.edu.sdnu.i.livemeeting.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownAnimatorAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABShape;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.CreateMeetingActivity;
import cn.edu.sdnu.i.livemeeting.activity.SearchActivity;
import cn.edu.sdnu.i.livemeeting.activity.bmob.CommonUtil;
import cn.edu.sdnu.i.livemeeting.activity.bmob.MessageEventOne;
import cn.edu.sdnu.i.livemeeting.adapter.MeetingStackAdapter;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.info.Meet;
import cn.edu.sdnu.i.livemeeting.info.Meet_User;
import cn.edu.sdnu.i.livemeeting.util.GlideImageLoader;

import static cn.edu.sdnu.i.livemeeting.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;


public class HomeFragment extends Fragment {

    private static final int GET_DECODE_RESULT =0x05 ;
    private Banner banner;
    private SmartRefreshLayout refreshLayout;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private CardStackView mCardStack;
    private TextView planName;
    private TextView planNum;

    private List<String> data;
    private List<HomeMsg> homeMsgList=new ArrayList<>();
    private List<HomeMsg> homeMsgPlanList=new ArrayList<>();
    public List<String> groupIds=new ArrayList<>();
    private List<Integer> images=new ArrayList<>();

    Integer[] color = {
            R.color.usc_gold,
            R.color.pink,
            R.color.dark_orchid,
            R.color.classColor,
            R.color.dodger_blue,
            R.color.blue,
            R.color.portland_orange
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        data=new ArrayList<>();
        data.add("创建会议");
        initViews(view);
        getGroup();
        setClick();
        setBanner();
        setFloatingButton();
        EventBus.getDefault().register(this);
        return view;
    }

    private void setFloatingButton() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
            @Override
            public void onRFACItemLabelClick(int position, RFACLabelItem item) {
                doFloatingClick(position);
                rfabHelper.toggleContent();
            }

            @Override
            public void onRFACItemIconClick(int position, RFACLabelItem item) {
                doFloatingClick(position);
                rfabHelper.toggleContent();
            }
        });

        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("创建会议")
                .setResId(R.drawable.ic_create)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("搜索、加好友")
                        .setResId(R.drawable.ic_sreach_1)
//                        .setDrawable(getResources().getDrawable(R.mipmap.ico_test_c))
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(RFABShape.generateCornerShapeDrawable(0xaa000000, RFABTextUtil.dip2px(getContext(), 4)))
                        .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("扫码")
                .setResId(R.drawable.ic_scan)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(getContext(), 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(RFABTextUtil.dip2px(getContext(), 5))
        ;

        rfabHelper = new RapidFloatingActionHelper(getContext(), rfaLayout, rfaButton, rfaContent).build();
    }

    private void doFloatingClick(int position) {
        Intent intent;
        switch (position){
            case 0:
                //todo 创建会议
                intent=new Intent(getContext(), CreateMeetingActivity.class);
                startActivity(intent);
                break;
            case 1:
                //todo 搜索
                intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case 2:
                //todo 扫码
                if(CommonUtil.isCameraCanUse()){
                    intent=new Intent(getActivity(), cn.edu.sdnu.i.livemeeting.zxing.activity.CaptureActivity.class);
                    startActivityForResult(intent,GET_DECODE_RESULT);
                }else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 222);
                            return;
                        }
                    } else {
                        Toast.makeText(getActivity(), "请到设置中开启本应用的照相机权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
           default:
               break;
        }

    }

    private void setBanner() {
        images.add(R.drawable.ic_lb1);
        images.add(R.drawable.ic_lb2);
        images.add(R.drawable.ic_lb3);
        images.add(R.drawable.ic_lb4);
        images.add(R.drawable.ic_lb5);

        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setDelayTime(3500);
//        banner.setBannerAnimation(Transformer.DepthPage);
        banner.start();
    }

    private void setClick() {


        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
                //TODO 刷新界面
                homeMsgList.clear();
//                groupIds.clear();
                getGroup();
                updateView();
                updatePlanView();
            }
        });
    }

    private void updatePlanView() {
        //创建回调
        TIMValueCallBack<List<TIMGroupBaseInfo>> cb = new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
            }
            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {//参数返回各群组基本信息

                for(TIMGroupBaseInfo info : timGroupInfos) {
//创建待获取信息的群组Id列表
                    ArrayList<String> groupList = new ArrayList<String>();
                    groupList.add(info.getGroupId());
//获取群组详细信息
                    TIMGroupManager.getInstance().getGroupDetailInfo(
                            groupList, //需要获取信息的群组Id列表
                            new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                                @Override
                                public void onError(int i, String s) {
                                }
                                @Override
                                public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                                    for(final TIMGroupDetailInfo info : timGroupDetailInfos) {
                                        List<String> users=new ArrayList<>();
                                        users.add(info.getGroupOwner());
                                        getPlanInfo(users,info);
                                    }
                                }
                            });
                }
            }
        };
//获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event1(MessageEventOne messageEventOne){
        if(EventBus.getDefault().isRegistered(this)){
            groupIds=messageEventOne.getGroupIds();
        }
    }

    private void getGroup() {
            homeMsgList.clear();
            final List<String> stringList=new ArrayList<>();
//            groupIds.remove(groupIds.size());
            BmobQuery<Meet> query = new BmobQuery<Meet>();
            query.setLimit(500);
            query.findObjects(getContext(), new FindListener<Meet>() {
                @Override
                public void onSuccess(List<Meet> list) {
                    Log.e("Bmob获取groupId成功","Ok"+" "+list.size());
                    for (Meet meet : list) {
                        stringList.add(meet.getId());
                    }
                    EventBus.getDefault().post(new MessageEventOne(stringList));
                }
                @Override
                public void onError(int i, String s) {
                    Log.e("错误码",i+s);
                }
            });

    }


    private void updateView() {
        Log.e("获取的群组数量",groupIds.size()+"");
        if (!groupIds.isEmpty()){
            //获取群组公开信息
            TIMGroupManager.getInstance().getGroupPublicInfo(groupIds, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                @Override
                public void onError(int code, String desc) {
                    Log.e("经历失败了，oh",code+" "+desc);
                }

                @Override
                public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                    for(final TIMGroupDetailInfo info : timGroupDetailInfos) {
                        List<String> users=new ArrayList<>();
                        users.add(info.getGroupOwner());
                        getInfo(users,info);
                    }
                }
            });
        }
    }

    private void getInfo(List<String> users, final TIMGroupDetailInfo info) {
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.e("失败了，oh",i+" "+s);
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    String [] result=info.getGroupIntroduction().split(",");
                    String section,local,time;
                    if (result.length==1){
                        section=result[0];
                        local="";
                        time="";
                    }else {
                        section=result[0];
                        local=result[1];
                        time=result[2];
                    }
                    HomeMsg homeMsg=new HomeMsg(info.getGroupId(),info.getGroupName(), timUserProfile.getNickName(),section,local,time,(int)info.getMemberNum());
                    homeMsgList.add(homeMsg);
                }
                MeetingStackAdapter adapter = new MeetingStackAdapter(getContext());
                mCardStack.setAdapter(adapter);
                mCardStack.setItemExpendListener(new CardStackView.ItemExpendListener() {
                    @Override
                    public void onItemExpend(boolean expend) {
                    }
                });
                Integer[] newColor=new Integer[homeMsgList.size()];
                for (int i=0;i<homeMsgList.size();i++){
                    if (homeMsgList.size()<color.length)
                        newColor[i]=color[i%color.length];
                }
                adapter.updateData(Arrays.asList(newColor),homeMsgList);
//                mCardStack.setAnimatorAdapter(new AllMoveDownAnimatorAdapter(mCardStack));
                mCardStack.setAnimatorAdapter(new UpDownAnimatorAdapter(mCardStack));
                //mCardStack.setAnimatorAdapter(new UpDownStackAnimatorAdapter(mCardStack));

            }
        });
    }

    private void getPlanInfo(List<String> users, final TIMGroupDetailInfo info) {
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.e("失败了，oh",i+" "+s);
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                int max=0;
                int n=0;
                Date maxDate=null;
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    String [] result=info.getGroupIntroduction().split(",");
                    String section,local,time;
                    if (result.length==1){
                        section=result[0];
                        local="";
                        time="";
                    }else {
                        section=result[0];
                        local=result[1];
                        time=result[2];
                    }
                    HomeMsg homeMsg=new HomeMsg(info.getGroupId(),info.getGroupName(), timUserProfile.getNickName(),section,local,time,(int)info.getMemberNum());
                    if (n==0)
                        maxDate=homeMsg.getDate();
                    else if (maxDate.compareTo(homeMsg.getDate())>0&&homeMsg.getDistanceTime()>=0){
                        max=n;
                        maxDate=homeMsg.getDate();
                    }
                    homeMsgPlanList.add(homeMsg);
                    n++;
                }
                planName.setText(homeMsgPlanList.get(max).getTitle());
                planNum.setText(homeMsgPlanList.get(max).getDistanceTime()+"");
            }
        });
    }

    private void initViews(View view) {
        mCardStack=view.findViewById(R.id.home_rec_stackView);
        refreshLayout=view.findViewById(R.id.home_refresh);
        rfaLayout = view.findViewById(R.id.label_list_sample_rfal);
        rfaButton = view.findViewById(R.id.label_list_sample_rfab);
        banner=view.findViewById(R.id.home_rec_banner);
        planName=view.findViewById(R.id.home_plan_meeting_name);
        planNum=view.findViewById(R.id.home_plan_meeting_day);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }
    public static HomeFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //onActivityResult:1.接收返回的扫码结果(meetId)
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;
        try{
            if(requestCode==GET_DECODE_RESULT&&data.getExtras()!=null){
                bundle = data.getExtras();
                //获取扫码结果String sacnResult
                final String scanResult = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);
                //先查询当前用户所参加的所有会议并放入list中，再检索list中是否有与scanResult相同的MeetId，若有则签到成功，否则得先参加会议再来
                BmobQuery<Meet_User> query = new BmobQuery<>();
                query.addWhereEqualTo("userId", BmobUser.getCurrentUser(getContext()).getObjectId());
                query.findObjects(getContext(),new FindListener<Meet_User>() {
                    @Override
                    public void onSuccess(List<Meet_User> list) {
                        int i=0;
                        final Meet_User tempMeetUser;
                        for(i=0;i<list.size();i++){
                            if(scanResult.equals(list.get(i).getMeetId())){
                                tempMeetUser=list.get(i);//用于存放符合条件的哪个Meet_User对象
                                //将Meet_User表中相应元组的isComing属性改为true
                                Meet_User meetUser = new Meet_User();
                                meetUser.setComing(true);
                                meetUser.update(getActivity(),list.get(i).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getContext(), "签到成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(getContext(), "签到失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                            //如果for循环正常退出，则说明没有符合条件的数据
                            if(i==list.size()-1) {Toast.makeText(getContext(), "你没有参加这个会议", Toast.LENGTH_SHORT).show();}
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        }catch (NullPointerException e){
        }finally {
            Intent intent=new Intent(getContext(),MainActivity.class);
            startActivity(intent);
        }

    }
}
