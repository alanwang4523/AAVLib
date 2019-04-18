package com.alanwang.aav.alvideoeditor.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alanwang.aav.algeneral.ui.AWBaseAdapter;
import com.alanwang.aav.alvideoeditor.R;
import com.alanwang.aav.alvideoeditor.beans.EffectBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Author: AlanWang4523.
 * Date: 19/4/18 09:02.
 * Mail: alanwang4523@gmail.com
 */
public class StyleEffectAdapter extends AWBaseAdapter<EffectBean> {

    private int selectIndex = 0;
    private EffectSelectListener effectSelectListener;
    private RequestOptions glideOptions = new RequestOptions()
            .centerCrop().placeholder(R.drawable.ic_launcher);

    public StyleEffectAdapter(Context mContext) {
        super(mContext);
    }

    public void setEffectSelectListener(EffectSelectListener effectSelectListener) {
        this.effectSelectListener = effectSelectListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aav_recycler_view_layout, parent, false);
        return new StyleEffectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final StyleEffectViewHolder viewHolder = (StyleEffectViewHolder) holder;
        final EffectBean item = getItemList().get(position);

        viewHolder.tvName.setText(item.name);
        Glide.with(mContext).load(item.iconId).apply(glideOptions).into(viewHolder.ivIcon);

        boolean isSelect = selectIndex == position;
        if (isSelect) {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorTextSelect));
            viewHolder.ivIcon.setBackgroundResource(R.drawable.image_border);
        } else {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorTextDefault));
            viewHolder.ivIcon.setBackgroundResource(android.R.color.transparent);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIndex = position;
                if (effectSelectListener != null) {
                    effectSelectListener.onEffectSelect(item.effectId);
                }
                notifyDataSetChanged();
            }
        });
    }

    private class StyleEffectViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;

        public StyleEffectViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon_effect_item);
            tvName = itemView.findViewById(R.id.tv_name_effect_item);
        }
    }
}
