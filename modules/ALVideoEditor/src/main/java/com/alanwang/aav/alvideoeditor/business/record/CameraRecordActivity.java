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
public class CameraRecordActivity extends AppCompatActivity implements
        CameraRecordFragment.CameraRecordFragmentListener {

    private CameraRecordFragment recordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aav_common_fragment_activity);

        recordFragment = CameraRecordFragment.newInstance();
        recordFragment.setCameraRecordFragmentListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frag_container, recordFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        recordFragment.onBackPressed();
    }

    @Override
    public void closeCameraRecordFragment() {
        supportFinishAfterTransition();
    }
}
