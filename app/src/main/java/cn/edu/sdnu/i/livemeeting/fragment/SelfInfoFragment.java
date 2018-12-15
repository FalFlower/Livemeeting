package cn.edu.sdnu.i.livemeeting.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABShape;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.CreateMeetingActivity;
import cn.edu.sdnu.i.livemeeting.activity.EditProfileActivity;
import cn.edu.sdnu.i.livemeeting.activity.LoginActivity;
import cn.edu.sdnu.i.livemeeting.activity.NewEditProfileActivity;
import cn.edu.sdnu.i.livemeeting.activity.SearchActivity;
import cn.edu.sdnu.i.livemeeting.activity.bmob.CommonUtil;
import cn.edu.sdnu.i.livemeeting.adapter.HomeRecAdaptar;
import cn.edu.sdnu.i.livemeeting.info.CustomProfile;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.profile.ProfileSimpleTextView;
import cn.edu.sdnu.i.livemeeting.util.BlurTransformation;
import cn.edu.sdnu.i.livemeeting.util.FastBlurUtils;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;


import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;
import static me.ele.uetool.base.Application.getApplicationContext;

public class SelfInfoFragment extends Fragment {
    private static final int GET_DECODE_RESULT =0x05 ;
    private ImageView ava;
    private TextView mId;
    private TextView mNickName;
    private TextView mSign;
    private TextView mGender;
    private TextView mLocation;
    private TextView mPosition;
    private TextView mAppointmentNum;
    private TextView mHavingNum;
    private TextView mInvalidNum;

    private SmartRefreshLayout refreshLayout;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private TIMUserProfile mUserProfile;

    private List<HomeMsg> homeMsgList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_slef_info, container, false);
        findAllViews(mainView);
        setClick();
        getSelfInfo();
        setFloatingButton();
        return mainView;
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                mUserProfile = timUserProfile;
                updateViews(timUserProfile);
            }
        });

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
                                        getInfo(users,info);
                                    }
                                }
                            });
                }
            }
        };
//获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);
    }



    private void getInfo(List<String> users, final TIMGroupDetailInfo info) {
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
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
                int ap=0,hav=0,inv=0;
                for (HomeMsg homeMsg : homeMsgList) {
                    switch (homeMsg.getStatus()){
                        case HomeMsg.MSG_IS_APPOINTMENT:
                            ap++;
                            break;
                        case HomeMsg.MSG_IS_HAVING:
                            hav++;
                            break;
                        case HomeMsg.MSG_IS_INVALID:
                            inv++;
                            break;
                            default:
                                break;
                    }
                }
                mAppointmentNum.setText(ap+"");
                mHavingNum.setText(hav+"");
                mInvalidNum.setText(inv+"");
            }
        });
    }

    private void updateViews(TIMUserProfile timUserProfile) {
        //更新界面
        String faceUrl = timUserProfile.getFaceUrl();

        String name=timUserProfile.getNickName();
        mNickName.setText(name.trim());
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";

        if (TextUtils.isEmpty(faceUrl)) {
            if (genderValue==1){
                ImgUtils.loadRound(R.drawable.right_ava, ava);
            }else {
                ImgUtils.loadRound(R.drawable.left_ava, ava);
            }
        } else {
            ImgUtils.loadRound(faceUrl,ava);
        }
        mGender.setText(genderStr);
        mSign.setText(timUserProfile.getSelfSignature().trim());
        mLocation.setText(timUserProfile.getLocation().trim());
        mId.setText(timUserProfile.getIdentifier().trim());
        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        mPosition.setText(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知").trim());

    }
    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null) {
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }
    public void instance(TIMUserProfile mUserProfile){
        this.mUserProfile=mUserProfile;
    }

    private void setClick() {
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1500);
                homeMsgList.clear();
                getSelfInfo();
            }
        });
    }

    private void findAllViews(View mainView) {
        ava=mainView.findViewById(R.id.self_info_ava);
        mId=mainView.findViewById(R.id.self_info_id);
        mNickName=mainView.findViewById(R.id.self_info_name);
        mSign=mainView.findViewById(R.id.self_info_sign);
        mGender=mainView.findViewById(R.id.self_info_gender);
        mLocation=mainView.findViewById(R.id.self_info_location);
        mPosition=mainView.findViewById(R.id.self_info_position);
        mAppointmentNum=mainView.findViewById(R.id.self_info_APPOINTMENT_num);
        mHavingNum=mainView.findViewById(R.id.self_info_HAVING_num);
        mInvalidNum=mainView.findViewById(R.id.self_info_INVALID_num);

        refreshLayout=mainView.findViewById(R.id.self_info_refresh);
        rfaLayout = mainView.findViewById(R.id.self_info_sample_rfal);
        rfaButton = mainView.findViewById(R.id.self_info_sample_rfab);
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
                .setLabel("修改个人信息")
                .setResId(R.drawable.change)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("退出登录")
                        .setResId(R.drawable.logout)
//                        .setDrawable(getResources().getDrawable(R.mipmap.ico_test_c))
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(RFABShape.generateCornerShapeDrawable(0xaa000000, RFABTextUtil.dip2px(getContext(), 4)))
                        .setWrapper(1)
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
                //todo 修改个人信息
                intent=new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case 1:
                //todo 退出登录
                //TODO 偶尔出现Bug，需要点两次才能退出
                logout();
                MainActivity.instance.finish();
                break;
            default:
                break;
        }

    }
    private void logout() {
        //登出
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Toast.makeText(getApplicationContext(),"登出失败 错误码："+code+"错误描述："+desc,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess() {
                //登出成功
                Toast.makeText(getApplicationContext(),"已注销",Toast.LENGTH_SHORT).show();
                //取消自动登录
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("auto_login",false);
                editor.apply();
                //转到登录界面
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
    public static void alphaTask(Activity context) {
        context.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    public static SelfInfoFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        SelfInfoFragment fragment = new SelfInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        homeMsgList.clear();
        getSelfInfo();
        super.onResume();
    }
}
