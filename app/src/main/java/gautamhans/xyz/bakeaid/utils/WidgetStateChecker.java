package gautamhans.xyz.bakeaid.utils;

/**
 * Created by Gautam on 10-Aug-17.
 */

public class WidgetStateChecker {

    private static String WidgetState = "main";

    public static String getWidgetState() {
        return WidgetState;
    }

    public static void setWidgetState(String widgetState) {
        WidgetState = widgetState;
    }
}
