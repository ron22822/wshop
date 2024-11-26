package com.example.wshop.constant;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String rolename;

    RoleEnum(String rolename){
        this.rolename = rolename;
    }

    public String getString(){
        return rolename;
    }
}
