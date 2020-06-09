package com.example.LAndZVideoPlayer.Vdate;

//Liu
public class VideoBean {
    private String mVideoUrl;
    private String mVideoThumbUrl;
    private String mVideoTitle;
    private String mVideoAuthor;
    private Integer mVideolikecount;

    public VideoBean(String videoUrl, String videoThumbUrl, String videoTitle, String videoAuthor, Integer videolikecount) {
        mVideoUrl = videoUrl;
        mVideoThumbUrl = videoThumbUrl;
        mVideoTitle = videoTitle;
        mVideoAuthor = videoAuthor;
        mVideolikecount = videolikecount;
    }

    public String getVideoUrl() {

        return mVideoUrl;
    }

    public String getVideoThumbUrl() {

        return mVideoThumbUrl;
    }

    public String getVideoTitle() {

        return mVideoTitle;
    }

    public String getVideoAuthor(){
        return mVideoAuthor;
    }

    public Integer getVideoLikeCount(){
        return mVideolikecount;
    }
}
