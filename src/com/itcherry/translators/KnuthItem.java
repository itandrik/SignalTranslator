package com.itcherry.translators;


public class KnuthItem {
    private String operationAddress;
    private String operationCode;
    private int at; //-2 : true
    private int af; //-1 : false

    public KnuthItem(String operationAddress, String operationCode, int at, int af) {
        this.operationAddress = operationAddress;
        this.operationCode = operationCode;
        this.at = at;
        this.af = af;
    }

    public String getOperationAddress() {
        return operationAddress;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public int getAt() {
        return at;
    }

    public int getAf() {
        return af;
    }
}
