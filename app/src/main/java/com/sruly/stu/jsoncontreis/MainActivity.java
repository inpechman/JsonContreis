package com.sruly.stu.jsoncontreis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sruly.stu.jsoncontreis.logic.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadFileOnConditions("http://www.geognos.com/api/en/countries/info/all.json");

    }

    private void downloadFileOnConditions(String urlPath) {
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        long defaultValue = getResources().getInteger(R.integer.sum_default_num);
        long lastTime = sharedPreferences.getLong(getString(R.string.last_time_opend_app),defaultValue);
        final long TOW_HOERS = 1000*60*60*2;
        long currentTime = System.currentTimeMillis();
        if (currentTime - TOW_HOERS > lastTime){
            if (downloadFile(urlPath)){
                editor = sharedPreferences.edit();
                editor.putLong(getString(R.string.last_time_opend_app), currentTime);
                editor.apply();
            }
        }


    }

    private boolean downloadFile(final String urlPath) {
        final boolean[] result = {true};
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    String json = Helpers.stringFromInputStream(inputStream);
                    JSONObject originalJson = new JSONObject(json);
                    if (originalJson.has("Results")){
                        JSONArray[] dividedJson = Helpers.divideJson(originalJson.getJSONObject("Results"), 4);
                        String baseDir = getFilesDir().getAbsolutePath();
                        for (int i = 0; i < dividedJson.length; i++) {
                            Helpers.saveJsonToFile(dividedJson[i], baseDir + "/countries/" + i + ".json");

                        }
                    }
                } catch (MalformedURLException e) {
                    result[0] = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    result[0] = false;
                    e.printStackTrace();
                } catch (JSONException e) {
                    result[0] = false;
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        return result[0];
    }
}
