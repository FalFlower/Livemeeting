package cn.edu.sdnu.i.livemeeting.info;

import com.tencent.TIMGroupMemberRoleType;

public class GroupInfo {
    private String id;
    private TIMGroupMemberRoleType role;
    public GroupInfo(String id,TIMGroupMemberRoleType role){
        this.id=id;
        this.role=role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TIMGroupMemberRoleType getRole() {
        return role;
    }

    public void setRole(TIMGroupMemberRoleType role) {
        this.role = role;
    }
}
