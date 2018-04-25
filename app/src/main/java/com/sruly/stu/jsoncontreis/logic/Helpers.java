package com.sruly.stu.jsoncontreis.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stu on 4/25/2018.
 *
 */

public class Helpers {
    public static String stringFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024 * 8];
        int counter;
        while ((counter = inputStream.read(buffer)) != -1){
            stringBuilder.append(new String(buffer, 0, counter));
        }
        return stringBuilder.toString();
    }

    public static JSONArray[] divideJson(JSONObject results, int parts) throws JSONException {
        JSONArray keys = results.names();
        int numOfObjInFile = keys.length() / parts + 1;
        JSONArray[] jsonArrays = new JSONArray[parts];
        for (int i = 0; i < jsonArrays.length; i++) {
            jsonArrays[i] = new JSONArray();
        }

        for (int i = 0, array = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            JSONObject currentCountry = results.getJSONObject(key);
            jsonArrays[array].put(remoteToLocal(key, currentCountry));
            if (i != 0 && i % numOfObjInFile == 0){
                array++;
            }
        }

        return jsonArrays;
    }

    private static JSONObject remoteToLocal(String key, JSONObject remoteObject) throws JSONException {
        JSONObject localObject = new JSONObject();
        localObject.put("countryID", key);
        localObject.put("countryName", remoteObject.getString("Name"));
        localObject.put("countryLocation", remoteObject.getJSONArray("GeoPt"));
        localObject.put("telPrefix", remoteObject.getString("TelPref"));
//        JSONObject capital = remoteObject.getJSONObject("Capital");
//        localObject.put("capitalName", capital.getString("Name"));
//        localObject.put("capitalLocation", capital.getJSONArray("GeoPt"));
        return localObject;
    }


    public static void saveJsonToFile(JSONArray jsonArray, String filePath) throws IOException {
        File file = new File(filePath);
        File dir = file.getParentFile();
        dir.mkdirs();
        file.createNewFile();
        FileWriter fileWriter = new  FileWriter(file);
        fileWriter.write(jsonArray.toString());
        fileWriter.close();
    }
}
