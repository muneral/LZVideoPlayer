package com.example.LAndZVideoPlayer.PlayVideo;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;

import com.example.LAndZVideoPlayer.ZLother.VideoPlayerConfig;
import com.example.LAndZVideoPlayer.message.DurationMessage;
import com.example.LAndZVideoPlayer.message.Message;
import com.example.LAndZVideoPlayer.message.UIStateMessage;
import com.example.LAndZVideoPlayer.state.LZState;
import com.example.LAndZVideoPlayer.utils.Utils;

import java.util.Observable;
import java.util.Observer;

//视频播放管理类，主要与视频展示展示UI进行交互，视频播放的具体操作交由播放器抽象类VideoPlayer实现
//Zhu
public final class VideoPlayerManager implements IVideoPlayer.PlayCallback {
    private static final String TAG = "VideoPlayerManager";
    private static volatile VideoPlayerManager sVideoPlayerManager;
    // 播放器实例
    private AbsBaseVideoPlayer mPlayer;
    // 播放状态观察者
    private PlayStateObservable mPlayStateObservable;
    // 当前播放地址
    private String mVideoUrl;
    private int mObserverHash = -1;
    // 当前模仿窗口模式
    private int mScreenState = LZState.SCREEN_STATE_NORMAL;
    // 播放相关配置
    private VideoPlayerConfig mVideoPlayerConfig;

    //传入播放器配置
    private VideoPlayerManager(VideoPlayerConfig videoPlayerConfig) {
        mVideoPlayerConfig = videoPlayerConfig;
        createPlayer();
        mPlayStateObservable = new PlayStateObservable();
    }

    //获取实例，全局有且只有一个
    public static VideoPlayerManager getInstance() {
        if (sVideoPlayerManager == null) {
            synchronized (VideoPlayerManager.class) {
                if (sVideoPlayerManager == null) {
                    loadConfig(new VideoPlayerConfig.Builder().build()); //加载默认配置
                }
            }
        }
        if (sVideoPlayerManager.mPlayer == null) {
            synchronized (VideoPlayerManager.class) {
                if (sVideoPlayerManager.mPlayer == null) {
                    sVideoPlayerManager.createPlayer();
                }
            }
        }
        return sVideoPlayerManager;
    }

    //创建播放器实例
    private void createPlayer() {
        mPlayer = mVideoPlayerConfig.getPlayerFactory().create();
        mPlayer.setPlayCallback(this);
    }

    public VideoPlayerConfig getConfig() {
        return mVideoPlayerConfig;
    }

    //加载配置
    public static void loadConfig(VideoPlayerConfig videoPlayerConfig) {
        if (sVideoPlayerManager == null) {
            sVideoPlayerManager = new VideoPlayerManager(videoPlayerConfig);
        }
    }

    public void removeTextureView() {
        if (mPlayer.mTextureView != null &&
                mPlayer.mTextureView.getParent() != null) {
            ((ViewGroup) mPlayer.mTextureView.getParent()).removeView(mPlayer.mTextureView);
            setTextureView(null);
        }
    }

    public void setTextureView(TextureView textureView) {
        mPlayer.setTextureView(textureView);
    }

    //获取正在播放的视频地址，必须在stop或release方法调用之前获取
    public String getVideoUrl() {
        return mVideoUrl;
    }

    @SuppressLint("DefaultLocale")
    void start(String url, int observerHash) {
        bindPlayerView(url, observerHash);
        onPlayStateChanged(LZState.STATE_LOADING);
        mPlayer.start(url);
    }

    private void bindPlayerView(String url, int observerHash) {
        this.mVideoUrl = url;
        this.mObserverHash = observerHash;
    }

    @SuppressLint("DefaultLocale")
    public void play() {
        mPlayer.play();
        onPlayStateChanged(LZState.STATE_PLAYING);
    }

    @SuppressLint("DefaultLocale")
    public void resume() {
        if (getState() == LZState.STATE_PAUSE) {
            play();
        }
    }

    @SuppressLint("DefaultLocale")
    public void pause() {
        if (getState() == LZState.STATE_PLAYING) {
            mPlayer.pause();
            onPlayStateChanged(LZState.STATE_PAUSE);
        }
    }

    @SuppressLint("DefaultLocale")
    public void stop() {
        onPlayStateChanged(LZState.STATE_NORMAL);
        mPlayer.stop();
        removeTextureView();
        mObserverHash = -1;
        mVideoUrl = null;
        mScreenState = LZState.SCREEN_STATE_NORMAL;
    }

    public void release() {
        mPlayer.setPlayerState(LZState.STATE_NORMAL);
        removeTextureView();
        mPlayer.release();
        mPlayer = null;
        mObserverHash = -1;
        mVideoUrl = null;
        mScreenState = LZState.SCREEN_STATE_NORMAL;
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    //指定View是否在播放视频
    public boolean isViewPlaying(int viewHash) {
        return mObserverHash == viewHash;
    }

    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        if (isPlaying()) {
            onPlayStateChanged(LZState.STATE_PLAYING_BUFFERING_START);
        }
        mPlayer.seekTo(position);
    }

    public int getState() {
        return sVideoPlayerManager.mPlayer.getPlayerState();
    }

    @Override
    public void onError(String error) {
        if (!TextUtils.isEmpty(error)) {
            Log.d(TAG, error);
        }
        mPlayer.stop();
        changeUIState(LZState.STATE_ERROR);
    }

    @Override
    public void onComplete() {
        changeUIState(LZState.STATE_AUTO_COMPLETE);
    }

    @Override
    public void onPlayStateChanged(int state) {
        changeUIState(state);
    }

    @Override
    public void onDurationChanged(int duration) {
        mPlayStateObservable.notify(new DurationMessage(mObserverHash, mVideoUrl, duration));
    }

    public void addObserver(Observer observer) {
        mPlayStateObservable.addObserver(observer);
    }

    private void changeUIState(int state) {
        mPlayer.setPlayerState(state);
        mPlayStateObservable.notify(new UIStateMessage(mObserverHash, mVideoUrl, state));
    }

    public void setScreenState(int screenState) {
        mScreenState = screenState;
    }

    static class PlayStateObservable extends Observable {

        private void setObservableChanged() {
            this.setChanged();
        }

        void notify(Message message) {
            setObservableChanged();
            notifyObservers(message);
        }
    }
}