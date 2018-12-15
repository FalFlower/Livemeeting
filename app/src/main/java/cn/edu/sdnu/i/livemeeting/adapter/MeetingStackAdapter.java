package cn.edu.sdnu.i.livemeeting.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMValueCallBack;

import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.GroupChatActivity;
import cn.edu.sdnu.i.livemeeting.activity.JoinMeetingActivity;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;
import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;
import static com.tencent.TIMGroupMemberRoleType.Owner;

public class MeetingStackAdapter extends StackAdapter<Integer>  {
    private static List<HomeMsg> msgList;
    public MeetingStackAdapter(Context context) {
        super(context);
    }

    public void updateData(List<Integer> data,List<HomeMsg> msgList) {
        this.msgList=msgList;
        updateData(data);
    }


    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_stack_meeting,parent,false);
        return new CardViewHolder(view);
    }
    public static class CardViewHolder extends CardStackView.ViewHolder {
        View root;
        FrameLayout frameLayout;
        CardView cardView;
        TextView title;
        TextView cardTitle;
        ImageView status;
        TextView section;
        TextView manager;
        TextView location;
        TextView time;
        TextView peoNum;
        CardViewHolder(View view) {
            super(view);
            root = view;
            frameLayout = view.findViewById(R.id.item_card_stack_fragment);
            cardView = view.findViewById(R.id.item_card_stack_cardView);
            title = view.findViewById(R.id.item_card_stack_inner_title);
            cardTitle=view.findViewById(R.id.item_card_stack_title_text);
            status=view.findViewById(R.id.item_card_stack_status);
            section= view.findViewById(R.id.item_card_stack_section);
            manager= view.findViewById(R.id.item_card_stack_manager);
            location= view.findViewById(R.id.item_card_stack_location);
            time= view.findViewById(R.id.item_card_stack_time);
            peoNum= view.findViewById(R.id.item_card_stack_num);
        }

        void onBind(Integer backgroundColorId, final int position, List<HomeMsg> dataList) {
            frameLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(),backgroundColorId), PorterDuff.Mode.SRC_IN);
            //todo 加载会议简介卡片UI
            Log.e("position?",position+"");
            HomeMsg homeMsg = dataList.get(position);
            title.setText(homeMsg.getTitle());
            cardTitle.setText(homeMsg.getTitle());
            manager.setText(homeMsg.getLeader());
            section.setText(homeMsg.getSection());
            location.setText(homeMsg.getLocal());
            time.setText(homeMsg.getTime());
            peoNum.setText(homeMsg.getCount() + "");
            switch (homeMsg.getStatus()) {
                case 0:
                    ImgUtils.load(R.drawable.sta_before, status);
                    break;
                case 1:
                    ImgUtils.load(R.drawable.sta_now, status);
                    break;
                case 2:
                    ImgUtils.load(R.drawable.sta_past, status);
                    break;
                default:
                    break;
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 如果没有加入会议，则选择是否加入会议。如果会议状态是正在进行，则进入会议界面
                    Log.e("positon", position + "");
                    //获取已加入的群组列表
                    TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(getContext(), "判断是否加入群组失败 错误码：" + i, Toast.LENGTH_SHORT).show();
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
                                                                    getApplication().startActivity(intent);
                                                                }else {
                                                                    Intent intent = new Intent(getApplication(), JoinMeetingActivity.class);
                                                                    intent.putExtra("join_id", msgList.get(position).getId());
                                                                    intent.putExtra("join_status","never");                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    getApplication().startActivity(intent);
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
                                        getApplication().startActivity(intent);
                                    }
                                    //创建回调


                                    break;
                                case 1:
                                    if (isSelf) {
                                        //跳转会议界面
                                        Intent intent = new Intent(getContext(), GroupChatActivity.class);
                                        intent.putExtra("join_ok_id", msgList.get(position).getId());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplication().startActivity(intent);
                                    } else {
                                        //跳转加入界面
                                        Intent intent = new Intent(getContext(), JoinMeetingActivity.class);
                                        intent.putExtra("join_id", msgList.get(position).getId());
                                        intent.putExtra("join_status","live");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplication().startActivity(intent);
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
                                                getApplication().startActivity(intent);
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
        }

        @Override
        public void onItemExpand(boolean b) {
            cardView.setVisibility(b ? View.VISIBLE : View.GONE);
            System.out.println("holder onItemExpand");
        }
    }

    @Override
    public int getItemViewType(int position) { return super.getItemViewType(position); }

    @Override
    public Integer getItem(int position) { return super.getItem(position); }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if(holder instanceof CardViewHolder) {
            CardViewHolder cardHolder = (CardViewHolder)holder;
            cardHolder.onBind(data,position,msgList);
        }
    }

}
