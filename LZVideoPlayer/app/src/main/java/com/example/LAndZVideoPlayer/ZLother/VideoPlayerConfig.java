package com.example.LAndZVideoPlayer.ZLother;

import android.content.Context;
import com.example.LAndZVideoPlayer.PlayVideo.IVideoPlayerFactory;
import com.example.LAndZVideoPlayer.PlayVideo.MediaPlayerFactory;

//播放器相关功能开关配置

public final class VideoPlayerConfig {
    private IVideoPlayerFactory mPlayerFactory;

    private VideoPlayerConfig(Builder builder) {
        this.mPlayerFactory = builder.playerFactory;
    }

    public IVideoPlayerFactory getPlayerFactory() {
        return mPlayerFactory;
    }

    public final static class Builder {
        private Context context;
        private IVideoPlayerFactory playerFactory;

        // @param ctx context.getApplicationContext()
        public Builder(Context ctx) {
            this.context = ctx;
        }

        public Builder() {
        }

        //配置Player工厂，用于创建播放器
        public Builder buildPlayerFactory(IVideoPlayerFactory factory) {
            this.playerFactory = factory;
            return this;
        }

        public VideoPlayerConfig build() {
            if (playerFactory == null) {
                playerFactory = new MediaPlayerFactory();
            }
            return new VideoPlayerConfig(this);
        }
    }
}
