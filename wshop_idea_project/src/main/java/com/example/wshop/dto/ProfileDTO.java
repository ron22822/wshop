package com.example.wshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private Long profileid;
    private String firstname;
    private String lastname;
    private String birthday;
    private String gender;
}
