package cn.edu.sdnu.i.livemeeting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import cn.edu.sdnu.i.livemeeting.R;
import cn.edu.sdnu.i.livemeeting.application.LiveApplication;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImgUtils {

    public static void load(Object url, ImageView targetView) {
        Glide.with(LiveApplication.getContext())
                .load(url)
                .dontAnimate()
                .into(targetView);
    }
    public static void loadFourRound(Object url, ImageView targetView) {
        Glide.with(LiveApplication.getContext())
                .load(url)
                .dontAnimate()
                .bitmapTransform(new RoundedCornersTransformation(LiveApplication.getContext(), 10, 0, RoundedCornersTransformation.CornerType.ALL))
                .into(targetView);
    }

    public static void loadRound(Object url, ImageView targetView) {
        Glide.with(LiveApplication.getContext())
                .load(url)
                .bitmapTransform(new CropCircleTransformation(LiveApplication.getContext()))
                .into(targetView);
    }
    public static void loadGauss(Context context,Object url, ImageView targetView){
        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade()
                .dontAnimate()
                .bitmapTransform(new BlurTransformation(context))
                .into(targetView);
    }

    static class BlurTransformation extends BitmapTransformation {

        private RenderScript rs;

        public BlurTransformation(Context context) {
            super( context );

            rs = RenderScript.create( context );
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap blurredBitmap = toTransform.copy( Bitmap.Config.ARGB_8888, true );

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(
                    rs,
                    blurredBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SHARED
            );
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(10);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            toTransform.recycle();

            return blurredBitmap;
        }

        @Override
        public String getId() {
            return "blur";
        }
    }
}
