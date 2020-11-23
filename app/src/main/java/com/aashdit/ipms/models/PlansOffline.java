package com.aashdit.ipms.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manabendu on 24/08/20
 */
public class PlansOffline extends RealmObject {

    @PrimaryKey()
    private Long id;
    private String latitude;
    private String longitude;
    private boolean syncStatus;
    private String data;
//    private RealmList<Integer > workIds;
//    private FileOffline file;

    public PlansOffline() {
    }

    public PlansOffline(Long id, String latitude, String longitude, boolean syncStatus, String data /*RealmList<Integer > workIds, FileOffline file*/) {
        this.latitude = latitude;
        this.longitude = longitude;
//        this.workIds = workIds;
        this.syncStatus = syncStatus;
//        this.file = file;
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.syncStatus = syncStatus;
    }


//    public FileOffline getFile() {
//        return file;
//    }
//
//    public void setFile(FileOffline file) {
//        this.file = file;
//    }
}
