package cn.edu.sdnu.i.livemeeting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.GroupChatActivity;
import cn.edu.sdnu.i.livemeeting.activity.JoinMeetingActivity;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.info.MsgList;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;
import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;
import static com.tencent.TIMGroupMemberRoleType.Owner;

public class HomeRecAdaptar extends RecyclerView.Adapter<HomeRecAdaptar.HomeRecHolder> {
    private Context context;
    private List<HomeMsg> msgList;
    private HomeRecHolder holder;

    public HomeRecAdaptar(Context context, List<HomeMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    @Override
    public HomeRecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_rec, parent, false);
        holder = new HomeRecHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final HomeRecHolder holder, final int position) {
        holder.meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 如果没有加入会议，则选择是否加入会议。如果会议状态是正在进行，则进入会议界面
                Log.e("positon", position + "");
                //获取已加入的群组列表
                TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(context, "判断是否加入群组失败 错误码：" + i, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(final List<TIMGroupBaseInfo> timGroupBaseInfos) {
                        boolean isSelf = false;
                        for (TIMGroupBaseInfo info : timGroupBaseInfos) {
                            if (info.getGroupId().equals(msgList.get(position).getId()))
                                isSelf = true;
                        }
                        switch (msgList.get(position).getStatus()) {
                            case 0:
                                if (isSelf) {
                                    TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>> () {
                                        @Override
                                        public void onError(int code, String desc) {
                                        }
                                        @Override
                                        public void onSuccess(List<TIMGroupMemberInfo> infoList) {
                                            //参数返回群组成员信息
                                            for(TIMGroupMemberInfo info : infoList) {
                                                if (info.getUser().equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                                                    TIMGroupManager.getInstance().getSelfInfo(msgList.get(position).getId(), new TIMValueCallBack<TIMGroupSelfInfo>() {
                                                        @Override
                                                        public void onError(int i, String s) {
                                                        }
                                                        @Override
                                                        public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                                                            if (timGroupSelfInfo.getRole() == TIMGroupMemberRoleType.Owner) {
                                                                Intent intent = new Intent(getApplication(), GroupChatActivity.class);
                                                                intent.putExtra("join_ok_id", msgList.get(position).getId());
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                context.startActivity(intent);
                                                            }else {
                                                                Intent intent = new Intent(getApplication(), JoinMeetingActivity.class);
                                                                intent.putExtra("join_id", msgList.get(position).getId());
                                                                intent.putExtra("join_status","never");                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                context.startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
//获取群组成员信息
                                    TIMGroupManager.getInstance().getGroupMembers(
                                            msgList.get(position).getId(), //群组 ID
                                            cb);     //回调
                                } else {
                                    Intent intent = new Intent(getApplication(), JoinMeetingActivity.class);
                                    intent.putExtra("join_id", msgList.get(position).getId());
                                    intent.putExtra("join_status","never");                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                                //创建回调


                                break;
                            case 1:
                                if (isSelf) {
                                    //跳转会议界面
                                    Intent intent = new Intent(getApplication(), GroupChatActivity.class);
                                    intent.putExtra("join_ok_id", msgList.get(position).getId());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } else {
                                    //跳转加入界面
                                    Intent intent = new Intent(context, JoinMeetingActivity.class);
                                    intent.putExtra("join_id", msgList.get(position).getId());
                                    intent.putExtra("join_status","live");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                                break;
                            case 2:
                                TIMGroupManager.getInstance().getSelfInfo(msgList.get(position).getId(), new TIMValueCallBack<TIMGroupSelfInfo>() {
                                    @Override
                                    public void onError(int i, String s) {
                                    }
                                    @Override
                                    public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                                        if (timGroupSelfInfo.getRole() == Owner) {
                                            Intent intent = new Intent(getApplication(), GroupChatActivity.class);
                                            intent.putExtra("join_ok_id", msgList.get(position).getId());
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }else
                                            Toast.makeText(getContext(), "会议已过期", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }

                    }
                });
            }
        });
        HomeMsg homeMsg = msgList.get(position);
        holder.title.setText(homeMsg.getTitle());
        holder.leader.setText(homeMsg.getLeader());
        holder.section.setText(homeMsg.getSection());
        holder.local.setText(homeMsg.getLocal());
        holder.time.setText(homeMsg.getTime());
        holder.num.setText(homeMsg.getCount() + "");
        switch (homeMsg.getStatus()) {
            case 0:
                ImgUtils.load(R.drawable.sta_before, holder.status);
                break;
            case 1:
                ImgUtils.load(R.drawable.sta_now, holder.status);
                break;
            case 2:
                ImgUtils.load(R.drawable.sta_past, holder.status);
                break;
            default:
                break;
        }

    }


    private boolean getOwner(final int position) {
        final boolean[] is = {false};
        TIMGroupManager.getInstance().getSelfInfo(msgList.get(position).getId(), new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int i, String s) {
            }
            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                if (timGroupSelfInfo.getRole() == Owner) {
                    is[0] = true;
                }
            }
        });
        return is[0];
    }


    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class HomeRecHolder extends RecyclerView.ViewHolder {

        private LinearLayout meeting;
        private TextView title;
        private TextView section;
        private ImageView status;
        private TextView leader;
        private TextView local;
        private TextView time;
        private TextView num;

        public HomeRecHolder(View itemView) {
            super(itemView);
            meeting = itemView.findViewById(R.id.meeting_layout);
            title = itemView.findViewById(R.id.home_msg_title);
            section = itemView.findViewById(R.id.home_msg_section);
            status = itemView.findViewById(R.id.home_msg_status);
            leader = itemView.findViewById(R.id.home_msg_manager);
            local = itemView.findViewById(R.id.home_msg_local);
            time = itemView.findViewById(R.id.home_msg_time);
            num = itemView.findViewById(R.id.home_msg_num);
        }
    }
}
