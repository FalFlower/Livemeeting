package cn.edu.sdnu.i.livemeeting.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader  extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }
    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
}
