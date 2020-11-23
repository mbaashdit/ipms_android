package com.aashdit.ipms.models;

/**
 * Created by Manabendu on 02/09/20
 */
public class Dashboard {
    private int dashId;
    private String dashName;
    private int dashCount;

    public Dashboard() {
    }

    public int getDashId() {
        return dashId;
    }

    public void setDashId(int dashId) {
        this.dashId = dashId;
    }

    public String getDashName() {
        return dashName;
    }

    public void setDashName(String dashName) {
        this.dashName = dashName;
    }

    public int getDashCount() {
        return dashCount;
    }

    public void setDashCount(int dashCount) {
        this.dashCount = dashCount;
    }
}
