package gautamhans.xyz.bakeaid.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gautamhans.xyz.bakeaid.IdlingResource.SimpleIdlingResource;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.retrofit.RecipesAPI;
import gautamhans.xyz.bakeaid.retrofit.RecipesClient;
import gautamhans.xyz.bakeaid.ui.RecipeActivity;
import gautamhans.xyz.bakeaid.ui.adapters.RecipeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeFragment extends android.support.v4.app.Fragment {

    static final String RECIPES_SAVED = "RECIPES_SAVED";
    static final String RECYLER_VIEW_STATE = "RECYLER_VIEW_STATE";
    boolean isPortrait = true;

    Toast mToast;
    @BindView(R.id.refreshButton)
    Button mRefreshButton;
    @BindView(R.id.errorTextView)
    TextView mErrorTextView;
    Parcelable mListState;
    @BindView(R.id.rv_recipes)
    RecyclerView mRecyclerView;
    @BindView(R.id.recipe_progress_bar)
    ProgressBar mProgressBar;
    ArrayList<Recipe> recipes;
    SimpleIdlingResource idlingResource;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private Retrofit mRetrofit;
    private RecipesAPI mRecipesAPI;
    private RecipeAdapter mRecipeAdapter;


    public RecipeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (rootView.getTag() != null && rootView.getTag().equals("landscape")) {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            isPortrait = false;
        } else {
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            isPortrait = true;
        }

        idlingResource = (SimpleIdlingResource) ((RecipeActivity) getActivity()).getIdlingResource();
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        if (savedInstanceState != null) {
            recipes = savedInstanceState.getParcelableArrayList(RECIPES_SAVED);
            mRecipeAdapter = new RecipeAdapter(recipes, getActivity(), (RecipeActivity) getActivity());
            mRecyclerView.setAdapter(mRecipeAdapter);
            mListState = savedInstanceState.getParcelable(RECYLER_VIEW_STATE);
            if (isPortrait) mLinearLayoutManager.onRestoreInstanceState(mListState);
            else mGridLayoutManager.onRestoreInstanceState(mListState);
        } else {
            if (isNetworkAvailable()) {
                loadRecipes();
            } else {
                showError();
            }
        }

        return rootView;
    }

    private void loadRecipes() {
        mRetrofit = RecipesClient.getClient();
        mRecipesAPI = mRetrofit.create(RecipesAPI.class);

        Call<ArrayList<Recipe>> recipes = mRecipesAPI.getRecipes();

        mErrorTextView.setVisibility(View.GONE);
        mRefreshButton.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        recipes.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Log.d("Response Code: ", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    ArrayList<Recipe> recipes = response.body();
                    saveList(recipes);
                    mRecipeAdapter = new RecipeAdapter(recipes, (RecipeActivity) getActivity(), (RecipeActivity) getActivity());
                    mRecyclerView.setAdapter(mRecipeAdapter);

                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("Retrofit Error: ", t.getMessage());

                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG);
                mToast.show();

                t.printStackTrace();
            }
        });
    }

    private void saveList(ArrayList<Recipe> recipe) {
        this.recipes = recipe;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPES_SAVED, recipes);
        if (isPortrait) {
            mListState = mLinearLayoutManager.onSaveInstanceState();
            outState.putParcelable(RECYLER_VIEW_STATE, mListState);
        } else {
            mListState = mGridLayoutManager.onSaveInstanceState();
            outState.putParcelable(RECYLER_VIEW_STATE, mListState);
        }
    }

    // Check for internet connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mRefreshButton.setVisibility(View.VISIBLE);
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getContext(), "Please connect to internet and try again.", Toast.LENGTH_LONG);
        mToast.show();
    }

    @OnClick(R.id.refreshButton)
    public void refreshRecipes() {
        if (isNetworkAvailable()) {
            loadRecipes();
        } else {
            showError();
        }
    }

}
