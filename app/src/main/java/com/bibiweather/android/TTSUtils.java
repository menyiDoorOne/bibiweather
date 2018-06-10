package com.bibiweather.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by 门一 on 2018/6/4.
 */


//这是尝试的另一种语音工具类，这次APP中没有用到

public class TTSUtils implements InitListener, SynthesizerListener {

    private static final String TAG = "TTSUtils";
    private static volatile TTSUtils instance = null;
    private boolean isInitSuccess = false;
    private SpeechSynthesizer mTts;
    // 上下文
    private Context mContext;

    public TTSUtils() {

    }

    public static TTSUtils getInstance() {
        if (instance == null) {
            synchronized (TTSUtils.class) {
                if (instance == null) {
                    instance = new TTSUtils();
                }
            }
        }
        return instance;
    }

    public void init() {
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(mContext, this);
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 设置在线合成引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    public void speak(String msg) {
        if (isInitSuccess){
            if (mTts.isSpeaking()) {
                stop();
            }
            mTts.startSpeaking(msg, this);
        }else {
            init();
        }
    }

    public void pause() {
        mTts.pauseSpeaking();
    }

    public void resume() {
        mTts.resumeSpeaking();
    }

    public void stop() {
        mTts.stopSpeaking();
    }

    public void release() {
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    @Override
    public void onInit(int code) {
        Log.d(TAG, "InitListener init() code = " + code);
        if (code == ErrorCode.SUCCESS) {
            isInitSuccess = true;
        }
    }

    @Override
    public void onSpeakBegin() {
        // 开始播放
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos,
                                 String info) {
        // 合成进度
    }

    @Override
    public void onSpeakPaused() {
        // 暂停播放
    }

    @Override
    public void onSpeakResumed() {
        // 继续播放
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        // 播放进度
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (speechError != null) {
            Log.d(TAG, "onCompleted: " + speechError.getPlainDescription(true));
        }
    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        //以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            String sid = bundle.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            Log.d(TAG, "session id =" + sid);
        }
    }
}