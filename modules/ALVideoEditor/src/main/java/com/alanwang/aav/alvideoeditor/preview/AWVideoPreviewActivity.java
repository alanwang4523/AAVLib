package com.alanwang.aav.alvideoeditor.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aavlib.libutils.ALog;
import com.alanwang.aavlib.libvideo.core.AWVideoPlayController;
import com.alanwang.aavlib.libvideo.surface.AWSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

import java.io.File;

/**
 * Author: AlanWang4523.
 * Date: 19/1/29 00:47.
 * Mail: alanwang4523@gmail.com
 */

public class AWVideoPreviewActivity extends AppCompatActivity {
    private static final String KEY_VIDEO_PATH = "video_path";

    private String mVideoPath;
    private EnhancedRelativeLayout mVideoLayout;
    private AWSurfaceView mAWSurfaceView;
    private AWVideoPlayController mVideoPlayController;

    public static void launchVideoPreviewActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, AWVideoPreviewActivity.class);
        intent.putExtra(KEY_VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_video_preview);

        mVideoPath = getIntent().getStringExtra(KEY_VIDEO_PATH);
        ALog.e("mVideoPath = " + mVideoPath);
        if (TextUtils.isEmpty(mVideoPath)) {
            Toast.makeText(this, "Video file path is null!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File videoFile = new File(mVideoPath);
        if (!videoFile.exists()) {
            Toast.makeText(this, "Video file not exist!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mVideoLayout = findViewById(R.id.video_lyt);

        mAWSurfaceView = findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(mSurfaceCallback);

        mVideoPlayController = new AWVideoPlayController();
        mVideoPlayController.setControllerCallback(new AWVideoPlayController.IControllerCallback() {
            @Override
            public void onPlayReady(int width, int height, long duration) {
                mVideoLayout.setRatio(1.0f * height / width);
                mVideoPlayController.startPlay();
            }
        });
        mVideoPlayController.setVideoPath(mVideoPath);

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

    private ISurfaceCallback mSurfaceCallback = new ISurfaceCallback() {
        @Override
        public void onSurfaceChanged(Surface surface, int w, int h) {
            mVideoPlayController.updateSurface(surface, w, h);
        }

        @Override
        public void onSurfaceDestroyed(Surface surface) {
            mVideoPlayController.destroySurface();
        }
    };
}
