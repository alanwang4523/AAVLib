package com.alanwang.aav.alvideoeditor.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.EffectBean;
import com.alanwang.aav.alvideoeditor.common.EffectTypes;
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

        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getContext().getText(R.string.lib_video_editor_video_effect_title_style));

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

        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_ORIGINAL,
                getText(R.string.lib_video_editor_video_effect_name_original),
                R.drawable.icon_style_original));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_ROMANTIC,
                getText(R.string.lib_video_editor_video_effect_name_romantic),
                R.drawable.icon_style_romantic));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_FRESH,
                getText(R.string.lib_video_editor_video_effect_name_fresh),
                R.drawable.icon_style_fresh));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_BEAUTIFUL,
                getText(R.string.lib_video_editor_video_effect_name_beautiful),
                R.drawable.icon_style_beautiful));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_PINK,
                getText(R.string.lib_video_editor_video_effect_name_pink),
                R.drawable.icon_style_pink));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_REMINISCENCE,
                getText(R.string.lib_video_editor_video_effect_name_reminiscence),
                R.drawable.icon_style_reminiscence));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_BLUES,
                getText(R.string.lib_video_editor_video_effect_name_blues),
                R.drawable.icon_style_blues));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_COOL,
                getText(R.string.lib_video_editor_video_effect_name_cool),
                R.drawable.icon_style_cool));
        effectList.add(new EffectBean(
                EffectTypes.STYLE_TYPE_JAPANESE,
                getText(R.string.lib_video_editor_video_effect_name_japanese),
                R.drawable.icon_style_japanese));
        return effectList;
    }

    private String getText(int textId) {
        return getContext().getResources().getString(textId);
    }
}
