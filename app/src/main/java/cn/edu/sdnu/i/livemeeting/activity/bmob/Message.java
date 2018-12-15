package cn.edu.sdnu.i.livemeeting.activity.bmob;

import cn.bmob.v3.BmobObject;

public class Message extends BmobObject {
    private String message;
    private String from_who;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom_who() {
        return from_who;
    }

    public void setFrom_who(String from_who) {
        this.from_who = from_who;
    }
}
