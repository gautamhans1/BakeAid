package gautamhans.xyz.bakeaid.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Gautam on 09-Aug-17.
 */

public class WidgetUpdateService extends IntentService {

    public static final String INGREDIENTS_LIST = "INGREDIENTS_LIST";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    public static void startBakingAidService(Context context, ArrayList<String> ingredientsList){
        Timber.d("Starting Service\nNewData: " + ingredientsList);
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.putExtra(INGREDIENTS_LIST, ingredientsList);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ArrayList<String> ingredientsList = intent.getExtras().getStringArrayList(INGREDIENTS_LIST);
            handleActionUpdateWidget(ingredientsList);
        }
    }

    private void handleActionUpdateWidget(ArrayList<String> ingredientsList){
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.putExtra(INGREDIENTS_LIST, ingredientsList);
        sendBroadcast(intent);
    }


}
