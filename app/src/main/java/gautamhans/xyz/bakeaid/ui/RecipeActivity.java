package gautamhans.xyz.bakeaid.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import gautamhans.xyz.bakeaid.IdlingResource.SimpleIdlingResource;
import gautamhans.xyz.bakeaid.utils.WidgetStateChecker;
import gautamhans.xyz.bakeaid.widget.WidgetUpdateService;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.ui.adapters.RecipeAdapter;

public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    public static String RECIPE_SEL = "recipe_select";
    public static SharedPreferences sharedPreferences;
    public static String RECIPE_ID = "recip_id";

    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @VisibleForTesting
    @Nullable
    public IdlingResource getIdlingResource(){
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recipe);
        getIdlingResource();
    }

    @Override
    public void onRecipeClick(Recipe clickedIndex) {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> selectedRecipeData = new ArrayList<>();
        selectedRecipeData.add(clickedIndex);
        bundle.putParcelableArrayList(RECIPE_SEL, selectedRecipeData);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(RECIPE_ID, clickedIndex.getId());
        editor.apply();

        final Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WidgetStateChecker.setWidgetState("main");

        List<String> list = new ArrayList<>();
        list.add(0, getString(R.string.recipe_ingredients_widget_not_found_description));

        WidgetUpdateService.startBakingAidService(this, (ArrayList<String>) list);
    }
}