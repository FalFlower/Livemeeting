package cn.edu.sdnu.i.livemeeting.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.bmob.UsersVoteActivity;
import cn.edu.sdnu.i.livemeeting.activity.bmob.Vote;
import cn.edu.sdnu.i.livemeeting.info.Meet_User;
import cn.edu.sdnu.i.livemeeting.info.Msg;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.XCRoundRectImageView;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Context context;
    private List<Msg> mMsgList;
    private ViewHolder holder;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftTextLayout;
        LinearLayout rightTextLayout;
        LinearLayout leftPicLayout;
        LinearLayout rightPicLayout;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout leftVoteLayout;
        LinearLayout rightVoteLayout;

        ImageView leftTextAva;
        ImageView rightTextAva;
        ImageView leftPicAva;
        ImageView rightPicAva;
        ImageView leftVoteAva;
        ImageView rightVoteAva;

        TextView leftTextMsg;
        TextView rightTextMsg;
        TextView leftVoteTitle;
        TextView rightVoteTitle;

        ImageView leftPicMsg;
        ImageView rightPicMsg;
        ViewHolder(View itemView) {
            super(itemView);
            leftLayout=itemView.findViewById(R.id.left_layout);
            rightLayout=itemView.findViewById(R.id.right_layout);
            leftTextLayout=itemView.findViewById(R.id.left_layout_text);
            rightTextLayout=itemView.findViewById(R.id.right_layout_text);
            leftPicLayout=itemView.findViewById(R.id.left_layout_pic);
            rightPicLayout=itemView.findViewById(R.id.right_layout_pic);
            leftVoteLayout=itemView.findViewById(R.id.left_layout_vote);
            rightVoteLayout=itemView.findViewById(R.id.right_layout_vote);

            leftTextAva=itemView.findViewById(R.id.left_avatar);
            rightTextAva=itemView.findViewById(R.id.right_avatar);
            leftPicAva=itemView.findViewById(R.id.left_avatar_pic);
            rightPicAva=itemView.findViewById(R.id.right_avatar_pic);
            leftVoteAva=itemView.findViewById(R.id.left_avatar_vote);
            rightVoteAva=itemView.findViewById(R.id.right_avatar_vote);

            leftVoteTitle=itemView.findViewById(R.id.left_text_vote);
            rightVoteTitle=itemView.findViewById(R.id.right_text_vote);

            leftTextMsg=itemView.findViewById(R.id.left_text);
            rightTextMsg=itemView.findViewById(R.id.right_text);
            leftPicMsg=itemView.findViewById(R.id.left_pic);
            rightPicMsg=itemView.findViewById(R.id.right_pic);
        }
    }

    public MsgAdapter(Context context,List<Msg> mMsgList){
        this.mMsgList=mMsgList;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        holder=new ViewHolder(view);
        holder.leftTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.rightTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.leftPicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.rightPicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.leftVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 跳转Vote界面
                if (!mMsgList.get(holder.getAdapterPosition()).isOnly()){
                    toUserVote();
                    mMsgList.get(holder.getAdapterPosition()).setOnly(true);
                }else
                    Toast.makeText(context, "投票仅限一次", Toast.LENGTH_SHORT).show();

            }
        });

        holder.rightVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 跳转Vote界面
                if (!mMsgList.get(holder.getAdapterPosition()).isOnly()){
                    toUserVote();
                    mMsgList.get(holder.getAdapterPosition()).setOnly(true);
                }else
                    Toast.makeText(context, "投票仅限一次", Toast.LENGTH_SHORT).show();
            }
        });


        return holder;
    }

    private void toUserVote() {
        Intent intent=new Intent(context,UsersVoteActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("VOTE_ID_RESULT",
                mMsgList.get(holder.getAdapterPosition()).getVoteId()
                        + ","+mMsgList.get(holder.getAdapterPosition()).getVoteResultId());
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Msg msg=mMsgList.get(position);
        if (msg.getType()==Msg.TYPE_RECEIVED){
            //TODO 收到的消息
            if (msg.getMsgType()==Msg.TYPE_TEXT){
                //TODO 消息为文本消息
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftPicLayout.setVisibility(View.GONE);
                holder.leftVoteLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);
                List<String> users=new ArrayList<>();
                users.add(msg.getId());
                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        for (TIMUserProfile timUserProfile : timUserProfiles) {
                            String faceUrl = timUserProfile.getFaceUrl();
                            long genderValue = timUserProfile.getGender().getValue();
                            if (TextUtils.isEmpty(faceUrl)) {
                                if (genderValue==1){
                                    ImgUtils.loadRound(R.drawable.right_ava, holder.leftTextAva);
                                }else {
                                    ImgUtils.loadRound(R.drawable.left_ava, holder.leftTextAva);
                                }
                            } else {
                                ImgUtils.loadRound(faceUrl, holder.leftTextAva);
                            }
                        }
                    }
                });
                holder.leftTextMsg.setText(msg.getContent());
            } else if (msg.getMsgType()==Msg.TYPE_PIC) {
//                holder.leftPicMsg.setTag(msg.getPath());
                //TODO 消息为图片消息
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftTextLayout.setVisibility(View.GONE);
                holder.leftVoteLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);

                List<String> users=new ArrayList<>();
                users.add(msg.getId());
                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }
                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        for (TIMUserProfile timUserProfile : timUserProfiles) {
                            String faceUrl = timUserProfile.getFaceUrl();
                            long genderValue = timUserProfile.getGender().getValue();
                            if (TextUtils.isEmpty(faceUrl)) {
                                if (genderValue==1){
                                    ImgUtils.loadRound(R.drawable.right_ava, holder.leftPicAva);
                                }else {
                                    ImgUtils.loadRound(R.drawable.left_ava, holder.leftPicAva);
                                }
                            } else {
                                ImgUtils.loadRound(faceUrl, holder.leftPicAva);
                            }
                        }
                    }
                });

                ImgUtils.loadFourRound(msg.getPath(),holder.leftPicMsg);
            }else if (msg.getMsgType()==Msg.TYPE_VOTE){
                //TODO 他人处理投票
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftPicLayout.setVisibility(View.GONE);
                holder.leftTextLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftVoteTitle.setText(msg.getVoteName());

                List<String> users=new ArrayList<>();
                users.add(msg.getId());
                TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        for (TIMUserProfile timUserProfile : timUserProfiles) {
                            String faceUrl = timUserProfile.getFaceUrl();
                            long genderValue = timUserProfile.getGender().getValue();
                            if (TextUtils.isEmpty(faceUrl)) {
                                if (genderValue==1){
                                    ImgUtils.loadRound(R.drawable.right_ava, holder.leftVoteAva);
                                }else {
                                    ImgUtils.loadRound(R.drawable.left_ava, holder.leftVoteAva);
                                }
                            } else {
                                ImgUtils.loadRound(faceUrl, holder.leftVoteAva);
                            }
                        }
                    }
                });

            }
        }
        else if (msg.getType()==Msg.TYPE_SENT){
            //TODO 发出的消息
            if (msg.getMsgType()==Msg.TYPE_TEXT){
                //TODO 消息为文本消息
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightPicLayout.setVisibility(View.GONE);
                holder.rightVoteLayout.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.GONE);
                TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(TIMUserProfile timUserProfile) {
                        //获取自己信息成功
                        String faceUrl = timUserProfile.getFaceUrl();
                        long genderValue = timUserProfile.getGender().getValue();
                        if (TextUtils.isEmpty(faceUrl)) {
                            if (genderValue==1){
                                ImgUtils.loadRound(R.drawable.right_ava, holder.rightTextAva);
                            }else {
                                ImgUtils.loadRound(R.drawable.left_ava, holder.rightTextAva);
                            }
                        } else {
                            ImgUtils.loadRound(faceUrl, holder.rightTextAva);
                        }
                    }
                });
                holder.rightTextMsg.setText(msg.getContent());
            }else if (msg.getMsgType()==Msg.TYPE_PIC){
//            holder.rightPicMsg.setTag(msg.getPath());
                //TODO 消息为图片消息
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightTextMsg.setVisibility(View.GONE);
                holder.rightVoteLayout.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.GONE);

                List<String> users=new ArrayList<>();
                users.add(msg.getId());
                TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(TIMUserProfile timUserProfile) {
                        //获取自己信息成功
                        String faceUrl = timUserProfile.getFaceUrl();
                        long genderValue = timUserProfile.getGender().getValue();
                        if (TextUtils.isEmpty(faceUrl)) {
                            if (genderValue==1){
                                ImgUtils.loadRound(R.drawable.right_ava, holder.rightPicAva);
                            }else {
                                ImgUtils.loadRound(R.drawable.left_ava, holder.rightPicAva);
                            }
                        } else {
                            ImgUtils.loadRound(faceUrl, holder.rightPicAva);
                        }
                    }
                });

                ImgUtils.loadFourRound(msg.getPath(),holder.rightPicMsg);
            }else if (msg.getMsgType()==Msg.TYPE_VOTE){
                //TODO 处理自己投票
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightPicLayout.setVisibility(View.GONE);
                holder.rightTextLayout.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.GONE);
                TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(TIMUserProfile timUserProfile) {
                        //获取自己信息成功
                        String faceUrl = timUserProfile.getFaceUrl();
                        long genderValue = timUserProfile.getGender().getValue();
                        if (TextUtils.isEmpty(faceUrl)) {
                            if (genderValue==1){
                                ImgUtils.loadRound(R.drawable.right_ava, holder.rightVoteAva);
                            }else {
                                ImgUtils.loadRound(R.drawable.left_ava, holder.rightVoteAva);
                            }
                        } else {
                            ImgUtils.loadRound(faceUrl, holder.rightVoteAva);
                        }
                    }
                });
                holder.rightVoteTitle.setText(msg.getVoteName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Msg msg=mMsgList.get(holder.getAdapterPosition());

        super.onViewRecycled(holder);
    }
}
