package cn.edu.sdnu.i.livemeeting.application;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSettings;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.edu.sdnu.i.livemeeting.info.CustomProfile;
import cn.edu.sdnu.i.livemeeting.util.QnUploadHelper;

public class LiveApplication extends Application {
    private static LiveApplication app;
    private static Context appContext;
    private ILVLiveConfig mLiveConfig;

    private TIMUserProfile mSelfProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appContext = getApplicationContext();

        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400026811, 11334);
        List<String> customInfos = new ArrayList<String>();
        customInfos.add(CustomProfile.CUSTOM_GET);
        customInfos.add(CustomProfile.CUSTOM_SEND);
        customInfos.add(CustomProfile.CUSTOM_LEVEL);
        customInfos.add(CustomProfile.CUSTOM_RENZHENG);
        TIMManager.getInstance().initFriendshipSettings(CustomProfile.allBaseInfo, customInfos);
        TIMManager.getInstance().enableFriendshipStorage(true);//开启关系链存储
        TIMManager.getInstance().enableGroupInfoStorage(true);//开启群组存储
        TIMGroupSettings settings = new TIMGroupSettings();

//设置群资料拉取字段
        TIMGroupSettings.Options groupOpt = settings.new Options();
        long groupFlags = 0;
        groupFlags |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_FACE_URL
                | TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_GROUP_TYPE
                | TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_OWNER_UIN
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_LAST_MSG
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_INTRODUCTION
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_CREATE_TIME
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_LAST_MSG_TIME
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_NAME
                |TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_MEMBER_NUM;
        groupOpt.setFlags(groupFlags);
        List<String> custom=new ArrayList<>();
        custom.add("time");
        groupOpt.addCustomTag("time");
        groupOpt.setCustomTags(custom);//设置拉去自定义字段
        settings.setGroupInfoOptions(groupOpt);

//设置群成员资料拉取字段
        TIMGroupSettings.Options memberOpt = settings.new Options();
        long memberFlags = 0;
        memberFlags |= TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_NAME_CARD
                | TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_ROLE_INFO
                |TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_JOIN_TIME
                |TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_MSG_FLAG
                |TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_SHUTUP_TIME;
        memberOpt.setFlags(memberFlags);
        settings.setMemberInfoOptions(memberOpt);

//初始化群设置
        TIMManager.getInstance().initGroupSettings(settings);
        //初始化直播场景
        mLiveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(mLiveConfig);

        QnUploadHelper.init("TmmxhlTyf8yuLL7cjUOA-HGM69-zLHf8SgTbF47e",
                "w-8eqOYynoVaePkR8wTqY29_ufxGTsnkWBTCdjSb",
                "http://pil3lbqqk.bkt.clouddn.com/",
                "sdnu");

        LeakCanary.install(this);
        Bmob.initialize(this, "b0426cc243d6e2238f19e640ce89ad02");

    }

    public static Context getContext() {
        return appContext;
    }

    public static LiveApplication getApplication() {
        return app;
    }

    public void setSelfProfile(TIMUserProfile userProfile) {
        mSelfProfile = userProfile;
    }

    public TIMUserProfile getSelfProfile() {
        return mSelfProfile;
    }

    public ILVLiveConfig getLiveConfig() {
        return mLiveConfig;
    }
}
