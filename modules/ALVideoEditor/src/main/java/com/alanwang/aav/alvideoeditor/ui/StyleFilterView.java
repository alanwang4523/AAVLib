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
package com.alanwang.aav.alvideoeditor.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.common.FiltersHelper;

/**
 * Author: AlanWang4523.
 * Date: 19/4/18 08:49.
 * Mail: alanwang4523@gmail.com
 */
public class StyleFilterView extends RelativeLayout {

    private StyleFilterAdapter effectAdapter;

    public StyleFilterView(Context context) {
        super(context);
        initView();
    }

    public StyleFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StyleFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.aav_recycler_view_layout, this);

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getContext().getText(R.string.lib_video_editor_video_effect_title_style));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        effectAdapter = new StyleFilterAdapter(getContext());
        effectAdapter.setItemList(FiltersHelper.getStyleFilterList());

        recyclerView.setAdapter(effectAdapter);
    }

    public void setEffectSelectListener(EffectSelectListener effectSelectListener) {
        effectAdapter.setEffectSelectListener(effectSelectListener);
    }
}
