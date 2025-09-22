package com.employee.system.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employee_profile")
public class EmployeeProfile {

    @Id
    private String id;

    private String firstname;
    private String lastname;
    private String department;
    private String designation;
    private long phone;
    private String address;
    private String r_created_by;
    private String r_modified_by;
    private LocalDateTime r_cre_time;
    private LocalDateTime r_mod_time;
    private boolean enabled =true;
    @Lob
//    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private SignUp user;
}
