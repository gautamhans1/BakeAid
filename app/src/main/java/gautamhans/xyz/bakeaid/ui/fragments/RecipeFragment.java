package gautamhans.xyz.bakeaid.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class RecipeFragment extends Fragment {

    @BindView(R.id.rv_recipes)
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Retrofit mRetrofit;
    private RecipesAPI mRecipesAPI;
    private RecipeAdapter mRecipeAdapter;


    public RecipeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mLinearLayoutManager = new LinearLayoutManager((RecipeActivity) getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRetrofit = RecipesClient.getClient();
        mRecipesAPI = mRetrofit.create(RecipesAPI.class);

        Call<ArrayList<Recipe>> recipes = mRecipesAPI.getRecipes();

        recipes.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Log.d("Response Code: " , String.valueOf(response.code()));
                if(response.isSuccessful()){
                    ArrayList<Recipe> recipes = response.body();
                    mRecipeAdapter = new RecipeAdapter(recipes, (RecipeActivity)getActivity());
                    mRecyclerView.setAdapter(mRecipeAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return rootView;
    }
}
