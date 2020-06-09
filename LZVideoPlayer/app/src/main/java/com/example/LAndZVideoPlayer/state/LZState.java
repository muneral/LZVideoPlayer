package com.example.LAndZVideoPlayer.state;

//状态信息
//Liu
public class LZState {

    //屏幕状态
    //普通列表滑动模式（宽大于高）
    public static final int SCREEN_STATE_NORMAL = 1;
    //全屏列表滑动模式（高大于宽）
    public static final int SCREEN_STATE_LIST_FULLSCREEN = 2;
    //单个视频全屏模式
    public static final int SCREEN_STATE_FULLSCREEN = 3;
    public static boolean isFullScreen(int screenState) {
        return screenState == SCREEN_STATE_FULLSCREEN;
    }
    public static boolean isNormal(int screenState) {
        return screenState == SCREEN_STATE_NORMAL;
    }

    //播放器播放状态
    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    //正在播放
    public static final int STATE_PLAYING = 2;
    public static final int STATE_PLAYING_BUFFERING_START = 3;
    //暂停播放
    public static final int STATE_PAUSE = 4;
    //播放完成
    public static final int STATE_AUTO_COMPLETE = 5;
    //播放错误
    public static final int STATE_ERROR = 6;
}
