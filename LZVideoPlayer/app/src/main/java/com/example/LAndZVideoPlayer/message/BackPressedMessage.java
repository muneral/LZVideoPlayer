package com.example.LAndZVideoPlayer.message;

//全屏状态点击返回键时的消息
//Liu
public class BackPressedMessage extends Message {

    private int mScreenState;

    public BackPressedMessage(int screenState, int hash, String videoUrl) {
        super(hash, videoUrl);
        mScreenState = screenState;
    }

    public int getScreenState() {
        return mScreenState;
    }

}
