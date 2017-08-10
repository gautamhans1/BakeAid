package gautamhans.xyz.bakeaid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import gautamhans.xyz.bakeaid.utils.WidgetStateChecker;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsAdapter.StepClickListener, RecipeStepFragment.StepNextPrevListener {
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

        WidgetStateChecker.setWidgetState("detail");

        if (savedInstanceState == null) {

            if (getIntent() != null) {
                    bundle = getIntent().getExtras();
                    mRecipe = new ArrayList<>();
                    mRecipe = bundle.getParcelableArrayList(RecipeActivity.RECIPE_SEL);
                    recipeName = mRecipe.get(0).getName();
            }

            RecipeDetailsFragment recipeDetailsFragment = newInstance(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, recipeDetailsFragment)
                    .commit();

            if (findViewById(R.id.land_sw600_layout).getTag() != null && findViewById(R.id.land_sw600_layout).getTag().equals("tablet-landscape")) {
                final RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                recipeStepFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_video, recipeStepFragment)
                        .commit();
            }

        } else {
            recipeName = savedInstanceState.getString(RECIPE_TITLE);
        }

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(recipeName);
        }


    }

    public static RecipeDetailsFragment newInstance(Bundle bundle) {
        RecipeDetailsFragment f = new RecipeDetailsFragment();
        f.setArguments(bundle);
        return f;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
        RecipeStepFragment mRecipeStepFragment = newStepInstance(stepOut, clickedStepIndex, recipeName);
        FragmentManager mFragmentManager = getSupportFragmentManager();

        if (getResources().getBoolean(R.bool.tablet_land)) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_video, mRecipeStepFragment)
                    .commit();

        } else {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mRecipeStepFragment)
                    .addToBackStack(RECIPE_DETAIL_STACK)
                    .commit();
        }
    }

    public static RecipeStepFragment newStepInstance(List<Step> stepOut, int clickedStepIndex, String recipeName){
        RecipeStepFragment f = new RecipeStepFragment();
        ArrayList<Step> mStep = (ArrayList<Step>) stepOut;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SELECTED_STEPS, mStep);
        bundle.putInt(SELECTED_INDEX, clickedStepIndex);
        bundle.putString(RECIPE_TITLE, recipeName);
        f.setArguments(bundle);
        return f;
    }
}
