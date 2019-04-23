/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aav.algeneral.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.alanwang.aav.algeneral.R;

/**
 * Author: AlanWang4523.
 * Date: 19/4/14 19:42.
 * Mail: alanwang4523@gmail.com
 */
public class AWLoadingDialog extends Dialog {
    
    protected TextView tvHint;
    protected ProgressBar mLoadingPB;
    protected Context mContext;

    private String mHintText;
    private boolean isFullScreen;

    public AWLoadingDialog(Context context) {
        this(context, false);
    }

    public AWLoadingDialog(Context context, boolean isFullScreen) {
        super(context, R.style.dialog);
        this.isFullScreen = isFullScreen;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenStatus();
        setContentView(R.layout.aw_loading_dialog);
        mLoadingPB = findViewById(R.id.aw_loading_view);
        tvHint = findViewById(R.id.tv_loading_hint);

    }

    public void setLoadingText(String text) {
        mHintText = text;
    }

    private void setFullScreenStatus() {
        if (isFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (null != getWindow()) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus();  //需要注意页面会盖在通知栏上,各页面可以增加paddingTop 24dp解决
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus() {
        Window win = getWindow();
        if (null != win) {
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
    }

    @Override
    public void show() {
        if (mContext == null) {
            return;
        } else if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isDestroyed()) {
                return;
            }
        }
        try {
            super.show();
            if (!TextUtils.isEmpty(mHintText)) {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(mHintText);
            } else {
                tvHint.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (mContext == null) {
            return;
        } else if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isDestroyed()) {
                return;
            }
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
