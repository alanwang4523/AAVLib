package com.alanwang.aavlib.utils;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: AlanWang4523.
 * Date: 19/1/24 01:19.
 * Mail: alanwang4523@gmail.com
 */

public class OrientationChangeManager {
    final static OrientationChangeManager sInstance = new OrientationChangeManager();

    public interface OrientationChangeListener {
        /**
         * 通知设备方向改变
         * @param degree 当前的角度 [0, 359]
         * @param orientation 当前的方向 0、90、180、270
         */
        void onOrientationChanged(int degree, int orientation);
    }

    private OrientationEventListener mOrientationEventListener;
    private int mCurOrientation;// 0、90、180、270
    private int mCurDegree;// 0~359
    private ArrayList<OrientationChangeListener> mListenerList = new ArrayList<>();

    /**
     * 开始检测方向
     * @param context
     */
    public static void startDetectOrientation(Context context) {
        sInstance.enableDetectOrientation(context);
    }

    /**
     * 停止检测方向
     */
    public static void stopDetectOrientation() {
        sInstance.disableDetectOrientation();
    }

    /**
     * 添加方向改变的监听器
     * @param listener
     */
    public static void addOrientationChangeListener(OrientationChangeListener listener) {
        sInstance.addListener(listener);
    }

    /**
     * 移除方向改变的监听器
     * @param listener
     */
    public static void removeOrientationChangeListener(OrientationChangeListener listener) {
        sInstance.removeListener(listener);
    }

    /**
     * 获取设备当前的方向 0、90、180、270
     * @return
     */
    public static int getCurPhoneOrientation() {
        return sInstance.mCurOrientation;
    }

    /**
     * 获取设备当前的角度 [0, 359]
     * @return
     */
    public static int getCurPhoneDegree() {
        return sInstance.mCurDegree;
    }

    private OrientationChangeManager() {}

    private void enableDetectOrientation(Context context) {
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {
                @Override
                public void onOrientationChanged(int degree) {
                    if (degree != -1) {
                        degree %= 360;
                        mCurOrientation = (((degree + 45) / 90) * 90) % 360;
                        mCurDegree = degree;
                        if (mCurDegree != degree) {
                            notifyOrientationChanged(mCurDegree, mCurOrientation);
                        }
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
            return;
        }
        mCurDegree = 0;
        mCurOrientation = 0;
        notifyOrientationChanged(mCurDegree, mCurOrientation);
    }

    private void disableDetectOrientation() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }

    private void addListener(OrientationChangeListener listener) {
        synchronized (mListenerList) {
            if (!mListenerList.contains(listener)) {
                mListenerList.add(listener);
            }
        }
    }

    private void removeListener(OrientationChangeListener listener) {
        synchronized (mListenerList) {
            mListenerList.remove(listener);
        }
    }

    private void notifyOrientationChanged(int degree, int orientation) {
        synchronized (mListenerList) {
            Iterator<OrientationChangeListener> iterator = mListenerList.iterator();
            while (iterator.hasNext()) {
                OrientationChangeListener listener = iterator.next();
                listener.onOrientationChanged(degree, orientation);
            }
        }
    }
}
