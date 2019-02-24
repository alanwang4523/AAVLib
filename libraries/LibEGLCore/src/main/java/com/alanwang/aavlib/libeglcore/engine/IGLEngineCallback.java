package com.alanwang.aavlib.libeglcore.engine;

import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWMessage;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 01:20.
 * Mail: alanwang4523@gmail.com
 */

public interface IGLEngineCallback {
    /**
     * Engine start
     */
    void onEngineStart();

    /**
     * Surface update
     * @param surface
     * @param width
     * @param height
     */
    void onSurfaceUpdate(Surface surface, int width, int height);

    /**
     * to render
     * @param msg
     */
    void onRender(AWMessage msg);

    /**
     * Surface destroy
     */
    void onSurfaceDestroy();

    /**
     * Engine release
     */
    void onEngineRelease();
}
