package com.alanwang.aav.alvideoeditor.business.record;

/**
 * Author: AlanWang4523.
 * Date: 19/4/17 08:16.
 * Mail: alanwang4523@gmail.com
 */
public class CameraRecordPresenter implements CameraRecordContract.Presenter {

    private CameraRecordContract.View mViewer;

    public CameraRecordPresenter(CameraRecordContract.View viewer) {
        this.mViewer = viewer;
    }

    @Override
    public void startRecord() {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public void finishRecord() {

    }
}
