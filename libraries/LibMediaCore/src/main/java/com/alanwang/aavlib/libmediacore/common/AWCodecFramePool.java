package com.alanwang.aavlib.libmediacore.common;

import android.media.MediaCodec;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 01:02.
 * Mail: alanwang4523@gmail.com
 */
public class AWCodecFramePool {

    public static final int DEFAULT_POOL_SIZE = 25;

    private int mBufferSize;
    private LinkedBlockingQueue<SoftReference<AWCodecFrame>> mAvailableFrames;

    public AWCodecFramePool(int bufferSize) {
        init(bufferSize, DEFAULT_POOL_SIZE);
    }

    public AWCodecFramePool(int bufferSize, int poolSize) {
        init(bufferSize, poolSize);
    }

    /**
     * 初始化
     *
     * @param mBufferSize 每个SMFrame中Buffer的大小
     * @param poolSize    队列池的大小
     */
    private void init(int mBufferSize, int poolSize) {
        this.mBufferSize = mBufferSize;
        this.mAvailableFrames = new LinkedBlockingQueue<>(poolSize);
    }

    /**
     * 申请SMFrame存储数据的内存空间
     *
     * @param bufSize
     * @return
     */
    private AWCodecFrame allocateFrame(int bufSize) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufSize);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        return new AWCodecFrame(byteBuffer, bufferInfo);
    }

    /**
     * 为循环利用该 AWCodecFrame，从 pool 中申请一个 AWCodecFrame ,此方法申请出来的是干净的已开辟空间的 AWCodecFrame (已清除上次使用遗留的脏数据)，
     * 申请好 AWCodecFrame，填充具体数据后再将其放回 pool 中，数据消费者会从池中取走数据
     *
     * @return
     */
    public synchronized AWCodecFrame requestFrame() {
        AWCodecFrame codecFrame = null;
        if (!mAvailableFrames.isEmpty()) {
            try {
                //阻塞的方法，当队列为空时，调用该方法的线程阻塞，直到队列有数据
                SoftReference<AWCodecFrame> frameSoftReference = mAvailableFrames.take();
                codecFrame = frameSoftReference.get();
                if (codecFrame == null) {
                    codecFrame = allocateFrame(mBufferSize);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                codecFrame = allocateFrame(mBufferSize);
            }
        } else {
            codecFrame = allocateFrame(mBufferSize);
        }
        codecFrame.getBuffer().rewind();//重置buffer
        codecFrame.getBuffer().clear();//清空buffer

        // bufferInfo的设置
        codecFrame.getBufferInfo().offset = 0;
        codecFrame.getBufferInfo().flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
        codecFrame.getBufferInfo().size = 0;
        codecFrame.getBufferInfo().presentationTimeUs = 0L;
        return codecFrame;
    }

    /**
     * 将填充好数据的SMFrame存入队列池中
     *
     * @param smFrame
     */
    public synchronized void put(AWCodecFrame smFrame) {
        try {
            mAvailableFrames.put(new SoftReference<>(smFrame));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从队列池中获取填充好数据的SMFrame
     *
     * @return
     */
    public synchronized AWCodecFrame take() {
        try {
            return mAvailableFrames.take().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return allocateFrame(mBufferSize);
        }
    }

    /**
     * 将队列池清空
     */
    public synchronized void release() {
        if (mAvailableFrames != null) {
            mAvailableFrames.clear();
        }
    }
}
