package cn.edu.sdnu.i.livemeeting.adapter;

import android.content.Context;
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

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.ChooseFriendsActivity;
import cn.edu.sdnu.i.livemeeting.activity.LoginActivity;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;

public class ChooseFriendsAvaptar extends RecyclerView.Adapter<ChooseFriendsAvaptar.ChooseFriendsHolder>{
    private Context context;
    private List<String> list;
    private ChooseFriendsHolder holder;

    public ChooseFriendsAvaptar(Context context,List<String> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public ChooseFriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.view_friends_group_list,parent,false);
        holder=new ChooseFriendsHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        new SharedPreferencesUtil(getApplication()).doPutString("choose_friend",timUserProfiles.get(holder.getAdapterPosition()).getIdentifier());
                        ChooseFriendsActivity.getInstance().finish();
                    }
                });
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ChooseFriendsHolder holder, int position) {
        List<String> stringList=new ArrayList<>();
        stringList.add(list.get(position));
        TIMFriendshipManager.getInstance().getUsersProfile(stringList, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                //更新信息
                Toast.makeText(context, "更新信息"+timUserProfiles.get(0).getNickName(), Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(timUserProfiles.get(0).getFaceUrl())){
                    ImgUtils.loadRound(timUserProfiles.get(0).getFaceUrl(),holder.imageView);
                }else
                    ImgUtils.loadRound(R.drawable.left_ava,holder.imageView);
                if (timUserProfiles.get(0).getRemark().equals("")){
                    holder.textView.setText(timUserProfiles.get(0).getNickName());
                }else
                    holder.textView.setText(timUserProfiles.get(0).getRemark());
                Log.e("get",timUserProfiles.get(0).getRemark());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ChooseFriendsHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ChooseFriendsHolder(View view){
            super(view);
            textView=view.findViewById(R.id.friends_name);
            imageView=view.findViewById(R.id.friends_avar);
            linearLayout=view.findViewById(R.id.friend_layout);
        }
    }
}
