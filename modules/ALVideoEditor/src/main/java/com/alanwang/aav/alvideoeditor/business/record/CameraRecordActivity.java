package com.alanwang.aav.alvideoeditor.business.record;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.alanwang.aav.alvideoeditor.R;

/**
 * Author: AlanWang4523.
 * Date: 19/4/17 07:56.
 * Mail: alanwang4523@gmail.com
 */
public class CameraRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_common_fragment_activity);

        CameraRecordFragment recordFragment = CameraRecordFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frag_container, recordFragment);
        transaction.commitAllowingStateLoss();
    }
}
