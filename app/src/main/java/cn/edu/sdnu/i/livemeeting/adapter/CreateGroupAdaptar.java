package cn.edu.sdnu.i.livemeeting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.zip.Inflater;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;

public class CreateGroupAdaptar extends RecyclerView.Adapter<CreateGroupAdaptar.CreateGroupHolder> {
   private Context context;
   private CreateGroupHolder holder;
   private List<String> avaList;

   public CreateGroupAdaptar(Context context,List<String> avaList){
       this.context=context;
       this.avaList=avaList;
   }
    @Override
    public CreateGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_img,parent,false);
        holder=new CreateGroupHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CreateGroupHolder holder, int position) {
       if (avaList.get(position).equals("男")){
           ImgUtils.loadRound(R.drawable.right_ava,holder.imageView);
       }else if (avaList.get(position).equals("女")){
           ImgUtils.loadRound(R.drawable.left_ava,holder.imageView);
       }else {
           ImgUtils.loadRound(avaList.get(position),holder.imageView);
       }

    }

    @Override
    public int getItemCount() {
        return avaList.size();
    }

    class CreateGroupHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public CreateGroupHolder(View view){
            super(view);
            imageView=view.findViewById(R.id.img);
        }
    }
}
