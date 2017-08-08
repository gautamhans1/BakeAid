package gautamhans.xyz.bakeaid.ui.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Ingredient;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.ui.RecipeActivity;
import gautamhans.xyz.bakeaid.ui.RecipeDetailsActivity;
import gautamhans.xyz.bakeaid.ui.adapters.RecipeDetailsAdapter;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsFragment extends Fragment {
    static String RECIPE_SEL = "recipe_select";
    static String RECIPE_NAME = "recipe_name";
    ArrayList<Recipe> mRecipe;
    String recipeName;
    RelativeLayout mExpandButton;
    @BindView(R.id.ingredients_tv)
    TextView mIngredientsView;
    LinearLayout mLinearLayout;
    @BindView(R.id.rv_steps)
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.expandaleLayout);
        mExpandButton = (RelativeLayout) rootView.findViewById(R.id.expand_button);
        mLinearLayout.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelableArrayList(RECIPE_SEL);
        } else {
            mRecipe = getArguments().getParcelableArrayList(RecipeActivity.RECIPE_SEL);
        }


        addIngredients();
        addSteps();

        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(mLinearLayout, mExpandButton);
            }
        });

        return rootView;
    }

    private void addIngredients(){
        List<Ingredient> ingredients = mRecipe.get(0).getIngredients();
        recipeName = mRecipe.get(0).getName();

        for (int i = 0; i < ingredients.size(); i++) {
            mIngredientsView.append(" --- " + ingredients.get(i).getIngredient() + "\n");
            mIngredientsView.append("\t\t\tQuantity: " + ingredients.get(i).getQuantity().toString() + " " + ingredients.get(i).getMeasure() + "\n");
        }
    }

    private void addSteps(){
        mLayoutManager = new LinearLayoutManager((RecipeDetailsActivity) getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecipeDetailsAdapter adapter = new RecipeDetailsAdapter(mRecipe, recipeName, (RecipeDetailsActivity) getActivity(), (RecipeDetailsActivity)getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_SEL, mRecipe);
        outState.putString(RECIPE_NAME, recipeName);
    }

    private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout button){
        if (expandableLayout.getVisibility() == View.VISIBLE){
            createRotateAnimator(button, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
        }else{
            createRotateAnimator(button, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
        }
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        return super.onOptionsItemSelected(item);
    }
}
