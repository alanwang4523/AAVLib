package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aavlib.libvideo.surface.AAVSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

/**
 * Author: AlanWang4523.
 * Date: 19/1/29 00:47.
 * Mail: alanwang4523@gmail.com
 */

public class AAVVideoPreviewActivity extends AppCompatActivity implements ISurfaceCallback{

    private AAVSurfaceView mAAVSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_video_preview);

        mAAVSurfaceView = findViewById(R.id.video_surface_view);
        mAAVSurfaceView.setSurfaceCallback(this);
    }

    @Override
    public void onSurfaceChanged(Object surface, int w, int h) {
        
    }

    @Override
    public void onSurfaceDestroyed(Object surface) {

    }
}
