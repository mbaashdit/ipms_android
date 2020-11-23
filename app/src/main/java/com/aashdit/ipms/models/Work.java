package com.aashdit.ipms.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Manabendu on 17/06/20
 */
public class Work  implements Parcelable{
    public String financialYear;
    public String projectCode;
    public String schemeName;
    public String projectDescription;
    public String projectName;
    public String revisedEstimatedBudget;
    public String estimatedBudget;
    public String estmiatedStartDate;
    public String estimatedEndDate;
    public int projectId;

    public Work() {
    }

    protected Work(Parcel in) {
        financialYear = in.readString();
        projectCode = in.readString();
        schemeName = in.readString();
        projectDescription = in.readString();
        projectName = in.readString();
        revisedEstimatedBudget = in.readString();
        estimatedBudget = in.readString();
        estmiatedStartDate = in.readString();
        estimatedEndDate = in.readString();
        projectId = in.readInt();
    }

    public static final Creator<Work> CREATOR = new Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel in) {
            return new Work(in);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };

    public static Work parseProjects(JSONObject object){
        Work work = new Work();
        work.financialYear = object.optString("financialYear");
        work.projectCode = object.optString("projectCode");
        work.schemeName = object.optString("schemeName");
        work.estimatedBudget = object.optString("estimatedBudget");
        work.revisedEstimatedBudget = object.optString("revisedEstimatedBudget");
        work.projectDescription = object.optString("projectDescription");
        work.estmiatedStartDate = object.optString("estmiatedStartDate");
        work.estimatedEndDate = object.optString("estimatedEndDate");
        work.projectName = object.optString("projectName");
        work.projectId = object.optInt("projectId");

        return work;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(financialYear);
        dest.writeString(projectCode);
        dest.writeString(schemeName);
        dest.writeString(projectDescription);
        dest.writeString(projectName);
        dest.writeString(revisedEstimatedBudget);
        dest.writeString(estimatedBudget);
        dest.writeString(estmiatedStartDate);
        dest.writeString(estimatedEndDate);
        dest.writeInt(projectId);
    }
}
