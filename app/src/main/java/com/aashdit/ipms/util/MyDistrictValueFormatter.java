package com.aashdit.ipms.util;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Created by Manabendu on 02/09/20
 */
public class MyDistrictValueFormatter extends ValueFormatter {
    private final String[] districts = new String[]{
            "Anugul", "Boudh", "Balangir", "Bargarh", "Balasore", "Bhadrak", "Cuttack", "Deogarh", "Dhenkanal",
            "Ganjam", "Gajapati", "Jharsuguda", "Jajpur", "Jagatsinghapur", "Khordha", "Keonjhar", "Kalahandi",
            "Kandhamal", "Koraput", "Kendrapara", "Malkangiri", "Mayurbhanj", "Nabarangpur", "Nuapada",
            "Nayagarh", "Puri", "Rayagada", "Sambalpur", "Subarnapur", "Sundargarh"
    };
    private final BarLineChartBase<?> chart;

    public MyDistrictValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }


    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int distIndex = (int) value;
        String dist = "";
        if (axis instanceof XAxis) {
            switch (distIndex) {
                case 0:
                    dist = districts[0];
                    break;

                case 1:
                    dist = districts[1];
                    break;

                case 3:
                    dist = districts[3];
                    break;

                case 4:
                    dist = districts[4];
                    break;

                case 5:
                    dist = districts[5];
                    break;

                case 6:
                    dist = districts[6];
                    break;
                case 7:
                    dist = districts[7];
                    break;

                case 8:
                    dist = districts[8];
                    break;

                case 9:
                    dist = districts[9];
                    break;

                case 10:
                    dist = districts[10];
                    break;

                case 11:
                    dist = districts[11];
                    break;

                case 12:
                    dist = districts[12];
                    break;
                case 13:
                    dist = districts[13];
                    break;

                case 14:
                    dist = districts[14];
                    break;

                case 15:
                    dist = districts[15];
                    break;

                case 16:
                    dist = districts[16];
                    break;

                case 17:
                    dist = districts[17];
                    break;

                case 18:
                    dist = districts[18];
                    break;
                case 19:
                    dist = districts[19];
                    break;

                case 20:
                    dist = districts[20];
                    break;

                case 21:
                    dist = districts[21];
                    break;

                case 22:
                    dist = districts[22];
                    break;

                case 23:
                    dist = districts[23];
                    break;

                case 24:
                    dist = districts[24];
                    break;

                case 25:
                    dist = districts[25];
                    break;
                case 26:
                    dist = districts[26];
                    break;

                case 27:
                    dist = districts[27];
                    break;

                case 28:
                    dist = districts[28];
                    break;

                case 29:
                    dist = districts[29];
                    break;

            }
//            dist = districts[0];
        }
        return dist;
    }

    @Override
    public String getFormattedValue(float value) {

        int distIndex = (int) value;
        String dist = null;
        switch (distIndex) {
            case 0:
                dist = districts[0];
                break;

            case 1:
                dist = districts[1];
                break;

            case 3:
                dist = districts[3];
                break;

            case 4:
                dist = districts[4];
                break;

            case 5:
                dist = districts[5];
                break;

            case 6:
                dist = districts[6];
                break;
            case 7:
                dist = districts[7];
                break;

            case 8:
                dist = districts[8];
                break;

            case 9:
                dist = districts[9];
                break;

            case 10:
                dist = districts[10];
                break;

            case 11:
                dist = districts[11];
                break;

            case 12:
                dist = districts[12];
                break;
            case 13:
                dist = districts[13];
                break;

            case 14:
                dist = districts[14];
                break;

            case 15:
                dist = districts[15];
                break;

            case 16:
                dist = districts[16];
                break;

            case 17:
                dist = districts[17];
                break;

            case 18:
                dist = districts[18];
                break;
            case 19:
                dist = districts[19];
                break;

            case 20:
                dist = districts[20];
                break;

            case 21:
                dist = districts[21];
                break;

            case 22:
                dist = districts[22];
                break;

            case 23:
                dist = districts[23];
                break;

            case 24:
                dist = districts[24];
                break;

            case 25:
                dist = districts[25];
                break;
            case 26:
                dist = districts[26];
                break;

            case 27:
                dist = districts[27];
                break;

            case 28:
                dist = districts[28];
                break;

            case 29:
                dist = districts[29];
                break;

            case 30:
                dist = districts[30];
                break;

        }


        return dist;
    }
}
