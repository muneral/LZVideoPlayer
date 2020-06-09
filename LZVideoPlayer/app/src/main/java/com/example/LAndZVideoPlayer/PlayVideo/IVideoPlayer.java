package com.example.LAndZVideoPlayer.PlayVideo;

import android.view.TextureView;

//播放器接口
//Liu
public interface IVideoPlayer {

    //开始播放指定Url的视频
    void start(String url);

   //播放当前视频
    void play();

    //暂停播放
    void pause();

    //恢复播放
    void resume();

    //停止播放,即结束当前视频的播放操作，但不释放资源
    void stop();

   //重置播放器
    void reset();

  //释放资源
    void release();

    //设置播放状态 link LZState
    void setPlayerState(int state);

    //获取当前的播放状态 link LZState
    int getPlayerState();

    //判断当前是否正在播放：播放或暂停时返回true，否则false
    boolean isPlaying();

    //获取当前的播放进度
    int getCurrentPosition();

    //获取视频时长
    int getDuration();

    //跳到position位置开始播放
    void seekTo(int position);

    //设置播放回调函数
    void setPlayCallback(PlayCallback playCallback);

    void setTextureView(TextureView textureView);

    interface PlayCallback {
        void onError(String error);
        void onComplete();
        void onPlayStateChanged(int state);
        void onDurationChanged(int duration);
    }
}
