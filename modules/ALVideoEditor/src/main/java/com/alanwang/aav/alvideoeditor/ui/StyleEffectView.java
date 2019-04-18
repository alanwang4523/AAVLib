package com.alanwang.aav.alvideoeditor.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.EffectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/18 08:49.
 * Mail: alanwang4523@gmail.com
 */
public class StyleEffectView extends RelativeLayout implements EffectSelectListener {

    private StyleEffectAdapter effectAdapter;

    public StyleEffectView(Context context) {
        super(context);
        initView();
    }

    public StyleEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StyleEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.aav_recycler_view_layout, this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        effectAdapter = new StyleEffectAdapter(getContext());
        effectAdapter.setEffectSelectListener(this);
        effectAdapter.setItemList(getEffectList());

        recyclerView.setAdapter(effectAdapter);
    }

    @Override
    public void onEffectSelect(int type) {

    }

    private List<EffectBean> getEffectList() {
        List<EffectBean> effectList = new ArrayList<>();

        return effectList;
    }
}
