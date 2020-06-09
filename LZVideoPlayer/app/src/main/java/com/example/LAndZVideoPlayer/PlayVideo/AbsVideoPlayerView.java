package com.example.LAndZVideoPlayer.PlayVideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LAndZVideoPlayer.player.R;
import com.example.LAndZVideoPlayer.message.BackPressedMessage;
import com.example.LAndZVideoPlayer.message.DurationMessage;
import com.example.LAndZVideoPlayer.message.Message;
import com.example.LAndZVideoPlayer.message.UIStateMessage;
import com.example.LAndZVideoPlayer.state.LZState;
import com.example.LAndZVideoPlayer.utils.Utils;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//播放器视图，只起到展示视频的作用，视频播放的控制由VideoPlayerManager实现
//Zhu & Liu
public class AbsVideoPlayerView extends RelativeLayout implements
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        Observer {
    // 视频显示媒介容器(TextureView的父容器)
    protected FrameLayout mVideoTextureViewContainer;
    //视频预览图
    protected ImageView mVideoThumbView;
    //底部显示播放进度的进度条
    protected ProgressBar mBottomProgressBar;
    //视频加载进度
    protected ProgressBar mVideoLoadingBar;
    //视频播放按钮
    protected ImageView mVideoPlayView;
    //视频加载失败的提示View
    protected View mVideoErrorView;
    //底部播放控制条
    protected View mVideoControllerView;
    //底部 视频当前播放时间
    protected TextView mVideoPlayTimeView;
    //底部 视频总时长
    protected TextView mVideoTotalTimeView;
    //底部 视频播放进度
    protected SeekBar mVideoPlaySeekBar;
    //底部 全屏播放按钮
    protected ImageView mVideoFullScreenView;;
    //视频顶部显示全屏播放返回按钮和视频标题的父容器
    protected View mVideoHeaderViewContainer;
    //全屏播放时的返回按钮
    protected ImageView mVideoFullScreenBackView;
    //视频标题
    protected TextView mVideoTitleView;
    //播放时底部控制条自动隐藏任务
    protected DismissControllerViewTimerTask mDismissControllerViewTimerTask;
    // 播放时底部控制条自动隐藏触发器
    protected Timer mDismissControllerViewTimer;
    //播放时底部控制条自动隐藏间隔时间
    protected int mAutoDismissTime = 2000;
    //当前Observer（即：VideoPlayerView本身）对象的hashcode
    private int mViewHash;
    //视频标题
    private CharSequence mVideoTitle;
    //视频地址
    private String mVideoUrl;
    //视频时长，miliseconds
    private int mDuration = 0;
    //当前播放状态
    private int mCurrentState = LZState.STATE_NORMAL;
    //当前屏幕播放状态
    private int mCurrentScreenState = LZState.SCREEN_STATE_NORMAL;
    //正常状态下的标题是否显示
    private boolean mShowNormalStateTitleView = true;

    public AbsVideoPlayerView(Context context) {
        super(context);
        initView(context);
    }

    public AbsVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AbsVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mViewHash = this.toString().hashCode();
        inflate(context, R.layout.vp_layout_videoplayer, this);
        //避免ListView中item点击无法响应的问题
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setBackgroundColor(Color.BLACK);

        findAndBindView();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void findAndBindView() {
        mVideoTextureViewContainer = findViewById(R.id.vp_video_surface_container);
        mVideoThumbView = findViewById(R.id.vp_video_thumb);
        mBottomProgressBar = findViewById(R.id.vp_video_bottom_progress);
        mVideoLoadingBar = findViewById(R.id.vp_video_loading);
        mVideoPlayView = findViewById(R.id.vp_video_play);
        mVideoErrorView = findViewById(R.id.vp_video_play_error_view);
        mVideoControllerView = findViewById(R.id.vp_video_bottom_controller_view);
        mVideoPlayTimeView = findViewById(R.id.vp_video_play_time);
        mVideoTotalTimeView = findViewById(R.id.vp_video_total_time);
        mVideoPlaySeekBar = findViewById(R.id.vp_video_seek_progress);
        mVideoFullScreenView = findViewById(R.id.vp_video_fullscreen);
        mVideoHeaderViewContainer = findViewById(R.id.vp_video_header_view);
        mVideoFullScreenBackView = findViewById(R.id.vp_video_fullScreen_back);
        mVideoTitleView = findViewById(R.id.vp_video_title);
       mFullScreenViewStub = findViewById(R.id.vp_fullscreen_view_stub);

        mVideoPlayView.setOnClickListener(this);
        mVideoThumbView.setOnClickListener(this);
        mVideoTextureViewContainer.setOnClickListener(this);
        mVideoTextureViewContainer.setOnTouchListener(this);
        mVideoErrorView.setOnClickListener(this);
        mVideoFullScreenView.setOnClickListener(this);
        mVideoPlaySeekBar.setOnTouchListener(this);
        mVideoErrorView.setOnClickListener(this);
        mVideoControllerView.setOnTouchListener(this);
        mVideoPlaySeekBar.setOnSeekBarChangeListener(this);
        mVideoFullScreenBackView.setOnClickListener(this);
    }

    private void resetViewState() {
        mCurrentState = LZState.STATE_NORMAL;
        mCurrentScreenState = LZState.SCREEN_STATE_NORMAL;
        onPlayStateChanged(mCurrentState);
    }

    //绑定数据
    public void bind(String videoUrl, CharSequence title, boolean showNormalStateTitleView, boolean autoPlay) {
        mShowNormalStateTitleView = showNormalStateTitleView;
        //是否自动播放
        mVideoTitle = title;
        mVideoUrl = videoUrl;
        if (!TextUtils.isEmpty(mVideoTitle)) {
            mVideoTitleView.setText(mVideoTitle);
        }
        resetViewState();
        if (autoPlay) {
            startPlayVideo();
        }
    }

    public void bind(String videoUrl, CharSequence title, boolean autoPlay) {
        bind(videoUrl, title, mShowNormalStateTitleView, autoPlay);
    }

    public void bind(String videoUrl, CharSequence title) {
        bind(videoUrl, title, mShowNormalStateTitleView, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.vp_video_surface_container == id) {
            return;
        }
        if (!VideoPlayerManager.getInstance().isViewPlaying(mViewHash)) {
            //存在正在播放的视频，先将上一个视频停止播放，再继续下一个视频的操作
            VideoPlayerManager.getInstance().stop();
        }
        int state = VideoPlayerManager.getInstance().getState();

        if (R.id.vp_video_play == id) {
            if (TextUtils.isEmpty(mVideoUrl)) {
                Toast.makeText(getContext(), R.string.vp_no_url, Toast.LENGTH_SHORT).show();
                return;
            }
            switch (state) {
                case LZState.STATE_NORMAL:
                case LZState.STATE_ERROR:
                    startPlayVideo();
                    break;
                case LZState.STATE_PLAYING:
                    VideoPlayerManager.getInstance().pause();
                    break;
                case LZState.STATE_PAUSE:
                    VideoPlayerManager.getInstance().play();
                    break;
                case LZState.STATE_AUTO_COMPLETE:
                    VideoPlayerManager.getInstance().seekTo(0);
                    VideoPlayerManager.getInstance().play();
                    break;
            }
} else if (R.id.vp_video_thumb == id) {
        startPlayVideo();
        } else if (R.id.vp_video_fullscreen == id) {
        //全屏播放
        toggleFullScreen();
        } else if (R.id.vp_video_play_error_view == id) {
        startPlayVideo();
        } else if (R.id.vp_video_fullScreen_back == id) {
        exitFullScreen();
        }
    }

    //开始播放视频
    public void startPlayVideo() {
        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //先移除播放器关联的TextureView
        VideoPlayerManager.getInstance().removeTextureView();

        TextureView textureView = createTextureView();
        mVideoTextureViewContainer.addView(textureView);
        //准备开始播放
        VideoPlayerManager.getInstance().start(mVideoUrl, mViewHash);
        VideoPlayerManager.getInstance().setTextureView(textureView);
    }

    public TextureView createTextureView() {
        //重新为播放器关联TextureView
        TextureView textureView = newTextureView();
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureView.setLayoutParams(params);
        return textureView;
    }

    // 创建一个TextureView
    protected TextureView newTextureView() {
        return new TextureView(getContext());
    }

    public ImageView getThumbImageView() {
        return mVideoThumbView;
    }

    //底部SeekBar监听
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int seekToTime = seekBar.getProgress() * mDuration / 100;
            VideoPlayerManager.getInstance().seekTo(seekToTime);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //观察者模式监听播放状态的变化
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VideoPlayerManager.getInstance().addObserver(this);
        mToggleFullScreen = false;
    }

    //播放状态发生改变时的相关逻辑处理
    @Override
    public final void update(Observable o, final Object arg) {

        if (getContext() == null) {
            return;
        }
        if (!(arg instanceof Message)) {
            return;
        }
        if (mViewHash != ((Message) arg).getHash() ||
                !mVideoUrl.equals(((Message) arg).getVideoUrl())) {
            return;
        }

        if (arg instanceof DurationMessage) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onDurationChanged(((DurationMessage) arg).getDuration());
                }
            });
            return;
        }

        if (arg instanceof BackPressedMessage) {
            onBackPressed((BackPressedMessage) arg);
            return;
        }

        if (!(arg instanceof UIStateMessage)) {
            return;
        }
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                onPlayStateChanged(((UIStateMessage) arg).getState());
            }
        });
    }

    //点击返回键时的处理
    protected void onBackPressed(BackPressedMessage message) {
        if (LZState.isFullScreen(message.getScreenState())) {
            exitFullScreen();
        }
    }

    // 播放状态发生改变时调用
    protected void onPlayStateChanged(int state) {
        mCurrentState = state;
        onChangeUIState(state);
        switch (state) {
            case LZState.STATE_NORMAL:
                resetDuration();
                stopVideoProgressUpdate();
                ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case LZState.STATE_LOADING:
            case LZState.STATE_PLAYING_BUFFERING_START:
                break;
            case LZState.STATE_PLAYING:
                startVideoProgressUpdate();
                break;
            case LZState.STATE_PAUSE:
                stopVideoProgressUpdate();
                break;
            case LZState.STATE_AUTO_COMPLETE:
                stopVideoProgressUpdate();
                exitFullScreen();
                break;
            case LZState.STATE_ERROR:
                resetDuration();
                stopVideoProgressUpdate();
                break;
            default:
                throw new IllegalStateException("Illegal Play State:" + state);
        }
    }

    //重置视频时长
    private void resetDuration() {
        mDuration = 0;
    }

    //更新各个播放状态下的UI播放状态消息
    public void onChangeUIState(int state) {
        switch (state) {
            case LZState.STATE_NORMAL:
                onChangeUINormalState();
                break;
            case LZState.STATE_LOADING:
                onChangeUILoadingState();
                break;
            case LZState.STATE_PLAYING:
                onChangeUIPlayingState();
                break;
            case LZState.STATE_PAUSE:
                onChangeUIPauseState();
                break;
            case LZState.STATE_PLAYING_BUFFERING_START:
                onChangeUISeekBufferingState();
                break;
            case LZState.STATE_AUTO_COMPLETE:
                onChangeUICompleteState();
                break;
            case LZState.STATE_ERROR:
                onChangeUIErrorState();
                break;
            default:
                throw new IllegalStateException("Illegal Play State:" + state);
        }
    }

    //更新视频时长信息
    public void onDurationChanged(int duration) {
        mDuration = duration;
        String time = Utils.formatVideoTimeLength(duration);
        mVideoTotalTimeView.setText(time);
    }

    //头部视图状态
    private void onChangeVideoHeaderViewState(boolean showHeaderView) {
        if (showHeaderView == false) {
            Utils.hideViewIfNeed(mVideoHeaderViewContainer);
            return;
        }
        if (LZState.isFullScreen(mCurrentScreenState)) {
            Utils.showViewIfNeed(mVideoHeaderViewContainer);
            Utils.showViewIfNeed(mVideoFullScreenBackView);
        } else if (LZState.isNormal(mCurrentScreenState)) {
            if (mShowNormalStateTitleView) {
                Utils.showViewIfNeed(mVideoHeaderViewContainer);
                Utils.hideViewIfNeed(mVideoFullScreenBackView);
            } else {
                Utils.hideViewIfNeed(mVideoHeaderViewContainer);
            }
        }
    }

    //UI状态更新为Normal状态，即初始状态
    public void onChangeUINormalState() {
        Utils.showViewIfNeed(mVideoThumbView);//显示视频预览图
        Utils.hideViewIfNeed(mVideoLoadingBar);//隐藏加载loading
        mVideoPlayView.setImageResource(R.drawable.vp_play_selector);//显示播放按钮
        Utils.showViewIfNeed(mVideoPlayView);
        Utils.hideViewIfNeed(mVideoControllerView);//隐藏底部控制条
        Utils.hideViewIfNeed(mBottomProgressBar);//隐藏底部播放进度
        Utils.hideViewIfNeed(mVideoErrorView);//隐藏播放错误文案
        onChangeVideoHeaderViewState(true);
    }

    // UI状态更新为Loading状态，即视频加载状态
    public void onChangeUILoadingState() {
        //显示视频预览图
        Utils.hideViewIfNeed(mVideoThumbView);
        //显示加载loading
        Utils.showViewIfNeed(mVideoLoadingBar);
        //隐藏播放按钮
        Utils.hideViewIfNeed(mVideoPlayView);
        //隐藏底部控制条
        Utils.hideViewIfNeed(mVideoControllerView);
        //隐藏底部播放进度
        Utils.hideViewIfNeed(mBottomProgressBar);
        //隐藏播放错误文案
        Utils.hideViewIfNeed(mVideoErrorView);
        onChangeVideoHeaderViewState(false);
    }

    // UI状态更新为Playing状态，即视频播放状态
    public void onChangeUIPlayingState() {
        Utils.hideViewIfNeed(mVideoThumbView);//隐藏视频预览图
        Utils.hideViewIfNeed(mVideoLoadingBar);//隐藏加载loading
        Utils.hideViewIfNeed(mVideoErrorView);//隐藏播放错误文案
        Utils.showViewIfNeed(mVideoControllerView);
        startDismissControllerViewTimer();
        Utils.hideViewIfNeed(mBottomProgressBar); //隐藏底部播放进度
        mVideoPlayView.setImageResource(R.drawable.vp_pause_selector);//显示暂停按钮
        Utils.showViewIfNeed(mVideoPlayView);
        onChangeVideoHeaderViewState(true);
    }

    //UI状态更新为SeekBuffer状态，即视拖动进度条
    public void onChangeUISeekBufferingState() {
        //隐藏视频预览图
        Utils.hideViewIfNeed(mVideoThumbView);
        //隐藏加载loading
        Utils.showViewIfNeed(mVideoLoadingBar);
        //隐藏暂停按钮
        Utils.hideViewIfNeed(mVideoPlayView);
        //隐藏播放错误文案
        Utils.hideViewIfNeed(mVideoErrorView);
            //显示底部控制条
            Utils.showViewIfNeed(mVideoControllerView);
            cancelDismissControllerViewTimer();
            //隐藏底部播放进度
            Utils.hideViewIfNeed(mBottomProgressBar);

        onChangeVideoHeaderViewState(false);
    }

  //UI状态更新为Pause状态，即视频暂停播放状态
    public void onChangeUIPauseState() {
        Utils.hideViewIfNeed(mVideoThumbView);//隐藏视频预览图
        Utils.hideViewIfNeed(mVideoLoadingBar);//隐藏加载loading
        Utils.showViewIfNeed(mVideoControllerView);//显示底部控制条
        cancelDismissControllerViewTimer();
        Utils.hideViewIfNeed(mBottomProgressBar);//隐藏底部播放进度
        Utils.hideViewIfNeed(mVideoErrorView);//隐藏播放错误文案
        mVideoPlayView.setImageResource(R.drawable.vp_play_selector);//显示播放按钮
        Utils.showViewIfNeed(mVideoPlayView);
        onChangeVideoHeaderViewState(true);
    }

    //UI状态更新为Complete状态，即视频播放结束状态
    public void onChangeUICompleteState() {
        //显示视频预览图
        Utils.showViewIfNeed(mVideoThumbView);
        //隐藏加载loading
        Utils.hideViewIfNeed(mVideoLoadingBar);
        mVideoPlayView.setImageResource(R.drawable.vp_replay_selector);//显示再次播放按钮
        Utils.showViewIfNeed(mVideoPlayView);
        //显示底部控制条
        Utils.hideViewIfNeed(mVideoControllerView);
        cancelDismissControllerViewTimer();
        //隐藏底部播放进度
        Utils.hideViewIfNeed(mBottomProgressBar);
        //隐藏播放错误文案
        Utils.hideViewIfNeed(mVideoErrorView);
        updateProgress(mDuration);
        onChangeVideoHeaderViewState(true);
    }

    //UI状态更新为Error状态，即视频播放错误状态
    public void onChangeUIErrorState() {
        //隐藏视频预览图
        Utils.hideViewIfNeed(mVideoThumbView);
        //隐藏加载loading
        Utils.hideViewIfNeed(mVideoLoadingBar);
        //隐藏播放按钮
        Utils.hideViewIfNeed(mVideoPlayView);
        //隐藏底部控制条
        Utils.hideViewIfNeed(mVideoControllerView);
        cancelDismissControllerViewTimer();
        //隐藏底部播放进度
        Utils.hideViewIfNeed(mBottomProgressBar);
        //显示播放错误文案
        Utils.showViewIfNeed(mVideoErrorView);
        onChangeVideoHeaderViewState(false);
    }

    //当触摸视频时更新相关UI状态
    public void onChangeUIWhenTouchVideoView() {
        if (mCurrentState != LZState.STATE_PLAYING) {
            return;
        }
        boolean isAllShown = Utils.isViewShown(mVideoPlayView) && Utils.isViewShown(mVideoControllerView);
        if (isAllShown) {
            hideFullScreenTouchStateView();
        } else {
            showFullScreenTouchStateView();
        }
    }

    private void hideFullScreenTouchStateView() {
        Utils.hideViewIfNeed(mVideoPlayView);
        Utils.hideViewIfNeed(mVideoControllerView);
        Utils.showViewIfNeed(mBottomProgressBar);
        onChangeVideoHeaderViewState(false);
        cancelDismissControllerViewTimer();
    }

    private void showFullScreenTouchStateView() {
        Utils.showViewIfNeed(mVideoPlayView);
        Utils.showViewIfNeed(mVideoControllerView);
        Utils.hideViewIfNeed(mBottomProgressBar);
        startDismissControllerViewTimer();
        onChangeVideoHeaderViewState(true);
    }

    //开始计时，mAutoDismissTime时间后自动隐藏底部控制条和播放按钮
    public void startDismissControllerViewTimer() {
        cancelDismissControllerViewTimer();
        mDismissControllerViewTimer = new Timer();
        mDismissControllerViewTimerTask = new DismissControllerViewTimerTask();
        mDismissControllerViewTimer.schedule(mDismissControllerViewTimerTask, mAutoDismissTime);
    }

    // 取消自动隐藏底部控制条任务
    public void cancelDismissControllerViewTimer() {
        if (mDismissControllerViewTimer != null) {
            mDismissControllerViewTimer.cancel();
        }
        if (mDismissControllerViewTimerTask != null) {
            mDismissControllerViewTimerTask.cancel();
        }
    }

   //播放状态下控制条2秒自动隐藏
    public class DismissControllerViewTimerTask extends TimerTask {
        @Override
        public void run() {
            int state = mCurrentState;
            if (state != LZState.STATE_NORMAL && state != LZState.STATE_ERROR
                    && state != LZState.STATE_AUTO_COMPLETE) {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideFullScreenTouchStateView();
                        }
                    });
                }
            }
        }
    }

    private static final int PROGRESS_UPDATE_INTERNAL = 300;
    private static final int PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;

    //开始更新播放进度
    private void startVideoProgressUpdate() {
        stopVideoProgressUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    //停止更新播放进度
    private void stopVideoProgressUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            int position = VideoPlayerManager.getInstance().getCurrentPosition();
            updateProgress(position);
        }
    };

    private void updateProgress(int position) {
        int progress = position * 100 / (mDuration == 0 ? 1 : mDuration);
        mVideoPlayTimeView.setText(Utils.formatVideoTimeLength(position));
        mVideoPlaySeekBar.setProgress(progress);
        mBottomProgressBar.setProgress(progress);
    }

    //切换全屏播放状态
    private boolean mToggleFullScreen = false;
    //切换全屏播放前当前VideoPlayerView的父容器
    private ViewGroup mOldParent;
    //切换全屏播放前当前VideoPlayerView的在父容器中的索引
    private int mOldIndex = 0;
    //切换到全屏播放前当前VideoPlayerView的宽度
    private int mVideoWidth;
    //切换到全屏播放前当前VideoPlayerView的高度
    private int mVideoHeight;

    //全屏与非全屏切换
    public void toggleFullScreen() {
        if (LZState.isFullScreen(mCurrentScreenState)) {
            exitFullScreen();
        } else if (LZState.isNormal(mCurrentScreenState)) {
            startFullScreen();
        } else {
            throw new IllegalStateException("the screen state is error, state=" + mCurrentScreenState);
        }
    }
    /*全屏播放实现逻辑：
     * 1.将当前VideoPlayerView从父容器中移除
     * 2.然后再将当前VideoPlayerView添加到当前Activity的顶级容器Window.ID_ANDROID_CONTENT中
     * 3.设置当前Activity为全屏状态
     * 4.设置横屏
     * 步骤1和2保证了所有的播放操作均为同一对象，不存在播放状态的变化，因而可以有效的避免播放状态导致的异常崩溃*/

    //全屏播放
    public void startFullScreen() {
        mToggleFullScreen = true;
        VideoPlayerManager.getInstance().setScreenState(mCurrentScreenState = LZState.SCREEN_STATE_FULLSCREEN);
        VideoPlayerManager.getInstance().pause();

        ViewGroup windowContent = (Utils.getActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        mVideoWidth = this.getWidth();
        mVideoHeight = this.getHeight();
        mOldParent = (ViewGroup) this.getParent();
        mOldIndex = mOldParent.indexOfChild(this);
        mOldParent.removeView(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        windowContent.addView(this, lp);

        viewStubFullScreenGestureView();
        Utils.getActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        Utils.getActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mVideoFullScreenView.setImageResource(R.drawable.vp_ic_minimize);
        VideoPlayerManager.getInstance().play();
    }

    //退出全屏播放
    public void exitFullScreen() {
        if (!LZState.isFullScreen(mCurrentScreenState)) {
            return;
        }
        mToggleFullScreen = true;
        VideoPlayerManager.getInstance().setScreenState(mCurrentScreenState = LZState.SCREEN_STATE_NORMAL);
        VideoPlayerManager.getInstance().pause();

        ViewGroup windowContent = (Utils.getActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        windowContent.removeView(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mVideoWidth, mVideoHeight);
        mOldParent.addView(this, mOldIndex, lp);

        Utils.getActivity(getContext()).
                getWindow().
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消全屏
        Utils.getActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mVideoFullScreenView.setImageResource(R.drawable.vp_ic_fullscreen);
        mOldParent = null;
        mOldIndex = 0;
        if (mCurrentState != LZState.STATE_AUTO_COMPLETE) {
            VideoPlayerManager.getInstance().play();
        }
    }

    //手势操作逻辑处理
    private boolean mIsTouchControllerView = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        onTouchToVideoView(event);
        return false;
    }

    //TextureView Touch后显示底部控制条
    public void onTouchToVideoView(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelDismissControllerViewTimer();
                break;
            case MotionEvent.ACTION_UP:
                if (mIsTouchControllerView) {
                    startDismissControllerViewTimer();
                } else {
                    onChangeUIWhenTouchVideoView();
                }
                mIsTouchControllerView = false;
                break;
        }
    }

   //全屏播放时快进后退时的视图
   protected ViewStub mFullScreenViewStub;
   //全屏手势调节快进后退视图

    //初始化全屏播放时手势操作相关参数信息
    private void initFullScreenGestureParams() {
        int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        int currLight = android.provider.Settings.System.getInt(getContext().getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                255);
        float screenLight = currLight / 255f;

        WindowManager.LayoutParams window = ((Activity) getContext()).getWindow().getAttributes();
        window.screenBrightness = screenLight;
    }

    //加载并初始化全屏播放时手势操作相关视图
    protected void viewStubFullScreenGestureView() {
       if (mFullScreenViewStub == null) {
            return;
        }
       mFullScreenViewStub.setVisibility(VISIBLE);
       initFullScreenGestureParams();
    }
}
