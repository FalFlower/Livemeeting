package cn.edu.sdnu.i.livemeeting.activity.bmob;

import cn.bmob.v3.BmobObject;

public class Vote_User extends BmobObject {

    private String voteId;
    private String userId;

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
