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
package com.alanwang.aavlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Author: AlanWang4523.
 * Date: 19/3/13 01:13.
 * Mail: alanwang4523@gmail.com
 */
public class TestLibAudioActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_libaudio);

        TextView btn_test_record_to_wav = findViewById(R.id.btn_libaudio_record_to_wav);
        btn_test_record_to_wav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_libaudio_record_to_wav:
                testRecordAudioToWav();
                break;
            default:
        }
    }

    /**
     * 测试录制音频到 wav
     */
    private void testRecordAudioToWav() {
        startActivity(new Intent(this, TestAudioRecordActivity.class));
    }
}
