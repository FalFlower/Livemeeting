package cn.edu.sdnu.i.livemeeting.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.application.LiveApplication;

public class HomeMsg implements Serializable {
    public static final int MSG_IS_APPOINTMENT=0;
    public static final int MSG_IS_HAVING=1;
    public static final int MSG_IS_INVALID=2;
//    private String imgUrl;
    private String title;
    private boolean isSelf;
    private String section;
    private int status;
    private String leader;
    private String local;
    private String time;

    private String id="";
    private int count;
    private Date date;
    private int distanceTime;
    public Date getDate() {
        return date;
    }

    private boolean isLive=false;
    public HomeMsg(String id,String title,String leader,String section,String local, String time,int count){
//        this.imgUrl=imgUrl;
        this.id=id;
        this.title=title;
        this.section=section;
        this.leader=leader;
        this.local=local;
        this.time=time;
        this.count=count;
        isSelf=false;
        //获得当前会议状态
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            Date date2=sdf.parse(date);
            Date date1 = sdf.parse(time);
            long s1=date1.getTime();//将时间转为毫秒
            long s2=date2.getTime();//得到当前的毫秒
            distanceTime= (int) ((s2-s1)/1000/60/60/24);
            this.date=date1;
            if (date2.getTime()>date1.getTime()){
                this.status= MSG_IS_INVALID;
                if ((date2.getTime()!=0)&&(date1.getTime()!=0))
                if ((date2.getTime()-date1.getTime())>7){
                    //解散群组
                    TIMGroupManager.getInstance().deleteGroup(id, new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            //错误码 code 和错误描述 desc，可用于定位请求失败原因
                            //错误码 code 列表请参见错误码表
                            Log.d("会议解散失败", "login failed. code: " + code + " errmsg: " + desc);
                        }
                        @Override
                        public void onSuccess() {
                            //解散群组成功
                            isLive=true;
                        }
                    });
                }
            }else if (date2.getTime()==date1.getTime()){
                this.status=MSG_IS_HAVING;
            }else {
                this.status=MSG_IS_APPOINTMENT;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean getIsSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public int getDistanceTime() {
        return distanceTime;
    }
}
