package com.aashdit.ipms.models;

import org.json.JSONObject;

/**
 * Created by Manabendu on 06/08/20
 */
public class ProgressableWork {
    public String duration;
    public String estimatedStartDate;
    public String workComponentName;
    public String estimatedEndDate;
    public int workId;
    public int phaseCount;

    public static ProgressableWork parseProgressableWork(JSONObject object){
        ProgressableWork pw = new ProgressableWork();
        pw.duration = object.optString("duration");
        pw.estimatedStartDate = object.optString("estimatedStartDate");
        pw.workComponentName = object.optString("workComponentName");
        pw.estimatedEndDate = object.optString("estimatedEndDate");
        pw.workId = object.optInt("workId");
        pw.phaseCount = object.optInt("phaseCount");
        return pw;
    }
}
