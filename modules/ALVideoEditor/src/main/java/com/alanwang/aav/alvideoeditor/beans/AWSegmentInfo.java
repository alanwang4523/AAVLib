package com.alanwang.aav.alvideoeditor.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: AlanWang4523.
 * Date: 19/4/13 14:39.
 * Mail: alanwang4523@gmail.com
 */
public class AWSegmentInfo implements Parcelable {

    /**
     * 该片段的文件路径
     */
    private String filePath;

    /**
     * 片段的起始时间
     */
    private long startTimeMs;

    /**
     * 片段的截止时间
     */
    private long endTimeMs;

    /**
     * 效果 Id
     * 如果是视频则为：风格滤镜(LUT) Id
     * 如果是音频则为：音效 Id
     */
    private int effectId;

    /**
     * 变速录制的录制速度
     */
    private int speedType;

    public AWSegmentInfo() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    public void setStartTimeMs(long startTimeMs) {
        this.startTimeMs = startTimeMs;
    }

    public long getEndTimeMs() {
        return endTimeMs;
    }

    public void setEndTimeMs(long endTimeMs) {
        this.endTimeMs = endTimeMs;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public int getSpeedType() {
        return speedType;
    }

    public void setSpeedType(int speedType) {
        this.speedType = speedType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected AWSegmentInfo(Parcel in) {
        filePath = in.readString();
        startTimeMs = in.readLong();
        endTimeMs = in.readLong();
        effectId = in.readInt();
        speedType = in.readInt();
    }

    public static final Creator<AWSegmentInfo> CREATOR = new Creator<AWSegmentInfo>() {
        @Override
        public AWSegmentInfo createFromParcel(Parcel in) {
            return new AWSegmentInfo(in);
        }

        @Override
        public AWSegmentInfo[] newArray(int size) {
            return new AWSegmentInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeLong(startTimeMs);
        dest.writeLong(endTimeMs);
        dest.writeInt(effectId);
        dest.writeInt(speedType);
    }

    @Override
    public String toString() {
        return "AWSegmentInfo{" +
                "filePath='" + filePath + '\'' +
                ", startTimeMs=" + startTimeMs +
                ", endTimeMs=" + endTimeMs +
                ", effectId=" + effectId +
                ", speedType=" + speedType +
                '}';
    }
}
