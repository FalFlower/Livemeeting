package cn.edu.sdnu.i.livemeeting.activity.bmob;

import java.util.List;

import cn.edu.sdnu.i.livemeeting.info.Meet;

public class MessageEvent {
    private Meet meet;
    private List<Vote> list;
    private String voteId;
    private String voteResultId;
    private List groupIds;

    public List getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List groupIds) {
        this.groupIds = groupIds;
    }



    public String getVoteName() {
        return voteName;
    }

    public void setVoteName(String voteName) {
        this.voteName = voteName;
    }

    private String voteName;
    public  MessageEvent(List list){
        this.list=list;
    }

    public MessageEvent(String voteId, String voteResultId,String voteName) {
        this.voteId = voteId;
        this.voteResultId = voteResultId;
        this.voteName=voteName;
    }



    public List<Vote> getList() {
        return list;
    }

    public void setList(List<Vote> list) {
        this.list = list;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoteResultId() {
        return voteResultId;
    }

    public void setVoteResultId(String voteResultId) {
        this.voteResultId = voteResultId;
    }
    public MessageEvent(Meet meet){
        this.meet=meet;
    }
    public Meet getMeet() {
        return meet;
    }

    public void setMeet(Meet meet) {
        this.meet = meet;
    }
}
