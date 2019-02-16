package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.core.AAVVideoPlayController;
import com.alanwang.aavlib.libvideo.surface.AAVSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

/**
 * Author: AlanWang4523.
 * Date: 19/1/29 00:47.
 * Mail: alanwang4523@gmail.com
 */

public class AAVVideoPreviewActivity extends AppCompatActivity implements ISurfaceCallback, AAVVideoPlayController.IControllerCallback {

    private static final String VIDEO_PATH = "/sdcard/Alan/video/huahua.mp4";
    private EnhancedRelativeLayout mVideoLayout;
    private AAVSurfaceView mAAVSurfaceView;
    private AAVVideoPlayController mVideoPlayController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_video_preview);

        mVideoLayout = findViewById(R.id.video_lyt);

        mAAVSurfaceView = findViewById(R.id.video_surface_view);
        mAAVSurfaceView.setSurfaceCallback(this);

        mVideoPlayController = new AAVVideoPlayController();
        mVideoPlayController.setControllerCallback(this);
        mVideoPlayController.setVideoPath(VIDEO_PATH);
    }

    @Override
    public void onSurfaceChanged(Object surface, int w, int h) {
        mVideoPlayController.updateSurface((Surface) surface, w, h);
    }

    @Override
    public void onSurfaceDestroyed(Object surface) {
        mVideoPlayController.destroySurface();
    }

    @Override
    public void onPlayReady(int width, int height, long duration) {
        mVideoLayout.setRatio(1.0f * height / width);
    }

    @Override
    protected void onDestroy() {
        mVideoPlayController.release();
        super.onDestroy();
    }
}
