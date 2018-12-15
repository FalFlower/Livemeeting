package cn.edu.sdnu.i.livemeeting.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wayww.edittextfirework.FireworkView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.SearchUserAdaptar;
import cn.edu.sdnu.i.livemeeting.relationship.ShowAddFriendsInformationView;
import cn.edu.sdnu.i.livemeeting.relationship.ShowOthersInformationActivity;
import cn.edu.sdnu.i.livemeeting.util.ImgUtils;

public class SearchActivity extends AppCompatActivity {
    private EditText mSearch;
    private ListView mListView;
    private Button mDoSearch;
    private List<String> users;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<ShowAddFriendsInformationView> viewList;
    private String userId;
    private FireworkView mFireworkView;
    private static SearchUserAdaptar userAdaptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        users=new ArrayList<>();
        viewList=new ArrayList<>();
        findViews();
        setClick();
    }

    private void setClick() {

        mDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSearch.getText().toString().equals("")){
                    viewList.clear();
                   userAdaptar=new SearchUserAdaptar(viewList);
                    mListView.setAdapter(userAdaptar);
                    search();
                }
                editor.putString("search_text",mSearch.getText().toString());
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(), ShowOthersInformationActivity.class);
                intent.putExtra("userId",users.get(position));
                startActivity(intent);
            }
        });
    }

    private void search() {
        //TODO 搜索联系人
        String userId=mSearch.getText().toString();
        users.clear();
        users.add(userId);
        getUserInformation();

    }

    private void getUserInformation() {
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "没有找到该用户" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                //更新信息
                updateViews(timUserProfiles);
            }
        });
    }

    private void updateViews(List<TIMUserProfile> timUserProfiles) {
        for(TIMUserProfile res : timUserProfiles){
            updateTure(res);
        }
    }

    private void updateTure(TIMUserProfile timUserProfile) {
        //获取信息 绑定ListView
        ShowAddFriendsInformationView informationView=new ShowAddFriendsInformationView(getApplicationContext());
        String faceUrl =timUserProfile.getFaceUrl();
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        if (TextUtils.isEmpty(faceUrl)) {
            if (genderValue==1){
                ImgUtils.loadRound(R.drawable.right_ava, informationView.getmAvatar());
                ImgUtils.load(R.drawable.right_ava,informationView.getmGender());
            }else {
                ImgUtils.loadRound(R.drawable.left_ava, informationView.getmAvatar());
                ImgUtils.load(R.drawable.left_ava,informationView.getmGender());
            }
        } else {
            ImgUtils.loadRound(faceUrl, informationView.getmAvatar());
        }
        informationView.setmNickNameAndId(timUserProfile.getNickName()+"("+timUserProfile.getIdentifier()+")");
        informationView.setmLocation(timUserProfile.getLocation());
        viewList.add(informationView);
        userAdaptar=new SearchUserAdaptar(viewList);
        mListView.setAdapter(userAdaptar);
    }

    private void findViews() {
        mSearch=findViewById(R.id.search_main);
        mListView=findViewById(R.id.show_search_results_list);
        mDoSearch=findViewById(R.id.do_search);
        mFireworkView = findViewById(R.id.search_fire_work);
        mFireworkView.bindEditText(mSearch);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        editor.putString("search_text",mSearch.getText().toString());
    }
}
