package com.alanwang.aav.alvideoeditor.business.record;

/**
 * Author: AlanWang4523.
 * Date: 19/4/17 07:45.
 * Mail: alanwang4523@gmail.com
 */
public interface CameraRecordContract {

    interface View {
        /**
         * 显示加载的 View
         */
        void showLoadingView();

        /**
         * 隐藏加载的 View
         */
        void dismissLoadingView();
    }

    interface Presenter {
        /**
         * 开始录制
         */
        void startRecord();

        /**
         * 停止录制
         */
        void stopRecord();

        /**
         * 结束录制
         */
        void finishRecord();
    }
}
