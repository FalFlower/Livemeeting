package cn.edu.sdnu.i.livemeeting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.tencent.TIMCallBack;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import cn.edu.sdnu.i.livemeeting.R;

public class ManagerActivity extends AppCompatActivity {

    private EditText title;
    private EditText section;
    private EditText local;
    private EditText timeY;
    private EditText timeM;
    private EditText timeD;
    private Button ok;
    private String groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        initView();
        setClick();
    }

    private void setClick() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDate()){
                    setChange();
                }else {
                    Toast.makeText(ManagerActivity.this, "请检查日期设置是否完善，日期格式为（2018-04-13）", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setChange() {
        ArrayList<String> groupList = new ArrayList<String>();
        groupList.add(groupId);
//获取群组详细信息
        TIMGroupManager.getInstance().getGroupDetailInfo(
                groupList, //需要获取信息的群组Id列表
                new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(ManagerActivity.this, "获取群组消息失败 错误码："+i, Toast.LENGTH_SHORT).show();
                    }
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                        for(TIMGroupDetailInfo info : timGroupDetailInfos) {
                            String [] result=info.getGroupIntroduction().split(",");
                            String sections,locals,timeS,titleS;
                            if (result.length==1){
                                sections=result[0];
                                locals="";
                                timeS="";
                            }else {
                                sections=result[0];
                                locals=result[1];
                                timeS=result[2];
                            }
                            if (!title.getText().toString().isEmpty()){
                                titleS= title.getText().toString();
                                //修改群组名称
                                TIMGroupManager.getInstance().modifyGroupName(
                                        groupId,                //群组Id
                                        titleS,       //新名称
                                        new TIMCallBack() {
                                            @Override
                                            public void onError(int i, String s) {
                                                Toast.makeText(ManagerActivity.this, "修改Title失败", Toast.LENGTH_SHORT).show();
                                            }
                                            @Override
                                            public void onSuccess() {
                                            }
                                        }); //回调
                            }
                            if (!section.getText().toString().isEmpty()){
                                sections= title.getText().toString();
                            }
                            if (!local.getText().toString().isEmpty()){
                                locals= title.getText().toString();
                            }
                            String date=timeY.getText().toString()+"-"+timeM.getText().toString()+"-"+timeD.getText().toString();
                            if (!date.equals(timeS)){
                                timeS=date;
                            }

                            String msg=sections+","+locals+","+timeS;
                            TIMGroupManager.getInstance().modifyGroupIntroduction(groupId, msg, new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                }
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(ManagerActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }

    private void initView() {
        title=findViewById(R.id.change_edt_title);
        section=findViewById(R.id.change_edt_section);
        local=findViewById(R.id.change_edt_local);
        timeY=findViewById(R.id.change_time_year);
        timeM=findViewById(R.id.change_time_month);
        timeD=findViewById(R.id.change_time_day);
        ok=findViewById(R.id.change_ok);

        Intent intent=getIntent();
        groupId=intent.getStringExtra("manger_group_id");
    }


    private boolean checkDate() {
        String year = timeY.getText().toString();
        String month = timeM.getText().toString();
        String day = timeD.getText().toString();
        return (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(month) || !TextUtils.isEmpty(day)) && (year.length() == 4 || (month.length() == 2 || month.length() == 1) || (day.length() == 2 || day.length() == 1));
    }

}
