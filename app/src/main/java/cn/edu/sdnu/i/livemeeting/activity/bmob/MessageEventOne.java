package cn.edu.sdnu.i.livemeeting.activity.bmob;

import java.util.ArrayList;
import java.util.List;

public class MessageEventOne {
    private List<String> groupIds;
    private boolean isOwner;
    public MessageEventOne(List<String> groupIds){
        this.groupIds=groupIds;
    }
    public MessageEventOne(boolean isOwner){
        this.isOwner=isOwner;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}
