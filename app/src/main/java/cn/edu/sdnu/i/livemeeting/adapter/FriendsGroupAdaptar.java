package cn.edu.sdnu.i.livemeeting.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.edu.sdnu.i.livemeeting.relationship.InGroupShowFirendsListView;

public class FriendsGroupAdaptar extends BaseAdapter {

    private List<InGroupShowFirendsListView> listViews;

    public FriendsGroupAdaptar(List<InGroupShowFirendsListView> list){
        this.listViews=list;
    }

    @Override
    public int getCount() {
        return listViews==null?0:listViews.size();
    }

    @Override
    public Object getItem(int i) {
        return listViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView==null){
            convertView=listViews.get(i);;
            holder = new ViewHolder();
            holder.inGroupShowFirendsListView=(InGroupShowFirendsListView)convertView;
            //将当前viewHolder与converView绑定
            convertView.setTag(holder);
        }else{
            //如果不为空，获取
            holder= (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

class ViewHolder{
    InGroupShowFirendsListView inGroupShowFirendsListView;
    }
}
