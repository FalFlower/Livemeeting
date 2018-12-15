package cn.edu.sdnu.i.livemeeting.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.sdnu.i.livemeeting.MainActivity;
import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.adapter.HomeRecAdaptar;
import cn.edu.sdnu.i.livemeeting.adapter.RecyclerViewAdapter;
import cn.edu.sdnu.i.livemeeting.bean.DateBean;
import cn.edu.sdnu.i.livemeeting.info.HomeMsg;
import cn.edu.sdnu.i.livemeeting.info.MsgList;
import cn.edu.sdnu.i.livemeeting.listener.OnPagerChangeListener;
import cn.edu.sdnu.i.livemeeting.listener.OnSingleChooseListener;
import cn.edu.sdnu.i.livemeeting.util.CalendarUtil;
import cn.edu.sdnu.i.livemeeting.weiget.CalendarView;

import static cn.edu.sdnu.i.livemeeting.application.LiveApplication.getApplication;

/**
 * Created by WangChang on 2016/5/15.
 */
public class MeetingFragment extends Fragment {
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private HomeRecAdaptar homeRecAdaptar;
    private int[] cDate = CalendarUtil.getCurrentDate();
    private ImageView search;
    private ImageView today;
    private ImageView arrowLeft;
    private ImageView arrowRight;
    private SmartRefreshLayout smartRefreshLayout;
    private List<HomeMsg> homeMsgList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting, container, false);
        final TextView title = (TextView) view.findViewById(R.id.meeting_title);
        //当前选中的日期
        calendarView = (CalendarView) view.findViewById(R.id.calendar);
        recyclerView=view.findViewById(R.id.meeting_rec);
        search=view.findViewById(R.id.meeting_search);
        today=view.findViewById(R.id.meeting_today);
        arrowLeft=view.findViewById(R.id.meeting_arrow_left);
        arrowRight=view.findViewById(R.id.meeting_arrow_right);
        smartRefreshLayout=view.findViewById(R.id.meeting_refresh);
        setClick();
        updateView();

        HashMap<String, String> map = new HashMap<>();
        map.put("2017.10.30", "qaz");
        map.put("2017.10.1", "wsx");
        map.put("2017.11.12", "yhn");
        map.put("2017.9.15", "edc");
        map.put("2017.11.6", "rfv");
        map.put("2017.11.11", "tgb");
        calendarView
                .setStartEndDate("2016.1", "2028.12")
                .setDisableStartEndDate("2016.10.10", "2028.10.10")
                .setInitDate(cDate[0] + "." + cDate[1])
                .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                .init();

        title.setText(cDate[0] + "年" + cDate[1] + "月");

        calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                title.setText(date[0] + "年" + date[1] + "月");
            }
        });

        calendarView.setOnSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date) {
                title.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月");
//                if (date.getType() == 1) {
//                    chooseDate.setText("当前选中的日期：" + date.getSolar()[0] + "年" + date.getSolar()[1] + "月" + date.getSolar()[2] + "日");
//                }
            }
        });

        return view;
    }


    private void setClick() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                someday(view);
            }
        });
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.today();
            }
        });
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.lastMonth();
            }
        });
        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.nextMonth();
            }
        });
        smartRefreshLayout.autoRefresh(400);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
                homeMsgList.clear();
                updateView();
            }
        });
    }

    private void updateView() {

//创建回调
        TIMValueCallBack<List<TIMGroupBaseInfo>> cb = new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
            }
            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {//参数返回各群组基本信息

                for(TIMGroupBaseInfo info : timGroupInfos) {
//创建待获取信息的群组Id列表
                    ArrayList<String> groupList = new ArrayList<String>();
                    groupList.add(info.getGroupId());
//获取群组详细信息
                    TIMGroupManager.getInstance().getGroupDetailInfo(
                            groupList, //需要获取信息的群组Id列表
                            new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
                                @Override
                                public void onError(int i, String s) {
                                }
                                @Override
                                public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                                    for(final TIMGroupDetailInfo info : timGroupDetailInfos) {
                                        List<String> users=new ArrayList<>();
                                        users.add(info.getGroupOwner());
                                        getInfo(users,info);
                                    }
//                                    if (recyclerView==null){
//                                        Toast.makeText(getContext(), "rec is null", Toast.LENGTH_SHORT).show();
//                                    }
                                }
                            });
                }
            }
        };
//获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);

    }

    private void refresh(){
        for (int i = 0; i < homeMsgList.size(); i++) {
            switch (homeMsgList.get(i).getStatus()) {
                case 0:
                    //预约
                    break;
                case 1:
                    //进行中
                    break;
                case 2:
                    //过期
                    Log.e("会议过期",i+"");
                    HomeMsg newMsg=homeMsgList.get(i);
                    homeMsgList.remove(i);
                    homeRecAdaptar.notifyItemRemoved(i);
                    homeRecAdaptar.notifyItemRangeChanged(i,homeMsgList.size()-i);

                    homeMsgList.add(homeMsgList.size()-1,newMsg);
                    homeRecAdaptar.notifyItemInserted(homeMsgList.size()-1);
                    homeRecAdaptar.notifyItemRangeChanged(homeMsgList.size()-1,homeMsgList.size());

                    homeRecAdaptar = new HomeRecAdaptar(getContext(),homeMsgList);
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    recyclerView.setAdapter(homeRecAdaptar);
                    break;
                default:
                    break;
            }
        }


    }

    private void getInfo(List<String> users, final TIMGroupDetailInfo info) {
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplication(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {

                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    String [] result=info.getGroupIntroduction().split(",");
                    String section,local,time;
                    if (result.length==1){
                        section=result[0];
                        local="";
                        time="";
                    }else {
                        section=result[0];
                        local=result[1];
                        time=result[2];
                    }
                    HomeMsg homeMsg=new HomeMsg(info.getGroupId(),info.getGroupName(), timUserProfile.getNickName(),section,local,time,(int)info.getMemberNum());
                    homeMsgList.add(homeMsg);
                }
                homeRecAdaptar=new HomeRecAdaptar(getContext(),homeMsgList);
                homeRecAdaptar.notifyItemRangeChanged(0,homeMsgList.size());
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, OrientationHelper.VERTICAL));
                recyclerView.setAdapter(homeRecAdaptar);
            }
        });
    }

    private void someday(View v) {
        View view = LayoutInflater.from(MainActivity.instance).inflate(R.layout.input_layout, null);
        final EditText year = (EditText) view.findViewById(R.id.year);
        final EditText month = (EditText) view.findViewById(R.id.month);
        final EditText day = (EditText) view.findViewById(R.id.day);

        new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(year.getText())
                                || TextUtils.isEmpty(month.getText())
                                || TextUtils.isEmpty(day.getText())) {
                            Toast.makeText(MainActivity.instance, "请完善日期！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean result = calendarView.toSpecifyDate(Integer.valueOf(year.getText().toString()),
                                Integer.valueOf(month.getText().toString()),
                                Integer.valueOf(day.getText().toString()));
                        if (!result) {
                            Toast.makeText(MainActivity.instance, "日期越界！", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null).show();
    }


    public static MeetingFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        MeetingFragment fragment = new MeetingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
