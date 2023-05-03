package com.miral.nfcdetector.features.nfc.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NfcData extends RealmObject {

    @PrimaryKey
    private int id;
    private String uid;
    private String command;
    private String result;

    public NfcData() {
    }

    public NfcData(String uid, String command, String result) {
        this.uid = uid;
        this.command = command;
        this.result = result;
    }

    public NfcData(int id, String uid, String command, String result) {
        this.id = id;
        this.uid = uid;
        this.command = command;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
