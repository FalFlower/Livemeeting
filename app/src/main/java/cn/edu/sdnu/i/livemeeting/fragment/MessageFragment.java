package cn.edu.sdnu.i.livemeeting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.TextView;
import android.widget.Toast;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.SearchActivity;
import cn.edu.sdnu.i.livemeeting.adapter.RecyclerViewAdapter;
import cn.edu.sdnu.i.livemeeting.info.MsgList;

/**
 * Created by WangChang on 2016/5/15.
 */
public class MessageFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
//    private LinearLayout searchLinearLayout;
    private ImageView search;
    private TextView toastView;
    private SmartRefreshLayout refreshLayout;
    private List<MsgList> msgLists;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView =view.findViewById(R.id.recyclerView);
        msgLists=new ArrayList<>();

        search=view.findViewById(R.id.search_layout);
        toastView=view.findViewById(R.id.message_toast);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        refreshLayout.autoRefresh();
        //设置刷新
        setRefresh();
        updateView();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void updateView() {
//TODO 获取会话(本地存储)
        final List<TIMConversation> timConversations= TIMManager.getInstance().getConversionList();
        final List<String> isRIList=new ArrayList<>();
        for (final TIMConversation conversation : timConversations) {
            if (conversation.getType()== TIMConversationType.C2C) {
                boolean is=false;
                if (!(isRIList.size()==0)){
                    for (String s : isRIList) {
                        if (conversation.getPeer().isEmpty()){
                            is=true;
                            break;
                        }
                        if (s.equals(conversation.getPeer()))
                        {
                            is=true;
                            break;
                        }
                    }
                }
                if (!is){
                    MsgList msg;
                    TIMElem elem=conversation.getLastMsgs(1).get(0).getElement(0);
                    TIMElemType elemType = elem.getType();
                    if (elemType == TIMElemType.Text){
                        TIMTextElem timTextElem=(TIMTextElem)elem;
                        String mesContent=timTextElem.getText();
                        msg=new MsgList(MainActivity.instance,conversation.getPeer(),mesContent);
                    }else {
                        msg=new MsgList(MainActivity.instance,conversation.getPeer(),"");
                    }
                    isRIList.add(conversation.getPeer());
                    msgLists.add(msg);
                    toastView.setVisibility(View.GONE);
                }
            }

        }

        adapter = new RecyclerViewAdapter(this.getContext(),msgLists);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        //消息监听
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                for (TIMMessage timMessage : list) {
                    boolean isLive=false;
                    for (MsgList msgList : msgLists) {
                        if (timMessage.getSender().equals(msgList)){
                            isLive=true;
                        }
                    }
                    if (isLive){
                        for (int i=0;i<timMessage.getElementCount();i++){
                            //TODO Bug  timMessage.getSenderProfile().getIdentifier() null
                            TIMConversation timConversation=TIMManager.getInstance().getConversation(TIMConversationType.C2C,timMessage.getSender());
                            int position=isRIList.indexOf(timConversation.getPeer());
                            String str = null;
                            if (!timConversation.getLastMsgs(1).isEmpty()){
                                TIMElem elem=timConversation.getLastMsgs(1).get(0).getElement(0);
                                TIMElemType elemType = elem.getType();
                                if (elemType==TIMElemType.Text){
                                    TIMTextElem timTextElem=(TIMTextElem)elem;
                                    str=timTextElem.getText();
                                }
                                if (!(isRIList.size()<=position)){
                                    isRIList.remove(position);
                                    msgLists.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position,msgLists.size()-position);

                                    MsgList msg1=new MsgList(MainActivity.instance,timConversation.getPeer(),str);

                                    isRIList.add(0,msg1.getId());
                                    msgLists.add(0,msg1);
                                    adapter.notifyItemInserted(0);
                                    adapter.notifyItemRangeChanged(0,msgLists.size());

                                    adapter = new RecyclerViewAdapter(getContext(),msgLists);
                                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                                    recyclerView.setAdapter(adapter);
                                }

                            }
                        }
                    }else {
                        msgLists.clear();
                        udpate();
                    }

                }
                return false;
            }
        });
    }

    private void setRefresh() {
        //设置下拉刷新 上拉加载
//        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
                Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                msgLists.clear();
                udpate();
            }
        });
    }

    private void udpate() {
        List<TIMConversation> timConversations= TIMManager.getInstance().getConversionList();
        List<String> isRIList=new ArrayList<>();
        isRIList.add("");
        for (TIMConversation conversation : timConversations) {
            if (conversation.getType()== TIMConversationType.C2C){
                boolean is=false;
                for (String s : isRIList) {
                    if (s.equals(conversation.getPeer()))
                        is=true;
                }
                if (!is){
                    MsgList msg;
                    TIMElem elem=conversation.getLastMsgs(1).get(0).getElement(0);
                    TIMElemType elemType = elem.getType();
                    if (elemType == TIMElemType.Text){
                        TIMTextElem timTextElem=(TIMTextElem)elem;
                        String mesContent=timTextElem.getText();
                        msg=new MsgList(MainActivity.instance,conversation.getPeer(),mesContent);
                    }else {
                        msg=new MsgList(MainActivity.instance,conversation.getPeer(),"");
                    }
                    isRIList.add(conversation.getPeer());
                    msgLists.add(msg);
                    toastView.setVisibility(View.GONE);
                }
            }

        }
        adapter = new RecyclerViewAdapter(this.getContext(),msgLists);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }


    public static MessageFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
