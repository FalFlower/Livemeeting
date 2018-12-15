package cn.edu.sdnu.i.livemeeting.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.SingleChatActivity;
import cn.edu.sdnu.i.livemeeting.info.MsgList;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;

    private List<MsgList> data;

//    private int position;

    public RecyclerViewAdapter(Context context, List<MsgList> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.imageView.setImageResource(this.data.get(position));
        final MsgList msg=data.get(position);
        if (!TextUtils.isEmpty(msg.getFaceUrl()))
            ImgUtils.loadRound(msg.getFaceUrl(),holder.mAvatar);
        else
            ImgUtils.loadRound(R.drawable.left_ava,holder.mAvatar);

        String con="";
        con=msg.getContent();
        if (con.length()!=0){
            holder.mMesContent.setText(con);
        }else {
            holder.mMesContent.setText("[图片]");
        }

        holder.mName.setText(msg.getName());
        holder.mMesBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 聊天
                Intent intent=new Intent(context,SingleChatActivity.class);
                intent.putExtra("chat_id",msg.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mMesBody;
        private ImageView mAvatar;
        private TextView mName;
        private TextView mMesContent;
//        private TextView mTime;
        private ImageView mNewMes;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.mAvatar =  itemView.findViewById(R.id.message_ava);
            this.mName = itemView.findViewById(R.id.message_name);
            this.mMesContent =  itemView.findViewById(R.id.message_content);
//            this.mTime = itemView.findViewById(R.id.message_time);
            this.mNewMes = itemView.findViewById(R.id.message_new);
            this.mMesBody=itemView.findViewById(R.id.message_body);
        }
    }


}
