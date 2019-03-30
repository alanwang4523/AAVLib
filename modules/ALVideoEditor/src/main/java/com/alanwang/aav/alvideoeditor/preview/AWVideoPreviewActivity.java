package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aavlib.libvideo.core.AWVideoPlayController;
import com.alanwang.aavlib.libvideo.surface.AWSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

/**
 * Author: AlanWang4523.
 * Date: 19/1/29 00:47.
 * Mail: alanwang4523@gmail.com
 */

public class AWVideoPreviewActivity extends AppCompatActivity implements ISurfaceCallback {

    private static final String VIDEO_PATH = "/sdcard/Alan/video/AlanTest.mp4";
    private EnhancedRelativeLayout mVideoLayout;
    private AWSurfaceView mAWSurfaceView;
    private AWVideoPlayController mVideoPlayController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_video_preview);

        mVideoLayout = findViewById(R.id.video_lyt);

        mAWSurfaceView = findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(this);

        mVideoPlayController = new AWVideoPlayController();
        mVideoPlayController.setControllerCallback(new AWVideoPlayController.IControllerCallback() {
            @Override
            public void onPlayReady(int width, int height, long duration) {
                mVideoLayout.setRatio(1.0f * height / width);
                mVideoPlayController.startPlay();
            }
        });
        mVideoPlayController.setVideoPath(VIDEO_PATH);

    }

    @Override
    public void onSurfaceChanged(Surface surface, int w, int h) {
        mVideoPlayController.updateSurface(surface, w, h);
    }

    @Override
    public void onSurfaceDestroyed(Surface surface) {
        mVideoPlayController.destroySurface();
    }

    @Override
    protected void onResume() {
        if (mVideoPlayController != null) {
            mVideoPlayController.startPlay();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mVideoPlayController != null) {
            mVideoPlayController.stopPlay();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mVideoPlayController.stopPlay();
        mVideoPlayController.release();
        super.onDestroy();
    }
}
