package com.aashdit.ipms.models;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Manabendu on 24/08/20
 */
public class WorkIdOffiles extends RealmObject {
    private RealmList<Integer> workIds;

    public WorkIdOffiles() {
    }

    public WorkIdOffiles(RealmList<Integer> workIds) {
        this.workIds = workIds;
    }

    public RealmList<Integer> getWorkIds() {
        return workIds;
    }

    public void setWorkIds(RealmList<Integer> workIds) {
        this.workIds = workIds;
    }
}
