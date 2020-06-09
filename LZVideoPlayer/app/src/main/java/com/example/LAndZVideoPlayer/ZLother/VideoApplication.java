package com.example.LAndZVideoPlayer.ZLother;

import android.app.Application;

import com.example.LAndZVideoPlayer.AppContext;
import com.example.LAndZVideoPlayer.PlayVideo.MediaPlayerFactory;
import com.example.LAndZVideoPlayer.PlayVideo.VideoPlayerManager;

//Liu
public class VideoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.init(this);
        VideoPlayerManager.loadConfig(
                new VideoPlayerConfig.Builder(this)
                        .buildPlayerFactory(new MediaPlayerFactory())
                        .build()
        );
    }
}
