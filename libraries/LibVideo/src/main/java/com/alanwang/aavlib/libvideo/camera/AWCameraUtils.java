package com.alanwang.aavlib.libvideo.camera;

import android.hardware.Camera;
import com.alanwang.aavlib.libutils.ALog;
import com.alanwang.aavlib.libvideo.common.AWVideoSize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 21:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraUtils {

    /**
     * 获取最匹配的预览大小
     * @param parameters
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static AWVideoSize getMostSuitableSize(Camera.Parameters parameters, int targetWidth, int targetHeight){
        ALog.d("targetWidth = " + targetWidth + ", targetHeight = " + targetHeight
                + ", ratio = " + (1.0f * targetWidth / targetHeight));

        if (parameters == null || targetWidth <= 0 || targetHeight <= 0) {
            return null;
        }
        //获取当前支持的预览尺寸列表
        List<Camera.Size> supportSizeList = parameters.getSupportedPreviewSizes();
        if (supportSizeList == null) {
            return null;
        }

        float targetRatio = 1.0f * targetWidth / targetHeight;
        List<Camera.Size> fitSizeList = new ArrayList<>();

        for (Camera.Size size : supportSizeList) {
            ALog.d("size.width = " + size.width + "--->>>size.height = " + size.height);
            if (isTargetRatio(size, targetRatio) && (size.width * size.height >= targetWidth * targetHeight)) {
                fitSizeList.add(size);
            }
        }

        Iterator<Camera.Size> iterator = fitSizeList.iterator();
        while (iterator.hasNext()) {
            Camera.Size size = iterator.next();
            if ((size.width % 4) != 0 || (size.height % 4) != 0) {
                iterator.remove();
            }
        }

        if (fitSizeList.isEmpty()) {
            return null;
        }

        //将支持的预览尺寸列表按从小到大排序，目的是为了获取较大最接近于目标尺寸的预览尺寸
        Collections.sort(fitSizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int lVal = lhs.width * lhs.height;
                int rVal = rhs.width * rhs.height;
                return compareInt(lVal, rVal);
            }
        });

        int targetPixels = targetWidth * targetHeight;
        AWVideoSize lowMaxSize = null;
        AWVideoSize highMinSize = null;

        for (Camera.Size size : fitSizeList) {
            ALog.d("supportSizeList()--->>>size.width = " + size.width + "--->>>size.height = " + size.height);
            int pixel = size.width * size.height;
            if (pixel <= targetPixels) {
                if (lowMaxSize == null) {
                    lowMaxSize = new AWVideoSize(size.width, size.height);
                } else if (pixel > lowMaxSize.getPixels()) {
                    lowMaxSize.update(size.width, size.height);
                }
            } else {
                if (highMinSize == null) {
                    highMinSize = new AWVideoSize(size.width, size.height);
                } else if (pixel < highMinSize.getPixels()) {
                    highMinSize.update(size.width, size.height);
                }
            }
        }

        if (lowMaxSize == null) {
            if (highMinSize != null) {
                lowMaxSize = highMinSize;
            }
        }
        if (lowMaxSize != null) {
            ALog.d("final_size()--->>>size.width = " + lowMaxSize.width + "--->>>size.height = " + lowMaxSize.height);
        }
        return lowMaxSize;
    }

    private static int compareInt(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    private static boolean isTargetRatio(Camera.Size size, float ratio) {
        return (int)(size.width * 100.0f / size.height) == (int)(ratio * 100.0f);
    }

}
