package com.example.LAndZVideoPlayer.PlayVideo;

//创建基于MediaPlayer实现的播放器
//Zhu
public class MediaPlayerFactory implements IVideoPlayerFactory {
    @Override
    public AbsBaseVideoPlayer create() {
        return new MediaVideoPlayer();
    }
}
