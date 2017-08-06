package gautamhans.xyz.bakeaid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.ui.fragments.RecipeDetailsFragment;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsActivity extends AppCompatActivity {
//    static final String STACK_RECIPE_DETAILS = "STACK_RECIPE_DETAIL";
    static final String RECIPE_TITLE = "title";
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
                bundle = getIntent().getExtras();
                mRecipe = new ArrayList<>();
                mRecipe = bundle.getParcelableArrayList(RecipeActivity.RECIPE_SEL);
                recipeName = mRecipe.get(0).getName();
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
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECIPE_TITLE, recipeName);
    }
}
