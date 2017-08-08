package gautamhans.xyz.bakeaid.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.pojos.Step;
import gautamhans.xyz.bakeaid.ui.RecipeActivity;
import gautamhans.xyz.bakeaid.ui.RecipeDetailsActivity;

/**
 * Created by Gautam on 08-Aug-17.
 */

public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {
    private static MediaSessionCompat mMediaSession;
    ArrayList<Recipe> mRecipe;
    String recipeName;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.stepDescription)
    TextView stepDescription;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<Step> mStep;
    private int selectedIndex;
    private Handler mHandler;
    private String mVideoUrl;

    public RecipeStepFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mHandler = new Handler();
        mStep = new ArrayList<>();
        mRecipe = new ArrayList<>();

        initializeMediaSession();


        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelableArrayList(RecipeDetailsActivity.SELECTED_STEPS);
            selectedIndex = savedInstanceState.getInt(RecipeDetailsActivity.SELECTED_INDEX);
            recipeName = savedInstanceState.getString(RecipeDetailsActivity.RECIPE_TITLE);
        } else {
            mStep = getArguments().getParcelableArrayList(RecipeDetailsActivity.SELECTED_STEPS);
            if (mStep != null) {
                selectedIndex = getArguments().getInt(RecipeDetailsActivity.SELECTED_INDEX);
                recipeName = getArguments().getString(RecipeDetailsActivity.RECIPE_TITLE);
            } else {
                mRecipe = getArguments().getParcelableArrayList(RecipeActivity.RECIPE_SEL);
                mStep = (ArrayList<Step>) mRecipe.get(0).getSteps();
                selectedIndex = 0;
            }
        }

        stepDescription.setText("Description: \n\n" + mStep.get(selectedIndex).getDescription());
        mVideoUrl = mStep.get(selectedIndex).getVideoURL();

        if (!mVideoUrl.isEmpty()) {
            Uri videoUri = Uri.parse(mVideoUrl);
            initializeExoPlayer(videoUri);
        } else {
            mExoPlayer = null;
            mPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.no_video));
            mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RecipeDetailsActivity.SELECTED_STEPS, mStep);
        outState.putInt(RecipeDetailsActivity.SELECTED_INDEX, selectedIndex);
        outState.putString(RecipeDetailsActivity.RECIPE_TITLE, recipeName);
    }

    private void initializeExoPlayer(Uri uri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance((RecipeDetailsActivity) getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent((RecipeDetailsActivity) getActivity(), "Bake Aid");
            MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                    (RecipeDetailsActivity) getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat((RecipeDetailsActivity) getActivity(), RecipeDetailsActivity.class.getSimpleName());

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}