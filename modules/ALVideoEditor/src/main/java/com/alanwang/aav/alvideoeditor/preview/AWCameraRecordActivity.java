package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.alanwang.aav.algeneral.common.AWTimer;
import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aav.algeneral.ui.AWSegmentProgressBar;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
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
    private AWRecordButton btnRecord;
    private ImageView btnClose;
    private ImageView btnCameraSwitchCover;
    private ImageView btnFlashlightSwitchCover;
    private ImageView btnSpeedSwitchCover;
    private ImageView btnFaceBeauty;
    private ImageView btnStyleFilter;

    private AWVideoCameraScheduler mVideoCameraScheduler;
    private boolean mIsFrontCamera = true;
    private File mVideoSaveDir = new File("/sdcard/Alan/record");
    private File mCurVideoFile;

    private AWTimer mRecordTimer;
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

        mVideoCameraScheduler = new AWVideoCameraScheduler();
        mVideoCameraScheduler.setupRecord(576, 1024, 5 * 1024 * 1024);
        if (!mVideoSaveDir.exists()) {
            mVideoSaveDir.mkdirs();
        }

        mVideoLayout = findViewById(R.id.video_lyt);

        mAWSurfaceView = findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(this);

        mSegmentProgressBar = findViewById(R.id.spb_record_progress);
        mSegmentProgressBar.setMinProgress(3 * 1000);
        mSegmentProgressBar.setMaxProgress(mMaxRecordProgress);

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
                mCurVideoFile = new File(mVideoSaveDir, "AW_video_" + TimeUtils.getCurrentTime() + ".mp4");
                mVideoCameraScheduler.startRecord(mCurVideoFile.getAbsolutePath());
                mRecordTimer.start();
            }

            @Override
            public void onRecordStop() {
//                Toast.makeText(AWCameraRecordActivity.this, "" + mCurVideoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                AWVideoPreviewActivity.launchVideoPreviewActivity(AWCameraRecordActivity.this, mCurVideoFile.getAbsolutePath());
                pauseRecord();
            }
        });

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
            if (btnFlashlightSwitchCover.isSelected()) {
                btnFlashlightSwitchCover.setSelected(false);
                mVideoCameraScheduler.toggleFlashlight(true);
            } else {
                btnFlashlightSwitchCover.setSelected(true);
                mVideoCameraScheduler.toggleFlashlight(false);
            }
        } else if (v.getId() == R.id.iv_btn_camera_switchover) {
            mIsFrontCamera = !mIsFrontCamera;
            mVideoCameraScheduler.switchCamera(mIsFrontCamera);
        } else if (v.getId() == R.id.iv_btn_record_speed) {
            if (btnSpeedSwitchCover.isSelected()) {
                btnSpeedSwitchCover.setSelected(false);
            } else {
                btnSpeedSwitchCover.setSelected(true);
            }
        } else if (v.getId() == R.id.iv_btn_record_beauty) {
            if (btnFaceBeauty.isSelected()) {
                btnFaceBeauty.setSelected(false);
            } else {
                btnFaceBeauty.setSelected(true);
            }
        } else if (v.getId() == R.id.iv_btn_record_filter) {

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
    }
}
