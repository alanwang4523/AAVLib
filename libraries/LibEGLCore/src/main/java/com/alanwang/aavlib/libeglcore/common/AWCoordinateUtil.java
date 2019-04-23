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
package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 23:54.
 * Mail: alanwang4523@gmail.com
 */

public class AWCoordinateUtil {

    public static final float DEFAULT_VERTEX_COORDS[] = {
            -1.0f, -1.0f,  // 0 bottom left
            1.0f, -1.0f,   // 1 bottom right
            -1.0f,  1.0f,  // 2 top left
            1.0f,  1.0f,   // 3 top right
    };

    public static final float[] DEFAULT_TEXTURE_COORDS = new float[]{
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    public static final float[] TEXTURE_NO_ROTATION = new float[]{
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };
    public static final float[] TEXTURE_ROTATION_90 = new float[]{
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };
    public static final float[] TEXTURE_ROTATION_180 = new float[]{
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };
    public static final float[] TEXTURE_ROTATION_270 = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    public static float[] getNormalTextureCoords() {
        return getTextureCoords(0, false, false);
    }

    public static float[] getVFlipTextureCoords() {
        return getTextureCoords(90, false, true);
    }

    public static float[] getTextureCoords(float[] arGTextureCoords, int angleDegree) {
        float[] textureCoords = arGTextureCoords.clone();

        int rotateCount = angleDegree / 90;
        for (int i = 0; i < rotateCount; i++) {
            rotateTextureCoords90(textureCoords, textureCoords);
        }
        return textureCoords;
    }

    public static void rotateTextureCoords90(float[] arcTextureCoords, float[] dstTextureCoords) {
        float[] srcTextureCoords = arcTextureCoords.clone();

        dstTextureCoords[0 * 2 + 0] = srcTextureCoords[1 * 2 + 0];
        dstTextureCoords[0 * 2 + 1] = srcTextureCoords[1 * 2 + 1];

        dstTextureCoords[1 * 2 + 0] = srcTextureCoords[3 * 2 + 0];
        dstTextureCoords[1 * 2 + 1] = srcTextureCoords[3 * 2 + 1];

        dstTextureCoords[2 * 2 + 0] = srcTextureCoords[0 * 2 + 0];
        dstTextureCoords[2 * 2 + 1] = srcTextureCoords[0 * 2 + 1];

        dstTextureCoords[3 * 2 + 0] = srcTextureCoords[2 * 2 + 0];
        dstTextureCoords[3 * 2 + 1] = srcTextureCoords[2 * 2 + 1];
    }

    public static float[] flipTextureCoords(float[] srcTextureCoordsint, int angleDegree, boolean isHFlip, boolean isVFlip) {
        float[] textureCoords = srcTextureCoordsint.clone();
        boolean isXCoordsFlip = isHFlip;
        boolean isYCoordsFlip = isVFlip;
        if (angleDegree == 90 || angleDegree == 270) {
            isXCoordsFlip = isVFlip;
            isYCoordsFlip = isHFlip;
        }

        if (isXCoordsFlip) {
            textureCoords[0] = flip2(textureCoords[0]);
            textureCoords[2] = flip2(textureCoords[2]);
            textureCoords[4] = flip2(textureCoords[4]);
            textureCoords[6] = flip2(textureCoords[6]);
        }
        if (isYCoordsFlip) {
            textureCoords[1] = flip2(textureCoords[1]);
            textureCoords[3] = flip2(textureCoords[3]);
            textureCoords[5] = flip2(textureCoords[5]);
            textureCoords[7] = flip2(textureCoords[7]);
        }

        return textureCoords;
    }

    /**
     * 获取纹理坐标
     * @param angleDegree 旋转角度：0， 90， 180， 270
     * @param isHFlip 是否做水平镜像
     * @param isVFlip 是否做垂直镜像
     * @return
     */
    public static float[] getTextureCoords(int angleDegree, boolean isHFlip, boolean isVFlip) {
        float[] textureCoords;

        // 如果旋转了 90 或 180 度，因为原始的 X/Y 坐标互换了，因此水平翻转需要翻转的是 Y 坐标，垂直翻转需要翻转的是 X 坐标
        boolean isXCoordsFlip;
        boolean isYCoordsFlip;
        switch (angleDegree) {
            case 90:
                textureCoords = TEXTURE_ROTATION_90.clone();
                isXCoordsFlip = isVFlip;
                isYCoordsFlip = isHFlip;
                break;
            case 180:
                textureCoords = TEXTURE_ROTATION_180.clone();
                isXCoordsFlip = isHFlip;
                isYCoordsFlip = isVFlip;
                break;
            case 270:
                textureCoords = TEXTURE_ROTATION_270.clone();
                isXCoordsFlip = isVFlip;
                isYCoordsFlip = isHFlip;
                break;
            default:
                textureCoords = TEXTURE_NO_ROTATION.clone();
                isXCoordsFlip = isHFlip;
                isYCoordsFlip = isVFlip;
                break;
        }
        if (isXCoordsFlip) {
            textureCoords[0] = flip(textureCoords[0]);
            textureCoords[2] = flip(textureCoords[2]);
            textureCoords[4] = flip(textureCoords[4]);
            textureCoords[6] = flip(textureCoords[6]);
        }
        if (isYCoordsFlip) {
            textureCoords[1] = flip(textureCoords[1]);
            textureCoords[3] = flip(textureCoords[3]);
            textureCoords[5] = flip(textureCoords[5]);
            textureCoords[7] = flip(textureCoords[7]);
        }
        return textureCoords;
    }

    private static float flip(float f) {
        return f == 0.0f ? 1.0f : 0.0f;
    }

    private static float flip2(float f) {
        return 1.0f - f;
    }

    /**
     * 获取 CenterCrop 模式下的纹理坐标
     * @param srcWidth
     * @param srcHeight
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    public static float[] getCenterCropTextureCoords(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        float srcTextureAspectRatio = (float)srcHeight / (float)srcWidth;
        float dstTextureAspectRatio = (float)dstHeight / (float)dstWidth;
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if(srcTextureAspectRatio > dstTextureAspectRatio){
            int expectedHeight = (int)((float)srcHeight * dstWidth / (float)srcWidth + 0.5f);
            yOffset = (float)(expectedHeight - dstHeight) / (2 * expectedHeight);
        } else if(srcTextureAspectRatio < dstTextureAspectRatio){
            int expectedWidth = (int)((float)(srcHeight * dstWidth) / (float)dstHeight + 0.5);
            xOffset = (float)(srcWidth - expectedWidth)/(2 * srcWidth);
        }
        float texCoords[] = { xOffset, yOffset, (float)(1.0 - xOffset), yOffset, xOffset, (float)(1.0 - yOffset),
                (float)(1.0 - xOffset), (float)(1.0 - yOffset) };

        return texCoords;
    }
}
