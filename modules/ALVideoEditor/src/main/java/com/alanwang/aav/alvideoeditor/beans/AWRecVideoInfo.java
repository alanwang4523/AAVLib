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
package com.alanwang.aav.alvideoeditor.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.alanwang.aavlib.libutils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/13 15:01.
 * Mail: alanwang4523@gmail.com
 */
public class AWRecVideoInfo implements Parcelable {
    /**
     * 当前目录
     */
    private String currentDir;

    /**
     * 多个视频拼接合并后的路径
     */
    private String mergedPath;

    /**
     * 最终输出的文件路径
     */
    private String finalOutputPath;

    /**
     * 视频宽度
     */
    private int width;

    /**
     * 视频高度
     */
    private int height;

    /**
     * 视频编码码率
     */
    private int bitrate;

    /**
     * 视频的总时长
     */
    private long duration;

    /**
     * 片段列表
     */
    private List<AWSegmentInfo> segmentList;

    public AWRecVideoInfo() {
        segmentList = new ArrayList<AWSegmentInfo>();
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public String getMergedPath() {
        return mergedPath;
    }

    public void setMergedPath(String mergedPath) {
        this.mergedPath = mergedPath;
    }

    public String getFinalOutputPath() {
        return finalOutputPath;
    }

    public void setFinalOutputPath(String finalOutputPath) {
        this.finalOutputPath = finalOutputPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<AWSegmentInfo> getSegmentList() {
        return segmentList;
    }

    public int getSegmentsSize() {
        return segmentList.size();
    }

    /**
     * 添加一个片段
     * @param segmentInfo
     */
    public void addSegment(AWSegmentInfo segmentInfo) {
        segmentList.add(segmentInfo);
    }

    /**
     * 删除上一个片段
     */
    public void deleteLastSegment() {
        AWSegmentInfo lastSegment = segmentList.get(getSegmentsSize() - 1);
        File file = new File(lastSegment.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        segmentList.remove(lastSegment);
    }

    /**
     * 删除所有片段
     */
    public void deleteAllSegments() {
        for (AWSegmentInfo segment: segmentList) {
            File file = new File(segment.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        segmentList.clear();
    }

    /**
     * 删除当前录制目录下的所有文件
     */
    public void deleteAllFiles() {
        segmentList.clear();
        File curDir = new File(currentDir);
        FileUtils.deleteFile(curDir);
    }

    protected AWRecVideoInfo(Parcel in) {
        currentDir = in.readString();
        mergedPath = in.readString();
        finalOutputPath = in.readString();
        width = in.readInt();
        height = in.readInt();
        bitrate = in.readInt();
        duration = in.readLong();
        segmentList = in.createTypedArrayList(AWSegmentInfo.CREATOR);
    }

    public static final Creator<AWRecVideoInfo> CREATOR = new Creator<AWRecVideoInfo>() {
        @Override
        public AWRecVideoInfo createFromParcel(Parcel in) {
            return new AWRecVideoInfo(in);
        }

        @Override
        public AWRecVideoInfo[] newArray(int size) {
            return new AWRecVideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentDir);
        dest.writeString(mergedPath);
        dest.writeString(finalOutputPath);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(bitrate);
        dest.writeLong(duration);
        dest.writeTypedList(segmentList);
    }

    @Override
    public String toString() {
        return "AWRecVideoInfo{" +
                "currentDir='" + currentDir + '\'' +
                ", mergedPath='" + mergedPath + '\'' +
                ", finalOutputPath='" + finalOutputPath + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", bitrate=" + bitrate +
                ", duration=" + duration +
                ", segmentList=" + segmentList +
                '}';
    }
}
