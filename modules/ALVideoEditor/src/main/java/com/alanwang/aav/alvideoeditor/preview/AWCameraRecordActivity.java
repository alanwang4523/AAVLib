package com.alanwang.aav.alvideoeditor.preview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import com.alanwang.aav.algeneral.ui.AWRecordButton;
import com.alanwang.aav.algeneral.ui.EnhancedRelativeLayout;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aavlib.libvideo.surface.AWSurfaceView;
import com.alanwang.aavlib.libvideo.surface.ISurfaceCallback;

/**
 * Author: AlanWang4523.
 * Date: 19/4/1 00:26.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraRecordActivity extends AppCompatActivity implements ISurfaceCallback {

    private EnhancedRelativeLayout mVideoLayout;
    private AWSurfaceView mAWSurfaceView;
    private AWRecordButton btnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_activity_camera_record);

        mVideoLayout = findViewById(R.id.video_lyt);

        mAWSurfaceView = findViewById(R.id.video_surface_view);
        mAWSurfaceView.setSurfaceCallback(this);

        btnRecord = findViewById(R.id.btn_camera_record);
        btnRecord.setRecordListener(new AWRecordButton.OnRecordListener() {
            @Override
            public void onRecordStart() {
            }

            @Override
            public void onRecordStop() {
            }
        });
    }


    @Override
    public void onSurfaceChanged(Surface surface, int w, int h) {

    }

    @Override
    public void onSurfaceDestroyed(Surface surface) {

    }
}
