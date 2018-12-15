package cn.edu.sdnu.i.livemeeting.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.application.LiveApplication;

import static com.tencent.qalsdk.service.QalService.tag;

public class AddFriendUtils {

    private Activity activity=null;
    private String userId;
    private List<TIMAddFriendRequest> reqList;
    public AddFriendUtils(Activity activity,String userId){
        this.reqList=new ArrayList<TIMAddFriendRequest>();
        this.userId=userId;
        this.activity=activity;
    }


    public void doAddFriend(String wording,String remark){
        TIMAddFriendRequest req = new TIMAddFriendRequest();
        req.setAddrSource("");
        if (wording.equals(""))
            req.setAddWording("请求加为好友");
        else
            req.setAddWording(wording);
        req.setIdentifier(userId);
        if (remark.equals(""))
            req.setRemark("");
        else
            req.setRemark(remark);
        reqList.add(req);

        //TODO 申请添加好友
        TIMFriendshipManager.getInstance().addFriend(reqList, new
                TIMValueCallBack<List<TIMFriendResult>>() {
                    @Override
                    public void onError(int code, String desc){
                        Toast.makeText(activity,"添加好友失败 ，错误码："+code+"错误描述："+desc,Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(List<TIMFriendResult> result){
                        for(TIMFriendResult res : result){
                            Log.e(tag, "identifier: " + res.getIdentifer() + " status: " +res.getStatus());
                            if ( res.getStatus()==TIMFriendStatus.TIM_ADD_FRIEND_STATUS_ALREADY_FRIEND){
                                Toast.makeText(activity,"好友添加成功",Toast.LENGTH_SHORT).show();
                                sendIS();
                            }else if (res.getStatus()==TIMFriendStatus.TIM_ADD_FRIEND_STATUS_PENDING){
                                Toast.makeText(activity,"好友验证发送成功，等待确认",Toast.LENGTH_SHORT).show();
                            }else if (res.getStatus()==TIMFriendStatus.TIM_FRIEND_STATUS_UNKNOWN){
                                Toast.makeText(activity,"没有用户信息",Toast.LENGTH_SHORT).show();
                            }else if (res.getStatus()==TIMFriendStatus.TIM_FRIEND_STATUS_SUCC   ){
                                Toast.makeText(activity,"添加好友成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                        activity.finish();
                    }
                });
    }

    private void sendIS() {
       SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(activity);
       SharedPreferences.Editor editor=pref.edit();
       editor.putBoolean("is_Ok",true);
       editor.apply();
    }


}
