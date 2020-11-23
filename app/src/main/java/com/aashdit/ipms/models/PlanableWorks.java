package com.aashdit.ipms.models;

import org.json.JSONObject;

/**
 * Created by Manabendu on 04/08/20
 */
public class PlanableWorks {
    public String duration;
    public String estimatedStartDate;
    public String estimatedEndDate;
    public String workComponentName;
    public String currentStatus;
//    public int totalDuration;
    public int workId;
    public int phaseCount;

    public static PlanableWorks parsePlannableWork(JSONObject object) {
        PlanableWorks pw = new PlanableWorks();
        pw.duration = object.optString("duration");
        pw.estimatedStartDate = object.optString("estimatedStartDate");
        pw.workComponentName = object.optString("workComponentName");
        pw.estimatedEndDate = object.optString("estimatedEndDate");
        pw.currentStatus = object.optString("currentStatus");
//        pw.totalDuration = object.optInt("totalDuration");
        pw.workId = object.optInt("workId");
        pw.phaseCount = object.optInt("phaseCount");
        return pw;
    }

}
