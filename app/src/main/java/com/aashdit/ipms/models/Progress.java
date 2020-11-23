package com.aashdit.ipms.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Manabendu on 01/08/20
 */
public class Progress {
    public String date;
    public String reason;
    public String status;
    public String actualProgress;
    public String latitude;
    public String longitude;
    public ArrayList<String> photos;

    public Progress() {
    }

    public static Progress parseProgress(JSONObject object) {
        Progress progress = new Progress();
        progress.reason = object.optString("reason");
        progress.date = object.optString("date");
        progress.status = object.optString("status");
        progress.latitude = object.optString("latitude");
        progress.longitude = object.optString("longitude");
        progress.actualProgress = object.optString("actualProgress");
        JSONArray imageArray = object.optJSONArray("photos");
        if (imageArray != null && imageArray.length() > 0){
            progress.photos = new ArrayList<>();
            for (int i = 0; i <imageArray.length() ; i++) {
                progress.photos.add(imageArray.optString(i));
            }
        }

        return progress;
    }

}
