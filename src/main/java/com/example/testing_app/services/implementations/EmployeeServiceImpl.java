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
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees");
        List<EmployeeEntity> employees=employeeRepository.findAll();
        return employees.stream()
                .map(employeeEntity -> modelMapper.map(employeeEntity,EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) throws ResourceNotFoundException {
        log.info("Trying to fetch employee by id {}", id);
        EmployeeEntity employee=employeeRepository.findById(id)
                .orElseThrow(()->{
                    log.error("No employee found with id {}", id);
                    return new ResourceNotFoundException("No employee exists with id "+ id);
                });
        log.info("Successfully fetched the employee with id {}", id);
        return modelMapper.map(employee,EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) {
        log.info("trying to create a employee with email - {}", employeeDTO.getEmail());
        List<EmployeeEntity> existingEmployees=employeeRepository.findByEmail(employeeDTO.getEmail());
        if(!existingEmployees.isEmpty()){
            log.error("There already exists with the given email - "+employeeDTO.getEmail());
            throw new RuntimeException("There already exists with the given email - "+employeeDTO.getEmail());
        }
        EmployeeEntity employee=modelMapper.map(employeeDTO, EmployeeEntity.class);
        EmployeeEntity saved=employeeRepository.save(employee);
        log.info("Successfully created employee with email{}", employee.getEmail());
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("trying to update employee with id {}", id);
        boolean exists=employeeRepository.existsById(id);
        if(!exists){
            log.error("There is no employee with id {} - ",id);
            throw new RuntimeException("No employee exists with the given id - "+id);
        }
        EmployeeEntity employee=modelMapper.map(employeeDTO,EmployeeEntity.class);
        employee.setId(id);
        EmployeeEntity saved=employeeRepository.save(employee);
        log.info("Update employee with id - {} ",id);
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Trying to delete employee with id {}",id);
        boolean exists=employeeRepository.existsById(id);
        if(!exists){
            log.error("no employee with id {} - ",id);
            throw new RuntimeException("No employee exists with the given id - "+id);
        }
        employeeRepository.deleteById(id);
        log.info("Successfully deleted employee with id - {}" ,id);
    }
}
