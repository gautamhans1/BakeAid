package gautamhans.xyz.bakeaid.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.pojos.Recipe;
import gautamhans.xyz.bakeaid.pojos.Step;

/**
 * Created by Gautam on 06-Aug-17.
 */

public class RecipeDetailsAdapter extends RecyclerView.Adapter<RecipeDetailsAdapter.ViewHolder> {

    List<Step> mStep;
    private String mRecipeName;
    private Context mContext;
    private StepClickListener mStepClickListener;

    public RecipeDetailsAdapter(List<Recipe> mStep, String mRecipeName, Context mContext, StepClickListener mStepClickListener) {
        this.mStep = mStep.get(0).getSteps();
        this.mRecipeName = mRecipeName;
        this.mContext = mContext;
        this.mStepClickListener = mStepClickListener;
    }

    @Override
    public RecipeDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_details_fragment_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeDetailsAdapter.ViewHolder holder, int position) {
        if (mStep.get(position).getId() == 0) {
            holder.mStepDescriptionView.setText(mStep.get(position).getShortDescription());
        } else {
            holder.mStepDescriptionView.setText(mStep.get(position).getId() + ". " + mStep.get(position).getShortDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mStep != null ? mStep.size() : 0;
    }

    public interface StepClickListener {
        void onStepClick(List<Step> stepOut, int clickedStepIndex, String recipeName);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.step_desc)
        TextView mStepDescriptionView;
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepClickListener.onStepClick(mStep, getAdapterPosition(), mRecipeName);
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(clickListener);
        }
    }
}
