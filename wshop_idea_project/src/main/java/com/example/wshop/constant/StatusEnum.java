package com.example.wshop.constant;

public enum StatusEnum {
    CREATED("CREATED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String statename;

    StatusEnum(String statename){
        this.statename = statename;
    }

    public String getString(){
        return statename;
    }
}
