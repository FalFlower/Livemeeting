package cn.edu.sdnu.i.livemeeting.activity.bmob;

import android.hardware.Camera;

/**
 * Created by JiangJun on 2018/1/23.
 * 用于检测拍照功能是否能使用
 */
public class CommonUtil {
    public static boolean isCameraCanUse(){
        boolean canUse = true;
        Camera mCamera = null;
        //用打开相机对象是否报异常检测
        try {mCamera = Camera.open();}
        catch (Exception e) {canUse = false;}
        if (canUse) {
            //如果相机资源已被占用，释放内存，重启相机
            if (mCamera != null){mCamera.release();}
        }
        return canUse;
    }
}
