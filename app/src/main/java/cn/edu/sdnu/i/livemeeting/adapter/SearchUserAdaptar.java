package cn.edu.sdnu.i.livemeeting.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.relationship.ShowAddFriendsInformationView;
/*
*
* */
public class SearchUserAdaptar extends BaseAdapter {
    List<ShowAddFriendsInformationView> list;

    public SearchUserAdaptar( List<ShowAddFriendsInformationView> list){
        this.list=new ArrayList<>();
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView==null){
            convertView=list.get(i);;
            holder = new ViewHolder();
            holder.addFriendsInformationView=(ShowAddFriendsInformationView) convertView;
            //将当前viewHolder与converView绑定
            convertView.setTag(holder);
        }else{
            //如果不为空，获取
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    class ViewHolder{
        ShowAddFriendsInformationView addFriendsInformationView;
    }
}

