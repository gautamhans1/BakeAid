package gautamhans.xyz.bakeaid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.pojos.Step;
import gautamhans.xyz.bakeaid.ui.adapters.RecipeDetailsAdapter;
import gautamhans.xyz.bakeaid.ui.fragments.RecipeDetailsFragment;
import gautamhans.xyz.bakeaid.ui.fragments.RecipeStepFragment;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsAdapter.StepClickListener, RecipeStepFragment.StepNextPrevListener {
    //    static final String STACK_RECIPE_DETAILS = "STACK_RECIPE_DETAIL";
    public static final String RECIPE_TITLE = "recipe_title";
    public static String SELECTED_STEPS = "selected_steps";
    public static String SELECTED_INDEX = "selected_index";
    public static String RECIPE_DETAIL_STACK = "recipe_detail_stack";
    String recipeName;
    private ArrayList<Recipe> mRecipe;
    private ActionBar mActionBar;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (savedInstanceState == null) {

            if (getIntent() != null) {
                try {
                    bundle = getIntent().getExtras();
                    mRecipe = new ArrayList<>();
                    mRecipe = bundle.getParcelableArrayList(RecipeActivity.RECIPE_SEL);
                    recipeName = mRecipe.get(0).getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();
            recipeDetailsFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_details_fragment_container, recipeDetailsFragment)
                    .commit();

        } else {
            recipeName = savedInstanceState.getString(RECIPE_TITLE);
        }

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(recipeName);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECIPE_TITLE, recipeName);
    }

    @Override
    public void onStepClick(List<Step> stepOut, int clickedStepIndex, String recipeName) {
        RecipeStepFragment mRecipeStepFragment = new RecipeStepFragment();
        FragmentManager mFragmentManager = getSupportFragmentManager();
        ArrayList<Step> mStep = (ArrayList<Step>) stepOut;

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SELECTED_STEPS, mStep);
        bundle.putInt(SELECTED_INDEX, clickedStepIndex);
        bundle.putString(RECIPE_TITLE, recipeName);
        mRecipeStepFragment.setArguments(bundle);

        mFragmentManager.beginTransaction()
                .replace(R.id.recipe_details_fragment_container, mRecipeStepFragment)
                .addToBackStack(RECIPE_DETAIL_STACK)
                .commit();
    }


}
