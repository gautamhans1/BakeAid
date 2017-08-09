package gautamhans.xyz.bakeaid.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.ui.adapters.RecipeAdapter;

public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    public static String RECIPE_SEL = "recipe_select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public void onRecipeClick(Recipe clickedIndex) {
        Bundle bundle = new Bundle();
        ArrayList<Recipe> selectedRecipeData = new ArrayList<>();
        selectedRecipeData.add(clickedIndex);
        bundle.putParcelableArrayList(RECIPE_SEL, selectedRecipeData);

//        Toast.makeText(this, "Recipe Clicked", Toast.LENGTH_SHORT).show();

        final Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}