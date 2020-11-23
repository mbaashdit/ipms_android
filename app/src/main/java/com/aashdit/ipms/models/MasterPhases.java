package com.aashdit.ipms.models;

import org.json.JSONObject;

/**
 * Created by Manabendu on 06/08/20
 */
public class MasterPhases {
    public String phaseDesc;
    public int phaseId;
    public String phaseCode;
    public boolean isActive;
    public String phaseName;

    public static MasterPhases parseMasterPhase(JSONObject object){
        MasterPhases mp = new MasterPhases();
        mp.isActive = object.optBoolean("isActive");
        mp.phaseDesc = object.optString("phaseDesc");
        mp.phaseId = object.optInt("phaseId");
        mp.phaseCode= object.optString("phaseCode");
        mp.phaseName= object.optString("phaseName");

        return mp;
    }
}
