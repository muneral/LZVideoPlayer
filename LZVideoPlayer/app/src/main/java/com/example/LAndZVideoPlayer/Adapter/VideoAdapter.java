package com.example.LAndZVideoPlayer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.LAndZVideoPlayer.R;
import com.example.LAndZVideoPlayer.Vdate.VideoBean;
import com.example.LAndZVideoPlayer.Vdate.VideoData;
import com.example.LAndZVideoPlayer.PlayVideo.AbsVideoPlayerView;

import java.util.List;

//Zhu
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<VideoBean> mVideoList;
    private int mScreenWidth;

    public VideoAdapter(Context context) {
        mContext = context;
        mVideoList = VideoData.getVideoList();
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    //为每个Item inflater生成一个View
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item_view, parent, false);
        view.getLayoutParams().width = mScreenWidth;
        view.getLayoutParams().height = (int) (mScreenWidth * 1.0f / 16 * 9 + 0.5f);
        return new VideoViewHolder(view);
    }

    @Override
    //用于适配渲染数据到View中
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoBean video = mVideoList.get(position);
        double count= video.getVideoLikeCount()/(double)10000;
        if(count > 1)
            holder.mPlayerView.bind(video.getVideoUrl(), video.getVideoTitle() + "\n作者: " + video.getVideoAuthor() + "\t\t点赞数：" + count + "万");
        else
            holder.mPlayerView.bind(video.getVideoUrl(), video.getVideoTitle() + "\n作者: " + video.getVideoAuthor() + "\t\t点赞数：" + video.getVideoLikeCount());
        holder.mPlayerView.getThumbImageView().setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext).load(video.getVideoThumbUrl()).into(holder.mPlayerView.getThumbImageView());
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        AbsVideoPlayerView mPlayerView;
        TextView mAuthor;
        TextView mLikeCount;
        public VideoViewHolder(View itemView) {
            super(itemView);
            mPlayerView = itemView.findViewById(R.id.video_player_view);
            mAuthor = itemView.findViewById(R.id.author);
            mLikeCount = itemView.findViewById(R.id.like_count);
        }
    }

}
