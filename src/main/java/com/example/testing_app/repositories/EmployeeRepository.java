package com.example.testing_app.repositories;

import com.example.testing_app.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity,Long> {
     List<EmployeeEntity> findByEmail(String email);
}
