package cn.edu.sdnu.i.livemeeting.activity.bmob;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class MyUser extends BmobUser {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
