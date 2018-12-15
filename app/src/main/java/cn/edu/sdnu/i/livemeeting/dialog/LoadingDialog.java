package cn.edu.sdnu.i.livemeeting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import cn.edu.sdnu.i.livemeeting.R;

/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingDialog extends Dialog {
    private TextView tv_text;

    public LoadingDialog(Context context) {
        super(context);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loading);
        tv_text = findViewById(R.id.tv_text);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 为加载进度个对话框设置不同的提示消息
     *
     * @param message 给用户展示的提示信息
     * @return build模式设计，可以链式调用
     */
    public LoadingDialog setMessage(String message) {
        tv_text.setText(message);
        return this;
    }
}
