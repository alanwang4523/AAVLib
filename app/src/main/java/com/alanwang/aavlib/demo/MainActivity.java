/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.demo;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alanwang.aav.algeneral.utils.RuntimePermissionsManager;
import com.alanwang.aav.alvideoeditor.business.preview.AWVideoPreviewActivity;
import com.alanwang.aav.alvideoeditor.business.record.CameraRecordActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RuntimePermissionsManager runtimePermissionsHelper = new RuntimePermissionsManager(this,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!runtimePermissionsHelper.isAllPermissionsGranted()) {
            runtimePermissionsHelper.makeRequest();
        }

        TextView btn_goto_video_editor = findViewById(R.id.btn_goto_video_editor);
        btn_goto_video_editor.setOnClickListener(this);
        TextView btn_goto_camera_record = findViewById(R.id.btn_goto_camera_record);
        btn_goto_camera_record.setOnClickListener(this);
        TextView btn_goto_test_libmediacore = findViewById(R.id.btn_goto_test_libmediacore);
        btn_goto_test_libmediacore.setOnClickListener(this);
        TextView btn_goto_test_libaudio = findViewById(R.id.btn_goto_test_libaudio);
        btn_goto_test_libaudio.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto_camera_record:
                startActivity(new Intent(MainActivity.this, CameraRecordActivity.class));
                break;
            case R.id.btn_goto_video_editor:
                String VIDEO_PATH = "/sdcard/Alan/video/AlanTest.mp4";
                AWVideoPreviewActivity.launchVideoPreviewActivity(MainActivity.this, VIDEO_PATH);
                break;
            case R.id.btn_goto_test_libmediacore:
                startActivity(new Intent(MainActivity.this, TestLibMediaCoreActivity.class));
                break;
            case R.id.btn_goto_test_libaudio:
                startActivity(new Intent(MainActivity.this, TestLibAudioActivity.class));
                break;
            default:
        }
    }
}
