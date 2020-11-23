package com.aashdit.ipms.models;

import org.json.JSONObject;

/**
 * Created by Manabendu on 31/07/20
 */
public class WorkPhase {
    public String duration;
    public String workPhaseName;
    public String estimatedStartDate;
    public String estimatedEndDate;
    public String status;
    public String actualProgress;
    public String currentStatus;
    public String progress;
    public String remarks;
    public int workPhaseId;
    public int workPlanId;
    public int workId;


    public static WorkPhase parsePlansByWorkId(JSONObject object){
        WorkPhase wp = new WorkPhase();
        wp.duration = object.optString("duration");
        wp.workPhaseName = object.optString("workPhaseName");
        wp.estimatedStartDate = object.optString("estimatedStartDate");
        wp.estimatedEndDate = object.optString("estimatedEndDate");
        wp.status = object.optString("status");
        wp.actualProgress = object.optString("actualProgress");
        wp.currentStatus = object.optString("currentStatus");
        wp.progress = object.optString("progress");
        wp.remarks = object.optString("remarks");
        wp.workPhaseId = object.optInt("workPhaseId");
        wp.workPlanId = object.optInt("workPlanId");
        wp.workId = object.optInt("workId");
        return wp;
    }
}
