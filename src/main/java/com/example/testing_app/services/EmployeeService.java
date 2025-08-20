package com.example.testing_app.services;

import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.exceptions.ResourceNotFoundException;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Long id) throws ResourceNotFoundException;

    EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) throws ResourceNotFoundException;

    void deleteById(Long id);
}
