package com.example.wshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "\"Profile\"")
public class Profile {
    @Id
    private Long profileid;

    @OneToOne
    @JoinColumn(name = "userid")
    private User user;

    private String firstname;
    private String lastname;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(length = 50)
    private String gender;
}
