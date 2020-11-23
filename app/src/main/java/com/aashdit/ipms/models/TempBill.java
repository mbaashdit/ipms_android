package com.aashdit.ipms.models;

/**
 * Created by Manabendu on 17/06/20
 */
public class TempBill {
    public String title;
    public String invoice;
    public String amount;

    public TempBill(String title, String invoice, String amount) {
        this.title = title;
        this.invoice = invoice;
        this.amount = amount;
    }
}
