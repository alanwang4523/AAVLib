package com.alanwang.aavlib.libutils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: AlanWang4523.
 * Date: 19/1/24 00:51.
 * Mail: alanwang4523@gmail.com
 */

public class AAVBitmapUtils {
    private static final String TAG = AAVBitmapUtils.class.getSimpleName();

    /**
     * 从资源文件获取 bitmap
     * @param res
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources res, @RawRes int id) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            try {
                is = res.openRawResource(id);
                bitmap = decodeStream(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        }
        return bitmap;
    }

    /**
     * 从输入流获取 bitmap
     * @param is
     * @return
     */
    public static Bitmap decodeStream(InputStream is) {
        Bitmap bitmap = null;
        if (is != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                bitmap = BitmapFactory.decodeStream(is, null, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    is.reset();
                    bitmap = BitmapFactory.decodeStream(is, null, options);
                } catch (OutOfMemoryError e1) {
                } catch (Exception e2) {
                }
            } catch (Exception e3) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        }
        return bitmap;
    }

    /**
     * 保存 Bitmap 至本地，如果失败进行重试
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, String path) {
        if ((bitmap != null) && !TextUtils.isEmpty(path)) {
            File file = new File(path);
            int saveTimes = 3;
            boolean isSaveSuccess;
            do {
                isSaveSuccess = saveBitmapToFile(bitmap, Bitmap.CompressFormat.JPEG, 90, file);
                if (isSaveSuccess) {
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                saveTimes--;
            } while (saveTimes > 0);
            return isSaveSuccess;
        } else {
            Log.e(TAG , " saveBitmap with a null param.");
            return false;
        }
    }

    /**
     * 将 bitmap 保存到文件
     * @param bmp
     * @param format
     * @param quality
     * @param file
     * @return
     */
    public static boolean saveBitmapToFile(Bitmap bmp, Bitmap.CompressFormat format, int quality, File file) {
        FileOutputStream fos = null;
        if (bmp == null) {
            return false;
        }
        try {
            if (file.exists() || file.createNewFile()) {
                fos = new FileOutputStream(file);
                bmp.compress(format, quality, fos);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (!bmp.isRecycled()) {
                    bmp.recycle();
                }
            } catch (Exception e) {}
        }
        return false;
    }

    /**
     * 镜像水平翻转图片
     * @param src 原始图片
     * @return 结果图片
     */
    public static Bitmap flipH(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        return convert(src, matrix);
    }

    /**
     * 镜像垂直翻转图片
     * @param src 原始图片
     * @return 结果图片
     */
    public static Bitmap flipV(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1); // 镜像垂直翻转
        return convert(src, matrix);
    }

    /**
     * 对图片进行矩阵变换
     * @param src    原始图片
     * @param matrix 变换矩阵
     * @return 结果图片
     */
    public static Bitmap convert(Bitmap src, Matrix matrix) {
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
