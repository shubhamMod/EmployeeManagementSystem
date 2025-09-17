package com.employee.system.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.employee.system.model.EmployeeProfile;
@Repository
public interface Profile extends JpaRepository<EmployeeProfile, String> {

    Page<EmployeeProfile> findByEnabledTrue(Pageable pageable);
    @Query(value = "SELECT id FROM employee_profile ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastEmployeeId();

}
