package com.example.LAndZVideoPlayer.PlayVideo;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import com.example.LAndZVideoPlayer.state.LZState;

//基类播放器
//Zhu
public abstract class AbsBaseVideoPlayer implements
        IVideoPlayer, TextureView.SurfaceTextureListener {

    protected int mState = LZState.STATE_NORMAL;// 播放器状态
    protected String mUrl;// 播放地址
    protected PlayCallback mPlayCallback;//播放器状态回调

   //配合播放器使用
    protected TextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;

    // 准备播放
    protected abstract void prepare();

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        Log.i("LZ", "AbsBaseVideoPlayer onSurfaceTextureAvailable");
        if (mSurfaceTexture == null && (getPlayerState() == LZState.STATE_NORMAL || getPlayerState() == LZState.STATE_LOADING)) {
            prepare();
        }
        mSurfaceTexture = surface;
    }

    @Override
    //SurfaceTexture缓冲大小变化
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i("LZ", "AbsBaseVideoPlayer onSurfaceTextureSizeChanged");
    }

    @Override
    //SurfaceTexture即将被销毁
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i("LZ", "AbsBaseVideoPlayer onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    //SurfaceTexture通过updateImage更新
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i("LZ", "AbsBaseVideoPlayer onSurfaceTextureUpdated");
    }

    @Override
    //当前是否正在播放
    public boolean isPlaying() {
        Log.i("LZ", "AbsBaseVideoPlayer isPlaying");
        return (getPlayerState() == LZState.STATE_PLAYING ||
                getPlayerState() == LZState.STATE_PLAYING_BUFFERING_START) &&
                getCurrentPosition() < getDuration();
    }

    @Override
    //设置播放回调函数
    public void setPlayCallback(PlayCallback playCallback) {
        Log.i("LZ", "AbsBaseVideoPlayer setPlayCallback");
        mPlayCallback = playCallback;
    }

    @Override
    public void setTextureView(TextureView textureView) {
        Log.i("LZ", "AbsBaseVideoPlayer setTextureView");

        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(null);
        }
        mSurfaceTexture = null;
        mTextureView = textureView;
        if (textureView != null) {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

}
