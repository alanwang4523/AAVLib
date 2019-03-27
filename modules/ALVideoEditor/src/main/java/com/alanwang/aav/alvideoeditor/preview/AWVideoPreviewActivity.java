package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aavlib.libeglcore.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.libvideo.player.AWVideoPlayer;
import com.alanwang.aavlib.libvideo.player.IVideoPlayer;
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
    private IVideoPlayer mVideoPlayer;
    private AWIOSurfaceProxy mIOSurfaceProxy;

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

        mIOSurfaceProxy = new AWIOSurfaceProxy();
        mVideoPlayer = new AWVideoPlayer();

//        mIOSurfaceProxy.setOnInputSurfaceReadyListener(new AWIOSurfaceProxy.OnInputSurfaceReadyListener() {
//            @Override
//            public void onInputSurfaceReady(Surface surface) {
//                mVideoPlayer.setSurface(surface);
//            }
//        });

        mVideoPlayer.setSurface(mIOSurfaceProxy.getInputSurface());
        mVideoPlayer.setOnPlayReadyListener(new IVideoPlayer.OnPlayReadyListener() {
            @Override
            public void onPlayReady(int width, int height) {
                mVideoLayout.setRatio(1.0f * height / width);
                mIOSurfaceProxy.setTextureSize(width, height);
                mVideoPlayer.start();
            }
        });
        mVideoPlayer.preparePlayer(VIDEO_PATH);
    }

    @Override
    public void onSurfaceChanged(Surface surface, int w, int h) {
        mIOSurfaceProxy.updateSurface(surface, w, h);
    }

    @Override
    public void onSurfaceDestroyed(Surface surface) {
        mIOSurfaceProxy.destroySurface();
    }

    @Override
    protected void onResume() {
        if (mVideoPlayer != null) {
            mVideoPlayer.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mVideoPlayer != null) {
            mVideoPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mVideoPlayer.stop();
        mIOSurfaceProxy.release();
        super.onDestroy();
    }
}
