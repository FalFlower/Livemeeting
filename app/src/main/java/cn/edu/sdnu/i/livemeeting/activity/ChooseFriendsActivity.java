package cn.edu.sdnu.i.livemeeting.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.ChooseFriendsAvaptar;
import cn.edu.sdnu.i.livemeeting.adapter.FriendsGroupAdaptar;
import cn.edu.sdnu.i.livemeeting.relationship.InGroupShowFirendsListView;
import cn.edu.sdnu.i.livemeeting.relationship.ShowOthersInformationActivity;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;
import cn.edu.sdnu.i.livemeeting.util.SharedPreferencesUtil;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getContext;

public class ChooseFriendsActivity extends AppCompatActivity {
    private static ChooseFriendsActivity instance=null;
    private EditText text;
    private Button find;
    private ListView listView;
    public static ChooseFriendsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        setContentView(R.layout.activity_choose_friends);
        initViews();
        setClick();
        updateView();
    }

    private void updateView() {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
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
                        String genderStr = genderValue == 1 ? "男" : "女";
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
                    listView.setAdapter(friendsGroupAdaptar);
                }
            }
        });
    }
    private void setClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }
                    @Override
                    public void onSuccess(final List<TIMUserProfile> timUserProfiles) {
                        if (text.getText().toString().isEmpty()){
                            Intent intent=new Intent(getContext(), ShowOthersInformationActivity.class);
                            String id=timUserProfiles.get(position).getIdentifier();
                            if (!id.equals(new SharedPreferencesUtil(getContext()).doGetString("MyId","iiiiiiii"))){
                                intent.putExtra("choose_id",id);
                                setResult(RESULT_OK, intent);
                            }else {
                                intent.putExtra("choose_id",timUserProfiles.get(position+1).getIdentifier());
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }else {
                            int count=0;
                            for (int i = 0; i < timUserProfiles.size(); i++) {
                                if (timUserProfiles.get(i).getIdentifier().equals(text.getText().toString())){
                                    count=i;
                                }
                            }
                            Log.e("dasda",count+"");
                            Intent intent=new Intent(getContext(), ShowOthersInformationActivity.class);
                            String id=timUserProfiles.get(count).getIdentifier();
                            if (!id.equals(new SharedPreferencesUtil(getContext()).doGetString("MyId","iiiiiiii"))){
                                intent.putExtra("choose_id",id);
                                setResult(RESULT_OK, intent);
                            }else {
                                intent.putExtra("choose_id",timUserProfiles.get(count+1).getIdentifier());
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }
                    }
                });
            }
        });


        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().toString().isEmpty()) {
                    List<String> usersId=new ArrayList<>();
                    usersId.add(text.getText().toString());
                    TIMFriendshipManager.getInstance().getUsersProfile(usersId, new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(getApplicationContext(), "没有找到该用户", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            //更新信息
                            List<InGroupShowFirendsListView> inGroupShowFirendsListViewList=new ArrayList<>();
                            for(TIMUserProfile res : timUserProfiles){
                                SharedPreferencesUtil preferencesUtil=new SharedPreferencesUtil(getContext());
                                String Myid=preferencesUtil.doGetString("MyId","iiiiiiii");
                                if (!res.getIdentifier().equals(Myid)){
                                    InGroupShowFirendsListView inGroupShowFirendsListView=new InGroupShowFirendsListView(getContext());
                                    String faceUrl = res.getFaceUrl();
                                    long genderValue = res.getGender().getValue();
                                    String genderStr = genderValue == 1 ? "男" : "女";
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
                                listView.setAdapter(friendsGroupAdaptar);
                            }
                        }
                    });
                }else {
                    updateView();
                }

            }
        });
    }

    private void initViews() {
        text=findViewById(R.id.choose_friends_edit);
        find=findViewById(R.id.choose_friends_find);
//        recyclerView=findViewById(R.id.choose_friends_list);
        listView=findViewById(R.id.choose_friends_list);
    }

}
