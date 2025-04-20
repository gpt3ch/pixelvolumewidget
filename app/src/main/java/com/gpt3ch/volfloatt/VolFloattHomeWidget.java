package com.gpt3ch.volfloatt;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class VolFloattHomeWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        // Create an Intent to start your FloatingWidgetService
        final Intent serviceIntent = new Intent(context, FloatingWidgetService.class);

        // Create a PendingIntent to start the service when the widget is clicked
        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the RemoteViews for your widget layout
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.vol_floatt_home_widget);

        // Set the OnClickListener for the widget to the PendingIntent
        views.setOnClickPendingIntent(R.id.widget_button, pendingIntent); // Assuming you have a Button with this ID in your widget layout

        // Update the app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(final Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(final Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}