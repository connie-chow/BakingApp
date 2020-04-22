package com.example.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.bakingapp.RecipeContract.BASE_CONTENT_URI;
import static com.example.bakingapp.RecipeContract.PATH_RECIPES;


// https://github.com/udacity/AdvancedAndroid_MyGarden/blob/TWID.02-Exercise-AddWateringService/app/src/main/java/com/example/android/mygarden/provider/PlantContract.java
// https://stackoverflow.com/questions/16939773/get-arraylistnamevaluepair-value-by-name
public class RecipeFetchService extends IntentService {

    public static final String ACTION_FETCH_RECIPES = "com.example.baking.action.fetch_recipes";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RecipeFetchService(String name) {
        super(name);
    }


    // explicitly triggering the Service to perform this action
    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, RecipeFetchService.class);
        intent.setAction(ACTION_FETCH_RECIPES);
        context.startService(intent);
    }


    // To handle this action we need to override onHandleIntent,
    // where you can extract the action and handle each action type separately
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_RECIPES.equals(action)) {
                handleActionWaterPlants();
            }
        }
    }


    // To water all plants we just run an update query setting the last watered time to now,
    // but only for those plants that are still alive. To check if a plant is still alive,
    // you can compare the last watered time with the time now and if the difference is larger
    // than MAX_AGE_WITHOUT_WATER, then the plant is dead!
    private void handleActionWaterPlants() {
        Uri RECIPES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        String recipeId = "";

        Cursor cursor = getContentResolver().query(
                RECIPES_URI,
                null,
                null,
                null,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID
        );


        ArrayList<Map<String, String>> recipeList = new ArrayList<>();  //HashMap<>();

        //Extract the recipes
        if(cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();
            int recipe_id = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);
            int recipe_name = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME);

            final String name = cursor.getString(recipe_name);
            final String id = cursor.getString(recipe_id);

            Map<String, String> entry = new HashMap<>();
            entry.put(id, name);
            recipeList.add(entry);
            cursor.close();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        RecipeWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetIds);




        //contentValues.put("recipe_id", recipeId);
        // Update only plants that are still alive
        // no updates, only read the recipe with the recipe id
        /*
        getContentResolver().update(
                RECIPES_URI,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME+">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});

         */
    }



}