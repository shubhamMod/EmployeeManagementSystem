package com.employee.system.model;

import java.time.LocalDateTime;
import java.util.*;


import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="UserData")
public class SignUp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String firstname;
	private String lastname;
	private String password;
	@Column(nullable = false,unique = true)
	private String email;
	private boolean enabled =true;
	private LocalDateTime 	r_cre_time;
	private String r_created_by;
	private LocalDateTime r_mod_time;
	private String 	r_modified_by;
	
	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),   
        inverseJoinColumns = @JoinColumn(name = "role_id") 
    )
    private List<Role> roles ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<EmployeeProfile> employeeProfiles;
	
	

	
}
