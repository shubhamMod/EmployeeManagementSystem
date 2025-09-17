package com.employee.system.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
//	private LocalDateTime r_cre_time;
//	private String r_created_by;
//	private LocalDateTime r_mod_time;
//	private String r_modified_by;
	@ManyToMany(mappedBy = "roles") 
	@JsonIgnore
    private List<SignUp> users;
	

}
