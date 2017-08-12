package gautamhans.xyz.bakeaid.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.pojos.Step;
import gautamhans.xyz.bakeaid.ui.RecipeActivity;
import gautamhans.xyz.bakeaid.ui.RecipeDetailsActivity;
import timber.log.Timber;

/**
 * Created by Gautam on 08-Aug-17.
 */

public class RecipeStepFragment extends Fragment {

    static final String PLAYER_CURRENT_LOCATION = "PLAYER_CURRENT_LOCATION";
    static final String PLAYER_URI = "PLAYER_URI";
    //    static final String STEP_DESCRIPTION = "STEP_DESCRIPTION";
    private static MediaSessionCompat mMediaSession;
    long currentPosition = 0;
    @BindView(R.id.thumbNail)
    ImageView thumbnailView;
    ArrayList<Recipe> mRecipe;
    String recipeName;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.stepDescription)
    TextView stepDescription;
    @BindView(R.id.previousStep)
    Button mPreviousStep;
    @BindView(R.id.nextStep)
    Button mNextStep;
    private Uri mediaUri;
    //    private String stepDescriptionText;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<Step> mStep;
    private int selectedIndex;
    private String mVideoUrl;
    private String mThumbnailUrl;
    private StepNextPrevListener mStepNextPrevListener;

    public RecipeStepFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mStep = new ArrayList<>();
        mRecipe = new ArrayList<>();

        mStepNextPrevListener = (RecipeDetailsActivity) getActivity();

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
        mThumbnailUrl = mStep.get(selectedIndex).getThumbnailURL();


        if (rootView.getTag() != null && rootView.getTag().equals("landscape")) {
            Log.d("Landscape Mode: ", "True");
            if (!mVideoUrl.isEmpty()) {
                Timber.d("Landscape mode");
                ((RecipeDetailsActivity) getActivity()).getSupportActionBar().hide();
            } else {
                ((RecipeDetailsActivity) getActivity()).getSupportActionBar().show();
            }
        } else {
            Log.d("Landscape Mode: ", "False");
        }

        if (!mVideoUrl.isEmpty()) {
            Uri videoUri = Uri.parse(mVideoUrl);
            mediaUri = videoUri;
            initializeExoPlayer();

        } else if (!mThumbnailUrl.isEmpty()) {
            if (mThumbnailUrl.length() > 4) {
                String format = mThumbnailUrl.substring(mThumbnailUrl.length() - 4);
                if (format.equals(".mp4")) {
                    Uri videoUri = Uri.parse(mThumbnailUrl);
                    if (rootView.getTag() != null && rootView.getTag().equals("landscape")) {
                        ((RecipeDetailsActivity) getActivity()).getSupportActionBar().hide();
                    }
                    mediaUri = videoUri;
                    initializeExoPlayer();
                } else {
                    Uri thumbnailUri = Uri.parse(mThumbnailUrl).buildUpon().build();
                    Glide.with(getContext()).load(thumbnailUri).into(thumbnailView);
                }
            }
        } else {
            mExoPlayer = null;
            mPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.no_video));
            mPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }

        setupClickListeners();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null)
            releasePlayer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (mExoPlayer != null) {
                currentPosition = savedInstanceState.getLong(PLAYER_CURRENT_LOCATION);
                mExoPlayer.seekTo(currentPosition);
                mediaUri = Uri.parse(savedInstanceState.getString(PLAYER_URI));
            }
        }
    }

    private void setupClickListeners() {
        mPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStep.get(selectedIndex).getId() > 0) {
                    if (mExoPlayer != null) {
                        mExoPlayer.stop();
                    }
                    mStepNextPrevListener.onStepClick(mStep, mStep.get(selectedIndex).getId() - 1, recipeName);
                } else {
                    Toast.makeText(getActivity(), "You are already on the first step.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNextStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int lastIndex = mStep.size() - 1;
                if (mStep.get(selectedIndex).getId() < mStep.get(lastIndex).getId()) {
                    if (mExoPlayer != null) {
                        mExoPlayer.stop();
                    }
                    mStepNextPrevListener.onStepClick(mStep, mStep.get(selectedIndex).getId() + 1, recipeName);
                } else {
                    Toast.makeText(getContext(), "You are already on the last step.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("State: ", "onSaveInstanceState()");
        outState.putParcelableArrayList(RecipeDetailsActivity.SELECTED_STEPS, mStep);
        outState.putInt(RecipeDetailsActivity.SELECTED_INDEX, selectedIndex);
        outState.putString(RecipeDetailsActivity.RECIPE_TITLE, recipeName);
        outState.putLong(PLAYER_CURRENT_LOCATION, currentPosition);
        if (mediaUri != null) {
            outState.putString(PLAYER_URI, String.valueOf(mediaUri));
        }
    }

    private void initializeExoPlayer() {
        Uri uri = mediaUri;
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            String userAgent = Util.getUserAgent(getActivity(), "Bake Aid");
            MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);


            mExoPlayer.prepare(mediaSource);
            if (currentPosition != 0) {
                mExoPlayer.seekTo(currentPosition);
            }
            mExoPlayer.setPlayWhenReady(true);
        }
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
    public void onPause() {
        super.onPause();
        Log.d("State: ", "onPause()");
        if (mExoPlayer != null) {
            currentPosition = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeExoPlayer();
    }

    public interface StepNextPrevListener {
        void onStepClick(List<Step> steps, int index, String recipeName);
    }

}
