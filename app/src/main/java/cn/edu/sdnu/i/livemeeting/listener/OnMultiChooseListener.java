package cn.edu.sdnu.i.livemeeting.listener;

import android.view.View;

import cn.edu.sdnu.i.livemeeting.bean.DateBean;

public interface OnMultiChooseListener {
    /**
     * @param view
     * @param date
     * @param flag 多选时flag=true代表选中数据，flag=false代表取消选中
     */
    void onMultiChoose(View view, DateBean date, boolean flag);

}
