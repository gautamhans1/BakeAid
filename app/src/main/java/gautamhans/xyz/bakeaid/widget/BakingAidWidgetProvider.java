package gautamhans.xyz.bakeaid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.ArrayList;

import gautamhans.xyz.bakeaid.R;
import gautamhans.xyz.bakeaid.ui.RecipeActivity;
import gautamhans.xyz.bakeaid.ui.RecipeDetailsActivity;
import gautamhans.xyz.bakeaid.utils.WidgetStateChecker;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAidWidgetProvider extends AppWidgetProvider {

    static ArrayList<String> ingredientsList = new ArrayList<>();
    static SharedPreferences sharedPreferences;
    static String widgetState;
    static RemoteViews views;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        widgetState = WidgetStateChecker.getWidgetState();
        views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        if (widgetState.equals("main")) {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.addCategory(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_grid_view, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        } else if (widgetState.equals("detail")) {
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.addCategory(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_grid_view, pendingIntent);

            Intent gridServiceIntent = new Intent(context, WidgetGridService.class);
            views.setRemoteAdapter(R.id.widget_grid_view, gridServiceIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        // Instruct the widget manager to update the widget

    }

    public static void updateBakingAidWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingAidWidgetProvider.class));

        final String action = intent.getAction();
        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            ingredientsList = intent.getExtras().getStringArrayList(WidgetUpdateService.INGREDIENTS_LIST);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
            BakingAidWidgetProvider.updateBakingAidWidget(context, appWidgetManager, appWidgetIds);
            super.onReceive(context, intent);
        }
    }
}

