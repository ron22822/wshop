package com.example.wshop.constant;

public enum ActivityEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String activity;

    ActivityEnum (String activity){
        this.activity = activity;
    }

    public String getString(){
        return activity;
    }
}
