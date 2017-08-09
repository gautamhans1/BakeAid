package gautamhans.xyz.bakeaid.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import gautamhans.xyz.bakeaid.R;

/**
 * Created by Gautam on 09-Aug-17.
 */

public class WidgetGridService extends RemoteViewsService {

    List<String> remoteViewIngredients;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;

        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            remoteViewIngredients = BakingAidWidgetProvider.ingredientsList;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return remoteViewIngredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_grid_view_item);
            remoteViews.setTextViewText(R.id.widget_grid_view_item, remoteViewIngredients.get(position));
            Intent fillInIntent = new Intent();
            remoteViews.setOnClickFillInIntent(R.id.widget_grid_view_item, fillInIntent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
