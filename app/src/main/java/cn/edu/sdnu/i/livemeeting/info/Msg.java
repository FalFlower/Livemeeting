package cn.edu.sdnu.i.livemeeting.info;

import android.net.Uri;

public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    public static final int TYPE_TEXT=2;
    public static final int TYPE_PIC=3;
    public static final int TYPE_VOTE=4;
    private String content;
    private int type;
    private String id;
//    private Uri uri;
    private String path;
    private int msgType;
    private String voteId="";
    private String voteResultId="";
    private String voteName="";
    private boolean isOnly=false;

    public Msg(String content,int type,String id){
        this.content=content;
        this.type=type;
        this.id=id;
        this.msgType=TYPE_TEXT;
    }
    public Msg(int type, String id,String path){
        this.path=path;
        this.type=type;
        this.id=id;
        this.msgType=TYPE_PIC;
    }

    public Msg(String voteId,String voteResultId,String voteName,int type){
        this.voteId=voteId;
        this.voteResultId=voteResultId;
        this.type=type;
        this.voteName=voteName;
        this.msgType=TYPE_VOTE;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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


    public int getMsgType() {
        return msgType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getVoteName() {
        return voteName;
    }

    public void setVoteName(String voteName) {
        this.voteName = voteName;
    }

    public boolean isOnly() {
        return isOnly;
    }

    public void setOnly(boolean only) {
        isOnly = only;
    }
}
