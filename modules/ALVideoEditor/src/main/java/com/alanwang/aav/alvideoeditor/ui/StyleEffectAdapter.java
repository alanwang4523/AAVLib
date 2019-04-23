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

import de.hdodenhof.circleimageview.CircleImageView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aav_item_effect_tray, parent, false);
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
            viewHolder.ivIcon.setBorderColor(mContext.getResources().getColor(R.color.colorTextSelect));
            viewHolder.ivIcon.setBorderWidth(3);
        } else {
            viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorTextDefault));
            viewHolder.ivIcon.setBorderColor(mContext.getResources().getColor(R.color.transparent));
            viewHolder.ivIcon.setBorderWidth(0);
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
        public CircleImageView ivIcon;
        public TextView tvName;

        public StyleEffectViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon_effect_item);
            tvName = itemView.findViewById(R.id.tv_name_effect_item);
        }
    }
}
