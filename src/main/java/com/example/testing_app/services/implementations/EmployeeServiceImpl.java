package com.example.testing_app.services.implementations;

import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.entities.EmployeeEntity;
import com.example.testing_app.repositories.EmployeeRepository;
import com.example.testing_app.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<EmployeeEntity> employees=employeeRepository.findAll();
        return employees.stream()
                .map(employeeEntity -> modelMapper.map(employeeEntity,EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        return modelMapper.map(employeeRepository.findById(id), EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) {
        EmployeeEntity employee=modelMapper.map(employeeDTO, EmployeeEntity.class);
        EmployeeEntity saved=employeeRepository.save(employee);
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        EmployeeEntity employee=modelMapper.map(employeeDTO,EmployeeEntity.class);
        employee.setId(id);
        EmployeeEntity saved=employeeRepository.save(employee);
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        boolean exists=employeeRepository.existsById(id);
        if(exists){
            employeeRepository.deleteById(id);
        }
    }
}
