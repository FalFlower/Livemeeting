package cn.edu.sdnu.i.livemeeting.info;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

public class MsgList {
    private Context context;
    private String name;
    private String faceUrl;
    private String content;
//    private String time;
    private String id;
    public MsgList(final Context context, final String id, String content){
        this.context=context;
        this.id=id;
        this.content=content;
//        this.time=time;
        List<String> stringList=new ArrayList<>();
        stringList.add(id);
        TIMFriendshipManager.getInstance().getFriendsProfile(stringList, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    if (!timUserProfile.getRemark().isEmpty()){
                        new SharedPreferencesUtil(context).doPutString(id+"name",timUserProfile.getRemark());
                    }else {
                        new SharedPreferencesUtil(context).doPutString(id+"name",timUserProfile.getNickName());
                    }
                    String faceUrl = timUserProfile.getFaceUrl();
                    if (TextUtils.isEmpty(faceUrl)) {
                        new SharedPreferencesUtil(context).doPutString(id+"face","");
                    } else {
                        new SharedPreferencesUtil(context).doPutString(id+"face",faceUrl);
                    }

                }
            }
        });
        this.name=new SharedPreferencesUtil(context).doGetString(id+"name","我的好友");
        this.faceUrl=new SharedPreferencesUtil(context).doGetString(id+"face","");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String redId) {
        this.faceUrl = redId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
