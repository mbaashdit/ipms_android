package com.aashdit.ipms.models;

import org.json.JSONArray;

import io.realm.RealmObject;

/**
 * Created by Manabendu on 24/08/20
 */
public class FileOffline extends RealmObject {
    private String fileName;
    private byte[] fileData;

    public FileOffline() {
    }

    public FileOffline(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
