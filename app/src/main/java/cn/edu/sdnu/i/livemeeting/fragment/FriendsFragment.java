package cn.edu.sdnu.i.livemeeting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.activity.SearchActivity;
import cn.edu.sdnu.i.livemeeting.adapter.FriendsGroupAdaptar;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import cn.edu.sdnu.i.livemeeting.relationship.InGroupShowFirendsListView;
import cn.edu.sdnu.i.livemeeting.relationship.ShowOthersInformationActivity;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

/**
 * Created by WangChang on 2016/5/15.
 */
public class FriendsFragment extends Fragment {
    private LinearLayout mSearch;
    private ListView mFirendsGroup;
    private LinearLayout isHide;
    private boolean isHideClick=false;
    private ImageView arrow;

    private SmartRefreshLayout refreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        findViews(view);
        createFriendsListView();
        setClick();
        return view;
    }

    private void createFriendsListView() {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
            }
            @Override
            public void onSuccess(List<TIMUserProfile> result){
                List<InGroupShowFirendsListView> inGroupShowFirendsListViewList=new ArrayList<>();
                for(TIMUserProfile res : result){
                    SharedPreferencesUtil preferencesUtil=new SharedPreferencesUtil(getContext());
                    String Myid=preferencesUtil.doGetString("MyId","iiiiiiii");
                    if (!res.getIdentifier().equals(Myid)){
                        InGroupShowFirendsListView inGroupShowFirendsListView=new InGroupShowFirendsListView(getContext());
                        String faceUrl = res.getFaceUrl();
                        long genderValue = res.getGender().getValue();
                        if (TextUtils.isEmpty(faceUrl)) {
                            if (genderValue==1){
                                ImgUtils.loadRound(R.drawable.right_ava,inGroupShowFirendsListView.getmAvatar());
                            }else {
                                ImgUtils.loadRound(R.drawable.left_ava,inGroupShowFirendsListView.getmAvatar());
                            }
                        } else {
                            ImgUtils.loadRound(faceUrl, inGroupShowFirendsListView.getmAvatar());
                        }
                        if (!res.getRemark().isEmpty()){
                            inGroupShowFirendsListView.setmName(res.getRemark());
                        }else if (!res.getNickName().isEmpty()){
                            inGroupShowFirendsListView.setmName(res.getNickName());
                        }else {
                            inGroupShowFirendsListView.setmName(res.getIdentifier());
                        }
                        inGroupShowFirendsListViewList.add(inGroupShowFirendsListView);
                    }
                    FriendsGroupAdaptar friendsGroupAdaptar=new FriendsGroupAdaptar(inGroupShowFirendsListViewList);
                    mFirendsGroup.setAdapter(friendsGroupAdaptar);
                    }
            }
        });
    }

    private void setClick() {
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        mFirendsGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }
                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        Log.e("num",timUserProfiles.size()+"");
                        for (TIMUserProfile timUserProfile : timUserProfiles) {
                            Log.e("tim",timUserProfile.getIdentifier());
                        }
                        Intent intent=new Intent(getContext(), ShowOthersInformationActivity.class);
                        String id=timUserProfiles.get(position).getIdentifier();
                        if (!id.equals(LiveApplication.getApplication().getSelfProfile().getIdentifier())){
                            intent.putExtra("userId",id);
                        }else {
                            intent.putExtra("userId",timUserProfiles.get(position+1).getIdentifier());
                        }

                        startActivity(intent);

                    }
                });
            }
        });
        isHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isHideClick){
                    isHideClick=true;
                    mFirendsGroup.setVisibility(View.VISIBLE);
                    ImgUtils.load(R.drawable.ic_ar_down,arrow);
                }else {
                    isHideClick=false;
                    mFirendsGroup.setVisibility(View.GONE);
                    ImgUtils.load(R.drawable.ic_ar_right,arrow);
                }

            }
        });
    }
    private void findViews(View view) {
        mSearch = view.findViewById(R.id.search);
        mFirendsGroup = view.findViewById(R.id.friends_group);
        isHide=view.findViewById(R.id.friends_hide);
        arrow=view.findViewById(R.id.friends_arrow);

        //设置下拉刷新 上拉加载
        refreshLayout = view.findViewById(R.id.refresh_Layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //更新好友列表
                refreshlayout.finishRefresh(1500);
                createFriendsListView();
            }
        });
    }


    public static FriendsFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
