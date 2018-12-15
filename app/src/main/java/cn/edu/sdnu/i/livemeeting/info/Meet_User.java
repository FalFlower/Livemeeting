package cn.edu.sdnu.i.livemeeting.info;

import cn.bmob.v3.BmobObject;

public class Meet_User extends BmobObject {
    private String userId;
    private String meetId;
    private Boolean isComing;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMeetId() {
        return meetId;
    }

    public void setMeetId(String meetId) {
        this.meetId = meetId;
    }

    public Boolean getComing() {
        return isComing;
    }

    public void setComing(Boolean coming) {
        isComing = coming;
    }







}
