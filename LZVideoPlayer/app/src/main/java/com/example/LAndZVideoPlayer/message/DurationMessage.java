package com.example.LAndZVideoPlayer.message;

//当视频时长解析成功时发送此消息进行视频时长显示的更新
//Liu
public class DurationMessage extends Message {

    private int mDuration;

    public DurationMessage(int hash, String videoUrl, int duration) {
        super(hash, videoUrl);
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

}
