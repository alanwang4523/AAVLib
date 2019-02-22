package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Author: AlanWang4523.
 * Date: 19/2/22 01:59.
 * Mail: alanwang4523@gmail.com
 */
public class TestLibMediaCoreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_libmediacore);

        TextView btn_libmediacore_test_cliper = findViewById(R.id.btn_libmediacore_test_cliper);
        btn_libmediacore_test_cliper.setOnClickListener(this);
        TextView btn_libmediacore_test_muxer = findViewById(R.id.btn_libmediacore_test_muxer);
        btn_libmediacore_test_muxer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_libmediacore_test_cliper:
                testClipper();
                break;
            case R.id.btn_libmediacore_test_muxer:
                testMuxer();
                break;
            default:
        }
    }

    /**
     * 测试音视频裁剪
     */
    private void testClipper() {

    }

    /**
     * 测试音视频合成
     */
    private void testMuxer() {

    }
}
