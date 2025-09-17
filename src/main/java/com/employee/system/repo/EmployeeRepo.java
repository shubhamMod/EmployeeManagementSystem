package com.employee.system.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.system.model.SignUp;

@Repository
public interface EmployeeRepo extends JpaRepository<SignUp, Long> {

	SignUp findByEmailIgnoreCase(String username);

}