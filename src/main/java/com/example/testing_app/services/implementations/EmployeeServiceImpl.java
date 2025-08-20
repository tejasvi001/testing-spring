package com.example.testing_app.services.implementations;

import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.entities.EmployeeEntity;
import com.example.testing_app.exceptions.ResourceNotFoundException;
import com.example.testing_app.repositories.EmployeeRepository;
import com.example.testing_app.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl  {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    public EmployeeDTO getEmployeeById(Long id) throws ResourceNotFoundException {
        log.info("Fetching employee with id: {}", id);
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });
        log.info("Successfully fetched employee with id: {}", id);
        return modelMapper.map(employee, EmployeeDTO.class);
    }


    public EmployeeDTO createNewEmployee(EmployeeDTO employeeDto) {
        log.info("Creating new employee with email: {}", employeeDto.getEmail());
        List<EmployeeEntity> existingEmployees = employeeRepository.findByEmail(employeeDto.getEmail());

        if (!existingEmployees.isEmpty()) {
            log.error("Employee already exists with email: {}", employeeDto.getEmail());
            throw new RuntimeException("Employee already exists with email: " + employeeDto.getEmail());
        }
        EmployeeEntity newEmployee = modelMapper.map(employeeDto, EmployeeEntity.class);
        EmployeeEntity savedEmployee = employeeRepository.save(newEmployee);
        log.info("Successfully created new employee with id: {}", savedEmployee.getId());
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }


    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDto) throws ResourceNotFoundException {
        log.info("Updating employee with id: {}", id);
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        if (!employee.getEmail().equals(employeeDto.getEmail())) {
            log.error("Attempted to update email for employee with id: {}", id);
            throw new RuntimeException("The email of the employee cannot be updated");
        }

        modelMapper.map(employeeDto, employee);
        employee.setId(id);

        EmployeeEntity savedEmployee = employeeRepository.save(employee);
        log.info("Successfully updated employee with id: {}", id);
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }


    public void deleteEmployee(Long id) throws ResourceNotFoundException {
        log.info("Deleting employee with id: {}", id);
        boolean exists = employeeRepository.existsById(id);
        if (!exists) {
            log.error("Employee not found with id: {}", id);
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
        log.info("Successfully deleted employee with id: {}", id);
    }
}
