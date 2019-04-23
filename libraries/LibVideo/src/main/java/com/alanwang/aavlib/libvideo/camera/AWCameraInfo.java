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
