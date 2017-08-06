package gautamhans.xyz.bakeaid.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<Recipe> mRecipe;
    private Context mContext;

    public RecipeAdapter(ArrayList<Recipe> mRecipe, Context mContext) {
        this.mRecipe = mRecipe;
        this.mContext = mContext;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_fragment_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        holder.mRecipeName.setText(mRecipe.get(position).getName());

        String imageUrl = mRecipe.get(position).getImage();
        if (imageUrl != "") {
            Uri uri = Uri.parse(imageUrl).buildUpon().build();
            Glide.with(mContext).load(uri).into(holder.mRecipeImage);
        } else {
            // TODO : Optimize Images & this code
            switch (mRecipe.get(position).getId()){
                case 1:
                    holder.mRecipeImage.setImageResource(R.drawable.nutella_pie);
                    break;
                case 2:
                    holder.mRecipeImage.setImageResource(R.drawable.brownies);
                    break;
                case 3:
                    holder.mRecipeImage.setImageResource(R.drawable.yellow_cake);
                    break;
                case 4:
                    holder.mRecipeImage.setImageResource(R.drawable.cheesecake);
                    break;
                default:
                    holder.mRecipeImage.setImageResource(R.drawable.food);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipe != null) return mRecipe.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name)
        TextView mRecipeName;
        @BindView(R.id.recipe_image)
        ImageView mRecipeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
