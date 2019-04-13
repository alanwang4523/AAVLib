package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alanwang.aav.algeneral.common.AWTimer;
import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aav.algeneral.ui.AWSegmentProgressBar;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.AWRecVideoInfo;
import com.alanwang.aav.alvideoeditor.beans.AWSegmentInfo;
import com.alanwang.aavlib.libutils.ALog;
import com.alanwang.aavlib.libutils.TimeUtils;
import com.alanwang.aavlib.libvideo.core.AWVideoCameraScheduler;
import com.alanwang.aavlib.libvideo.surface.AWSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

import java.io.File;

/**
 * Author: AlanWang4523.
 * Date: 19/4/1 00:26.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraRecordActivity extends AppCompatActivity
        implements ISurfaceCallback, View.OnClickListener, AWTimer.TimerListener {

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

    private AWVideoCameraScheduler mVideoCameraScheduler;
    private boolean mIsFrontCamera = true;
    private File mVideoSaveDir = new File("/sdcard/Alan/record");
    private AWRecVideoInfo mRecVideoInfo = new AWRecVideoInfo();
    private AWSegmentInfo mLastSegmentInfo;

    private AWTimer mRecordTimer;
    private long mMinRecordProgress = 3 * 1000;
    private long mMaxRecordProgress = 15 * 1000;
    private long mCurRecordProgress = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_camera_record);

        if (!mVideoSaveDir.exists()) {
            mVideoSaveDir.mkdirs();
        }

        mRecVideoInfo.setCurrentDir(mVideoSaveDir.getAbsolutePath());
        mRecVideoInfo.setWidth(576);
        mRecVideoInfo.setHeight(1024);
        mRecVideoInfo.setBitrate(5 * 1024 * 1024);

        mVideoCameraScheduler = new AWVideoCameraScheduler();
        mVideoCameraScheduler.setupRecord(mRecVideoInfo.getWidth(), mRecVideoInfo.getHeight(), mRecVideoInfo.getBitrate());

        mVideoLayout = findViewById(R.id.video_lyt);

        mAWSurfaceView = findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(this);

        mSegmentProgressBar = findViewById(R.id.spb_record_progress);
        mSegmentProgressBar.setMinProgress(mMinRecordProgress);
        mSegmentProgressBar.setMaxProgress(mMaxRecordProgress);


        rlTopOperation = findViewById(R.id.rl_top_options);
        btnClose = findViewById(R.id.iv_btn_close);
        btnClose.setOnClickListener(this);

        btnFlashlightSwitchCover = findViewById(R.id.iv_btn_flashlight_switchover);
        btnFlashlightSwitchCover.setOnClickListener(this);
        btnFlashlightSwitchCover.setSelected(true);

        btnCameraSwitchCover = findViewById(R.id.iv_btn_camera_switchover);
        btnCameraSwitchCover.setOnClickListener(this);

        btnSpeedSwitchCover = findViewById(R.id.iv_btn_record_speed);
        btnSpeedSwitchCover.setOnClickListener(this);

        btnFaceBeauty = findViewById(R.id.iv_btn_record_beauty);
        btnFaceBeauty.setOnClickListener(this);

        btnStyleFilter = findViewById(R.id.iv_btn_record_filter);
        btnStyleFilter.setOnClickListener(this);

        btnRecord = findViewById(R.id.btn_camera_record);
        btnRecord.setRecordListener(new AWRecordButton.OnRecordListener() {
            @Override
            public void onRecordStart() {
                mLastSegmentInfo = new AWSegmentInfo();
                File file = new File(mVideoSaveDir, "AW_video_" + TimeUtils.getCurrentTime() + ".mp4");
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
        
        btnDeleteSegment = findViewById(R.id.btn_video_segment_delete);
        btnDeleteSegment.setOnClickListener(this);
        
        btnRecordDone = findViewById(R.id.btn_video_record_done);
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
    public void onClick(View v) {
        if (v.getId() == R.id.iv_btn_close) {
            finish();
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
            mRecVideoInfo.setDuration(mCurRecordProgress);
            ALog.e("" + mRecVideoInfo);
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

    @Override
    protected void onDestroy() {
        mVideoCameraScheduler.finishRecord();
        mVideoCameraScheduler.release();
        mRecordTimer.stop();
        super.onDestroy();
    }

    private void pauseRecord() {
        mVideoCameraScheduler.stopRecord();
        mRecordTimer.stop();
        mSegmentProgressBar.finishASegment();
        showOperationViews();
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
}
