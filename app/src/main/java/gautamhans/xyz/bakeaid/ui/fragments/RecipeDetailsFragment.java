package gautamhans.xyz.bakeaid.ui.fragments;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import gautamhans.xyz.bakeaid.utils.WidgetStateChecker;
import gautamhans.xyz.bakeaid.widget.WidgetUpdateService;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsFragment extends Fragment {
    static String RECIPE_SEL = "recipe_select";
    static String RECIPE_NAME = "recipe_name";
    static String RECYCLER_VIEW_STATE_KEY = "RECYCLER_VIEW_STATE_KEY";
    static String INGREDIENTS_STATE = "INGREDIENTS_STATE";
    static boolean isExpanded = false;
    ArrayList<Recipe> mRecipe;
    String recipeName;
    RelativeLayout mExpandButton;
    @BindView(R.id.ingredients_tv)
    TextView mIngredientsView;
    LinearLayout mLinearLayout;
    @BindView(R.id.rv_steps)
    RecyclerView mRecyclerView;
    @BindView(R.id.ingredientsLayout)
    RelativeLayout ingredientsLayout;
    LinearLayoutManager mLayoutManager;
    Parcelable mListState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_details_fragment, container, false);
        ButterKnife.bind(this, rootView);
        WidgetStateChecker.setWidgetState("detail");

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

        ingredientsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(mLinearLayout, mExpandButton);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY);
            mLayoutManager.onRestoreInstanceState(mListState);
            isExpanded = savedInstanceState.getBoolean(INGREDIENTS_STATE);
            if (isExpanded) {
                mLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    private void addIngredients() {
        WidgetStateChecker.setWidgetState("detail");

        List<Ingredient> ingredients = mRecipe.get(0).getIngredients();
        recipeName = mRecipe.get(0).getName();

        ArrayList<String> ingredientsWidget = new ArrayList<>();
        ingredientsWidget.clear();

        for (int i = 0; i < ingredients.size(); i++) {
            mIngredientsView.append(" --- " + ingredients.get(i).getIngredient() + "\n");
            mIngredientsView.append("\t\t\tQuantity: " + ingredients.get(i).getQuantity().toString() + " " + ingredients.get(i).getMeasure() + "\n");

            System.out.println(ingredients.get(i).getIngredient());
            ingredientsWidget.add(ingredients.get(i).getIngredient() + "\n" + "Quantity: " + ingredients.get(i).getQuantity() + " " + ingredients.get(i).getMeasure() + "\n");
        }

        WidgetUpdateService.startBakingAidService(getActivity(), ingredientsWidget);
    }

    private void addSteps() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecipeDetailsAdapter adapter = new RecipeDetailsAdapter(mRecipe, recipeName, getActivity(), (RecipeDetailsActivity) getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_SEL, mRecipe);
        outState.putString(RECIPE_NAME, recipeName);
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE_KEY, mListState);
        outState.putBoolean(INGREDIENTS_STATE, isExpanded);
    }

    private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout button) {
        if (expandableLayout.getVisibility() == View.VISIBLE) {
            createRotateAnimator(button, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
            isExpanded = false;
        } else {
            createRotateAnimator(button, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            isExpanded = true;
        }
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

}
