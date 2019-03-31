package com.alanwang.aavlib.libvideo.camera;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 21:45.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraInfo {
    private int facingId;
    private int previewWidth;
    private int previewHeight;
    private int rotateDegree;

    public AWCameraInfo(int facingId, int previewWidth, int previewHeight, int rotateDegree) {
        this.facingId = facingId;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.rotateDegree = rotateDegree;
    }

    public int getFacingId() {
        return facingId;
    }

    public void setFacingId(int facingId) {
        this.facingId = facingId;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public int getRotateDegree() {
        return rotateDegree;
    }

    public void setRotateDegree(int rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    @Override
    public String toString() {
        return "AWCameraInfo{" +
                "facingId=" + facingId +
                ", previewWidth=" + previewWidth +
                ", previewHeight=" + previewHeight +
                ", rotateDegree=" + rotateDegree +
                '}';
    }
}
