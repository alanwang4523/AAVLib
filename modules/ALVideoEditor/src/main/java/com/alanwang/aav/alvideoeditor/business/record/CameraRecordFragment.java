/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aav.alvideoeditor.business.record;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alanwang.aav.algeneral.common.AWTimer;
import com.alanwang.aav.algeneral.ui.AWLoadingDialog;
import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aav.algeneral.ui.AWSegmentProgressBar;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.AWRecVideoInfo;
import com.alanwang.aav.alvideoeditor.beans.AWSegmentInfo;
import com.alanwang.aav.alvideoeditor.business.preview.AWVideoPreviewActivity;
import com.alanwang.aav.alvideoeditor.core.AWMediaConstants;
import com.alanwang.aav.alvideoeditor.core.AWMp4ParserHelper;
import com.alanwang.aav.alvideoeditor.ui.StyleFilterView;
import com.alanwang.aavlib.utils.ALog;
import com.alanwang.aavlib.utils.TimeUtils;
import com.alanwang.aavlib.video.core.AWVideoCameraScheduler;
import com.alanwang.aavlib.video.surface.AWSurfaceView;
import com.alanwang.aavlib.video.surface.ISurfaceCallback;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/17 07:53.
 * Mail: alanwang4523@gmail.com
 */
public class CameraRecordFragment extends Fragment implements
        ISurfaceCallback,
        AWTimer.TimerListener,
        View.OnClickListener,
        View.OnTouchListener {

    public interface CameraRecordFragmentListener {

        void closeCameraRecordFragment();
    }

    public static CameraRecordFragment newInstance() {
        return new CameraRecordFragment();
    }

    private final static int TIME_UPDATE_INTERVAL = 50;

    private EnhancedRelativeLayout mVideoLayout;
    private AWSurfaceView mAWSurfaceView;
    private AWSegmentProgressBar mSegmentProgressBar;

    private RelativeLayout rlTopOperation;
    private ImageView btnClose;
    private ImageView btnCameraSwitchCover;
    private ImageView btnFlashlightSwitchCover;

    private ImageView btnSpeedSwitchCover;
    private ImageView btnFaceBeauty;
    private ImageView btnStyleFilter;

    private ImageView btnDeleteSegment;
    private AWRecordButton btnRecord;
    private ImageView btnRecordDone;

    private StyleFilterView styleFilterView;

    private AWVideoCameraScheduler mVideoCameraScheduler;
    private boolean mIsFrontCamera = true;
    private File mVideoSaveDir;
    private AWRecVideoInfo mRecVideoInfo = new AWRecVideoInfo();
    private AWSegmentInfo mLastSegmentInfo;

    private AlertDialog mExitConfirmDialog;
    private AWLoadingDialog mLoadingDialog;
    private CameraRecordFragmentListener mFragmentLister;

    private AWTimer mRecordTimer;
    private long mMinRecordProgress = 3 * 1000;
    private long mMaxRecordProgress = 15 * 1000;
    private long mCurRecordProgress = 0L;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.aav_fragment_camera_record, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }

    private void initData() {
        mVideoSaveDir = new File(AWMediaConstants.VIDEO_RECORD_DIR_PATH + TimeUtils.getCurrentTime());
        if (!mVideoSaveDir.exists()) {
            mVideoSaveDir.mkdirs();
        }

        mRecVideoInfo.setCurrentDir(mVideoSaveDir.getAbsolutePath());
        mRecVideoInfo.setWidth(AWMediaConstants.VIDEO_ENCODE_WIDTH);
        mRecVideoInfo.setHeight(AWMediaConstants.VIDEO_ENCODE_HEIGHT);
        mRecVideoInfo.setBitrate(AWMediaConstants.VIDEO_ENCODE_BITRATE);

        mVideoCameraScheduler = new AWVideoCameraScheduler();
        mVideoCameraScheduler.setupRecord(mRecVideoInfo.getWidth(), mRecVideoInfo.getHeight(), mRecVideoInfo.getBitrate());

    }

    private void initView(View view) {
        mVideoLayout = view.findViewById(R.id.video_lyt);
        mVideoLayout.setOnTouchListener(this);

        mAWSurfaceView = view.findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(this);

        mSegmentProgressBar = view.findViewById(R.id.spb_record_progress);
        mSegmentProgressBar.setMinProgress(mMinRecordProgress);
        mSegmentProgressBar.setMaxProgress(mMaxRecordProgress);


        rlTopOperation = view.findViewById(R.id.rl_top_options);
        btnClose = view.findViewById(R.id.iv_btn_close);
        btnClose.setOnClickListener(this);

        btnFlashlightSwitchCover = view.findViewById(R.id.iv_btn_flashlight_switchover);
        btnFlashlightSwitchCover.setOnClickListener(this);
        btnFlashlightSwitchCover.setSelected(true);

        btnCameraSwitchCover = view.findViewById(R.id.iv_btn_camera_switchover);
        btnCameraSwitchCover.setOnClickListener(this);

        btnSpeedSwitchCover = view.findViewById(R.id.iv_btn_record_speed);
        btnSpeedSwitchCover.setOnClickListener(this);

        btnFaceBeauty = view.findViewById(R.id.iv_btn_record_beauty);
        btnFaceBeauty.setOnClickListener(this);

        btnStyleFilter = view.findViewById(R.id.iv_btn_record_filter);
        btnStyleFilter.setOnClickListener(this);

        styleFilterView = view.findViewById(R.id.bottom_style_effect_view);

        btnRecord = view.findViewById(R.id.btn_camera_record);
        btnRecord.setRecordListener(new AWRecordButton.OnRecordListener() {
            @Override
            public void onRecordStart() {
                mLastSegmentInfo = new AWSegmentInfo();
                File file = new File(mVideoSaveDir,
                        AWMediaConstants.PREFIX_VIDEO_SEGMENT_NAME +
                                mRecVideoInfo.getSegmentsSize() +
                                AWMediaConstants.SUFFIX_MP4);
                mLastSegmentInfo.setFilePath(file.getAbsolutePath());
                mLastSegmentInfo.setStartTimeMs(mCurRecordProgress);
                mVideoCameraScheduler.startRecord(mLastSegmentInfo.getFilePath());
                mRecordTimer.start();
                hiddenOperationViews();
            }

            @Override
            public void onRecordStop() {
                pauseRecord();
                mLastSegmentInfo.setEndTimeMs(mCurRecordProgress);
                mRecVideoInfo.addSegment(mLastSegmentInfo);

                btnDeleteSegment.setVisibility(View.VISIBLE);
                btnRecordDone.setVisibility(View.VISIBLE);
            }
        });

        btnDeleteSegment = view.findViewById(R.id.btn_video_segment_delete);
        btnDeleteSegment.setOnClickListener(this);

        btnRecordDone = view.findViewById(R.id.btn_video_record_done);
        btnRecordDone.setOnClickListener(this);

        mRecordTimer = new AWTimer(TIME_UPDATE_INTERVAL);
        mRecordTimer.setTimerListener(this);
    }

    @Override
    public void onSurfaceChanged(Surface surface, int w, int h) {
        mVideoCameraScheduler.updateSurface(surface, w, h);
    }

    @Override
    public void onSurfaceDestroyed(Surface surface) {
        mVideoCameraScheduler.destroySurface();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        btnDeleteSegment.setSelected(false);
        btnDeleteSegment.setBackgroundResource(R.drawable.record_delete_video_bg_normal);

        if (styleFilterView.getVisibility() == View.VISIBLE) {
            styleFilterView.setVisibility(View.GONE);
            showOperationViews();
            btnRecord.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_btn_close) {
            onBackPressed();
        } else if (v.getId() == R.id.iv_btn_flashlight_switchover) {
            // 闪光灯按钮
            if (btnFlashlightSwitchCover.isSelected()) {
                btnFlashlightSwitchCover.setSelected(false);
                mVideoCameraScheduler.toggleFlashlight(true);
            } else {
                btnFlashlightSwitchCover.setSelected(true);
                mVideoCameraScheduler.toggleFlashlight(false);
            }
        } else if (v.getId() == R.id.iv_btn_camera_switchover) {
            // 切换相机按钮
            mIsFrontCamera = !mIsFrontCamera;
            mVideoCameraScheduler.switchCamera(mIsFrontCamera);
        } else if (v.getId() == R.id.iv_btn_record_speed) {
            // 录制速度按钮
            if (btnSpeedSwitchCover.isSelected()) {
                btnSpeedSwitchCover.setSelected(false);
            } else {
                btnSpeedSwitchCover.setSelected(true);
            }
        } else if (v.getId() == R.id.iv_btn_record_beauty) {
            // 美颜按钮
            if (btnFaceBeauty.isSelected()) {
                btnFaceBeauty.setSelected(false);
            } else {
                btnFaceBeauty.setSelected(true);
            }
        } else if (v.getId() == R.id.iv_btn_record_filter) {
            // 滤镜按钮
            hiddenOperationViews();
            btnRecord.setVisibility(View.GONE);
            styleFilterView.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btn_video_segment_delete) {
            if (mRecVideoInfo.getSegmentsSize() <= 0) {
                return;
            }
            // 删除片段按钮
            if (btnDeleteSegment.isSelected()) {
                deleteLastSegment();
                btnDeleteSegment.setSelected(false);
                btnDeleteSegment.setBackgroundResource(R.drawable.record_delete_video_bg_normal);
            } else {
                btnDeleteSegment.setSelected(true);
                btnDeleteSegment.setBackgroundResource(R.drawable.record_delete_video_bg_sure);
            }
        } else if (v.getId() == R.id.btn_video_record_done) {
            // 结束录制按钮
            if (mCurRecordProgress <= mMinRecordProgress) {
                Toast toast = Toast.makeText(getContext(), null, Toast.LENGTH_SHORT);
                toast.setText(R.string.lib_video_editor_record_too_short);// 目的是为了去掉小米手机强制加的应用名
                toast.show();
            } else {
                handleRecordDone();
            }
        }
    }

    @Override
    public void onTimeUpdate() {
        mCurRecordProgress += TIME_UPDATE_INTERVAL;
        if (mCurRecordProgress >= mMaxRecordProgress) {
            mCurRecordProgress = mMaxRecordProgress;
            btnRecord.setRecordStatus(AWRecordButton.Status.IDLE);
            pauseRecord();
        }
        mSegmentProgressBar.setProgress(mCurRecordProgress);
    }

    public void onBackPressed() {
        if (mRecVideoInfo.getSegmentsSize() > 0) {
            if (mExitConfirmDialog == null) {
                mExitConfirmDialog = createExitConfirmDialog();
            }
            mExitConfirmDialog.show();
            return;
        }
        exitRecordPage();
    }

    @Override
    public void onDestroy() {
        if (mExitConfirmDialog != null && mExitConfirmDialog.isShowing()) {
            mExitConfirmDialog.dismiss();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mVideoCameraScheduler.finishRecord();
        mVideoCameraScheduler.release();
        mRecordTimer.stop();
        super.onDestroy();
    }

    public void setCameraRecordFragmentListener(CameraRecordFragmentListener fragmentListener) {
        this.mFragmentLister = fragmentListener;
    }

    private void pauseRecord() {
        mVideoCameraScheduler.stopRecord();
        mRecordTimer.stop();
        mSegmentProgressBar.finishASegment();
        showOperationViews();
    }

    /**
     * 处理录制完成
     */
    private void handleRecordDone() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AWLoadingDialog(getContext(), true);
            mLoadingDialog.setCancelable(true);
        }
        mLoadingDialog.show();
        mRecVideoInfo.setDuration(mCurRecordProgress);

        //拼接各视频片段
        List<String> videoList = new ArrayList<>();
        for (AWSegmentInfo segmentInfo: mRecVideoInfo.getSegmentList()) {
            videoList.add(segmentInfo.getFilePath());
        }
        File mergedFile = new File(mVideoSaveDir, AWMediaConstants.MERGED_OUT_VIDEO_NAME);
        try {
            AWMp4ParserHelper.mergeVideos(videoList, mergedFile.getAbsolutePath());
            mRecVideoInfo.setMergedPath(mergedFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ALog.e("" + mRecVideoInfo);
        mLoadingDialog.dismiss();

        AWVideoPreviewActivity.launchVideoPreviewActivity(getContext(), mRecVideoInfo.getMergedPath());
    }

    /**
     * 删除上一个片段
     */
    private void deleteLastSegment() {
        mSegmentProgressBar.deleteLastSegment();
        mCurRecordProgress = mLastSegmentInfo.getStartTimeMs();
        mRecVideoInfo.deleteLastSegment();

        btnRecordDone.setAlpha(mCurRecordProgress > mMinRecordProgress ? 1.0f : 0.5f);
        if (mRecVideoInfo.getSegmentsSize() <= 0) {
            btnDeleteSegment.setVisibility(View.GONE);
            btnRecordDone.setVisibility(View.GONE);
        }
    }

    /**
     * 录制过程中隐藏操作按钮
     */
    private void hiddenOperationViews() {
        rlTopOperation.setVisibility(View.GONE);
        btnSpeedSwitchCover.setVisibility(View.GONE);
        btnFaceBeauty.setVisibility(View.GONE);
        btnStyleFilter.setVisibility(View.GONE);
        btnDeleteSegment.setVisibility(View.GONE);
        btnRecordDone.setVisibility(View.GONE);
    }

    /**
     * 录制暂停时显示操作控件
     */
    private void showOperationViews() {
        rlTopOperation.setVisibility(View.VISIBLE);
        btnSpeedSwitchCover.setVisibility(View.VISIBLE);
        btnFaceBeauty.setVisibility(View.VISIBLE);
        btnStyleFilter.setVisibility(View.VISIBLE);
        btnDeleteSegment.setVisibility(View.VISIBLE);

        btnRecordDone.setVisibility(View.VISIBLE);
        btnRecordDone.setAlpha(mCurRecordProgress > mMinRecordProgress ? 1.0f : 0.5f);
    }

    /**
     * 退出录制
     */
    private void exitRecordPage() {
        mRecVideoInfo.deleteAllFiles();
        if (mFragmentLister != null) {
            mFragmentLister.closeCameraRecordFragment();
        }
    }

    private AlertDialog createExitConfirmDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.lib_video_editor_record_exit_msg)
                .setNegativeButton(R.string.lib_video_editor_cancel, null)
                .setPositiveButton(R.string.lib_video_editor_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitRecordPage();
                    }
                }).create();
        return alertDialog;
    }
}
