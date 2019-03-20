package com.alanwang.aavlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alanwang.aavlib.libmediacore.clipper.AWAVClipper;
import com.alanwang.aavlib.libmediacore.clipper.AWAudioClipper;
import com.alanwang.aavlib.libmediacore.clipper.AWVideoClipper;
import com.alanwang.aavlib.libmediacore.decoder.AWAudioHWDecoderToPCM;
import com.alanwang.aavlib.libmediacore.decoder.AWAudioHWDecoderToWav;
import com.alanwang.aavlib.libmediacore.encoder.AWAudioWavFileEncoder;
import com.alanwang.aavlib.libmediacore.exception.AWAudioException;
import com.alanwang.aavlib.libmediacore.exception.AWException;
import com.alanwang.aavlib.libmediacore.listener.AWMediaListener;
import com.alanwang.aavlib.libmediacore.muxer.AWAVAndroidMuxer;
import com.alanwang.aavlib.libutils.ALog;
import java.io.File;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/2/22 01:59.
 * Mail: alanwang4523@gmail.com
 */
public class TestLibMediaCoreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_libmediacore);

        TextView btn_test_extract_audio = findViewById(R.id.btn_libmediacore_test_extract_audio);
        btn_test_extract_audio.setOnClickListener(this);

        TextView btn_test_extract_video = findViewById(R.id.btn_libmediacore_test_extract_video);
        btn_test_extract_video.setOnClickListener(this);

        TextView btn_test_clipper = findViewById(R.id.btn_libmediacore_test_clipper);
        btn_test_clipper.setOnClickListener(this);

        TextView btn_test_muxer = findViewById(R.id.btn_libmediacore_test_muxer);
        btn_test_muxer.setOnClickListener(this);

        TextView btn_test_wav_encoder = findViewById(R.id.btn_libmediacore_test_wav_encoder);
        btn_test_wav_encoder.setOnClickListener(this);

        TextView btn_test_decoder_to_pcm = findViewById(R.id.btn_libmediacore_test_audio_decoder_to_pcm);
        btn_test_decoder_to_pcm.setOnClickListener(this);

        TextView btn_test_decoder_to_wav = findViewById(R.id.btn_libmediacore_test_audio_decoder_to_wav);
        btn_test_decoder_to_wav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_libmediacore_test_extract_audio:
                testExtractAudio();
                break;
            case R.id.btn_libmediacore_test_extract_video:
                testExtractVideo();
                break;
            case R.id.btn_libmediacore_test_clipper:
                testClipper();
                break;
            case R.id.btn_libmediacore_test_muxer:
                testMuxer();
                break;
            case R.id.btn_libmediacore_test_wav_encoder:
                testWavEncoder();
                break;
            case R.id.btn_libmediacore_test_audio_decoder_to_pcm:
                testM4aDecodeToPCM();
                break;
            case R.id.btn_libmediacore_test_audio_decoder_to_wav:
                testM4aDecodeToWAV();
                break;
            default:
        }
    }

    /**
     * 测试从视频文件中抽取出音频
     */
    private void testExtractAudio() {
        final String outputPath = "/sdcard/Alan/video/AlanTest_audio.m4a";
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        String mediaPath = "/sdcard/Alan/video/AlanTest.mp4";
        if (!checkIfFileExist(mediaPath)) {
            return;
        }

        AWAudioClipper audioClipper = new AWAudioClipper(outputPath);
        try {
            audioClipper.setDataSource(mediaPath);
            audioClipper.setExtractTime(5 * 1000, 15 * 1000);// 不调用该函数则默认抽取全部音频
            audioClipper.setProcessListener(new CommonProgressListener("AWAudioClipper", outputPath));
            audioClipper.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试从视频文件中抽取出无声视频
     */
    private void testExtractVideo() {
        final String outputPath = "/sdcard/Alan/video/AlanTest_video.mp4";
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        String mediaPath = "/sdcard/Alan/video/AlanTest.mp4";
        if (!checkIfFileExist(mediaPath)) {
            return;
        }

        AWVideoClipper videoClipper = new AWVideoClipper(outputPath);
        try {
            videoClipper.setDataSource(mediaPath);
            videoClipper.setExtractTime(5 * 1000, 15 * 1000);// 不调用该函数则默认抽取全部音频
            videoClipper.setProcessListener(new CommonProgressListener("AWVideoClipper", outputPath));
            videoClipper.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试音视频裁剪
     */
    private void testClipper() {
        final String outputPath = "/sdcard/Alan/video/AlanTest_clip.mp4";
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        String mediaPath = "/sdcard/Alan/video/AlanTest.mp4";
        if (!checkIfFileExist(mediaPath)) {
            return;
        }

        AWAVClipper avClipper = new AWAVClipper();
        try {
            avClipper.setDataSource(mediaPath, outputPath);
            long clipStartTimeMs = 5 * 1000;
            long clipEndTimeMs = 15 * 1000;
            avClipper.setExtractTime(clipStartTimeMs, clipEndTimeMs);
            avClipper.setProcessListener(new CommonProgressListener("AWAVClipper", outputPath));
            avClipper.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试音视频合成
     */
    private void testMuxer() {
        final String outputPath = "/sdcard/Alan/video/AlanTest_muxer.mp4";
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        String audioPath = "/sdcard/Alan/video/AlanTest_audio.m4a";
        if (!checkIfFileExist(audioPath)) {
            return;
        }
        String videoPath = "/sdcard/Alan/video/AlanTest_video.mp4";
        if (!checkIfFileExist(videoPath)) {
            return;
        }

        AWAVAndroidMuxer avMuxer = new AWAVAndroidMuxer();
        try {
            avMuxer.setDataSource(audioPath, videoPath, outputPath);
            avMuxer.setProcessListener(new CommonProgressListener("AWAVAndroidMuxer", outputPath));
            avMuxer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 wav 文件编码
     */
    private void testWavEncoder() {
        final String outputPath = "/sdcard/Alan/video/audio_test.m4a";
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        String srcWavFile = "/sdcard/Alan/video/audio_test.wav";
        if (!checkIfFileExist(srcWavFile)) {
            return;
        }

        AWAudioWavFileEncoder wavFileEncoder = new AWAudioWavFileEncoder();
        try {
            wavFileEncoder.setDataSource(srcWavFile, outputPath);
            wavFileEncoder.setup(64 * 1024);
            wavFileEncoder.setEncodeTime(30 * 1000, -1);
            wavFileEncoder.setProcessListener(new CommonProgressListener(
                    "AWAudioWavFileEncoder", outputPath));
            wavFileEncoder.start();
        } catch (AWAudioException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 m4a 解码
     */
    private void testM4aDecodeToPCM() {
        final String srcMa4Path = "/sdcard/Alan/video/audio_test.m4a";
        if (!checkIfFileExist(srcMa4Path)) {
            return;
        }

        String outputFilePath = "/sdcard/Alan/video/audio_test.pcm";
        File outputFile = new File(outputFilePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        AWAudioHWDecoderToPCM audioHWDecoderForPCM = new AWAudioHWDecoderToPCM();
        try {
            audioHWDecoderForPCM.setDataSource(srcMa4Path);
            audioHWDecoderForPCM.setOutputFile(outputFilePath);
            audioHWDecoderForPCM.setProcessListener(new CommonProgressListener(
                    "AWAudioHWDecoderToPCM", outputFilePath));
            audioHWDecoderForPCM.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 m4a 解码到 wav
     */
    private void testM4aDecodeToWAV() {
        final String srcMa4Path = "/sdcard/Alan/video/audio_test.m4a";
        if (!checkIfFileExist(srcMa4Path)) {
            return;
        }

        String outputFilePath = "/sdcard/Alan/video/audio_test.wav";
        File outputFile = new File(outputFilePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        AWAudioHWDecoderToWav audioHWDecoderToWav = new AWAudioHWDecoderToWav();
        try {
            audioHWDecoderToWav.setDataSource(srcMa4Path);
            audioHWDecoderToWav.setOutputFile(outputFilePath);
            audioHWDecoderToWav.setProcessListener(new CommonProgressListener(
                    "AWAudioHWDecoderToWav", outputFilePath));
            audioHWDecoderToWav.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测文件是否存在
     * @param mediaPath
     * @return
     */
    private boolean checkIfFileExist(String mediaPath) {
        File file = new File(mediaPath);
        if (!file.exists()) {
            Toast.makeText(this, mediaPath + " is not exists!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 在 UI 线程弹出 toast
     * @param toastMsg
     * @param isShowLong
     */
    private void toastInUiThread(final String toastMsg, final boolean isShowLong) {
        TestLibMediaCoreActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestLibMediaCoreActivity.this, toastMsg,
                        isShowLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CommonProgressListener implements AWMediaListener<Void> {
        private String Tag;
        private String outPath;
        private int lastProgress = 0;

        public CommonProgressListener(String tag, String outPath) {
            Tag = tag;
            this.outPath = outPath;
        }

        @Override
        public void onProgress(int percent) {
            if (percent - lastProgress >= 5) {
                lastProgress = percent;
                ALog.d(Tag + "::onProgress()-->" + percent);
            }
        }

        @Override
        public void onSuccess(Void result) {
            ALog.d(Tag + "::onFinish()-->" + outPath);
            toastInUiThread(Tag + " success!", true);
        }

        @Override
        public void onError(AWException e) {
            ALog.d(Tag + "::onError()-->" + e);
            toastInUiThread(Tag + " error!-->" + e.getErrorMsg(), true);
        }
    }

}
