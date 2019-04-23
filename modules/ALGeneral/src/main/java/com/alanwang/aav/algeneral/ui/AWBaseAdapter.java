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
package com.alanwang.aav.algeneral.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/1/29 00:37.
 * Mail: alanwang4523@gmail.com
 */

public abstract class AWBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<T> mItemList = new ArrayList<>();

    public AWBaseAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取数据列表
     * @return
     */
    public List<T> getItemList() {
        return mItemList;
    }

    /**
     * 设置数据列表
     * @param itemList
     */
    public void setItemList(List<T> itemList) {
        if (itemList != null){
            mItemList = itemList;
            notifyDataSetChanged();
        }
    }

    /**
     * 清空数据列表
     */
    public void clearItemList() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取某个位置的数据
     * @param position
     * @return
     */
    public T getItemAt(int position) {
        if (null == mItemList || mItemList.isEmpty() || position >= mItemList.size() || position < 0) {
            return null;
        }
        return mItemList.get(position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
