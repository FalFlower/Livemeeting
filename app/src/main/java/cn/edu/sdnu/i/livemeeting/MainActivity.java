package cn.edu.sdnu.i.livemeeting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;


import cn.edu.sdnu.i.livemeeting.fragment.HomeFragment;
import cn.edu.sdnu.i.livemeeting.fragment.MeetingFragment;
import cn.edu.sdnu.i.livemeeting.fragment.MyInfoFragment;
import cn.edu.sdnu.i.livemeeting.fragment.MessageFragment;
import cn.edu.sdnu.i.livemeeting.fragment.FriendsFragment;
import cn.edu.sdnu.i.livemeeting.fragment.SelfInfoFragment;
import cn.edu.sdnu.i.livemeeting.info.Meet_User;


public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener
            , ViewPager.OnPageChangeListener {

    public static MainActivity instance=null;
    private ArrayList<Fragment> fragments;
    private BottomNavigationBar bottomNavigationBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
            setContentView(R.layout.activity_main);


            instance=this;
            createBottomBar();
            if(ContextCompat.checkSelfPermission(MainActivity.instance,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.instance,new String[]{Manifest.permission.CAMERA},1);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.instance,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.instance,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.instance,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.instance,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }


    }

    private void createBottomBar() {
        //TODO 目前刚开始进入界面的时候会闪烁几次
        //创建底部导航栏
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);//mode为非固定
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        bottomNavigationBar.setBarBackgroundColor(R.color.blue);
        BadgeItem numberBadgeItem = new BadgeItem()
                .setBorderWidth(5)
                .setBackgroundColor(Color.RED)
                .setText("5")
                .setHideOnSelect(true);//选择是否点击时才显示   .setBadgeItem(numberBadgeItem)
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home, "首页").setInActiveColorResource(R.color.black).setActiveColorResource(R.color.zt))
                .addItem(new BottomNavigationItem(R.drawable.message, "消息").setInActiveColorResource(R.color.black).setActiveColorResource(R.color.zt))
                .addItem(new BottomNavigationItem(R.drawable.metting, "会议").setInActiveColorResource(R.color.black).setActiveColorResource(R.color.zt))
                .addItem(new BottomNavigationItem(R.drawable.friendlist, "好友").setInActiveColorResource(R.color.black).setActiveColorResource(R.color.zt))
                .addItem(new BottomNavigationItem(R.drawable.mine, "我的").setInActiveColorResource(R.color.black).setActiveColorResource(R.color.zt))
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, fragments.get(0));
        transaction.commit();

    }

        private ArrayList<Fragment> getFragments() {
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(HomeFragment.newInstance("首页"));
            fragments.add(MessageFragment.newInstance("消息"));
            fragments.add(MeetingFragment.newInstance("会议"));
            fragments.add(FriendsFragment.newInstance("好友"));
            fragments.add(SelfInfoFragment.newInstance("我的"));
            return fragments;
        }

        @Override
        public void onTabSelected(int position) {
            FragmentManager fm;
            FragmentTransaction ft = null;
            Fragment from=null;
            Fragment fragment=null;
            if (fragments != null) {
                if (position < fragments.size()) {
                    fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    //当前的fragment
                    from = fm.findFragmentById(R.id.layFrame);
                    //点击即将跳转的fragment
                    fragment = fragments.get(position);
                    if (fragment.isAdded()) {
                        // 隐藏当前的fragment，显示下一个
                        ft.hide(from).show(fragment);
                    } else {
                // 隐藏当前的fragment，add下一个到Activity中
                        ft.hide(from).add(R.id.layFrame, fragment);
                    }
                    ft.commitAllowingStateLoss();
                }
            }else {
                // 隐藏当前的fragment，add下一个到Activity中
                ft.hide(from).add(R.id.layFrame, fragment);
                if (fragment.isHidden()) {
                    ft.show(fragment);
                }
            }
        }

        @Override
        public void onTabUnselected(int position) {
            //这儿也要操作隐藏，否则Fragment会重叠
            if (fragments != null) {
                if (position < fragments.size()) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fragments.get(position);
                    // 隐藏当前的fragment
                    ft.hide(fragment);
                    ft.commitAllowingStateLoss();
                }
            }

        }

        @Override
        public void onTabReselected(int position) {

        }


        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at
         *                             position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {

        }

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }


}
