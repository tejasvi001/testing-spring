package com.example.testing_app.services.implementations;

import com.example.testing_app.TestContainerConfiguration;
import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.entities.EmployeeEntity;
import com.example.testing_app.exceptions.ResourceNotFoundException;
import com.example.testing_app.repositories.EmployeeRepository;
import com.example.testing_app.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private EmployeeEntity mockEmployee;
    private EmployeeDTO mockEmployeeDto;
    @BeforeEach
    void setUp(){
        mockEmployee= EmployeeEntity.builder()
                .id(1L).
                name("anuj").
                email("anuj@email.com").
                salary(200L).
                build();
        mockEmployeeDto=modelMapper.map(mockEmployee, EmployeeDTO.class);
    }
    @Test
    void testGetEmployeeByID_WhenIdIsPresent_ThenReturnEmployeeDTO() throws ResourceNotFoundException {
        //arrange
        Long id=mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));
        // act
        EmployeeDTO employeeDTO=employeeService.getEmployeeById(id);


        //assert

        assertThat(employeeDTO.getId()).isEqualTo(id);
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployee.getEmail());
        assertThat(employeeDTO.getName()).isEqualTo(mockEmployee.getName());
        assertThat(employeeDTO.getSalary()).isEqualTo(mockEmployee.getSalary());

        verify(employeeRepository).findById(id);
    }

    // Test getEmployeeById() when EmployeeId is not present then throw exception
    @Test
    public void testGetEmployeeById_WhenEmployeeIdIsNotPresent_ThenThrowException(){

        //assign
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No employee exists with id 1");

        verify(employeeRepository).findById(1L);


    }


    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenReturnEmployeeDTO(){
        //assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(mockEmployee);


        //act
        EmployeeDTO employeeDTO=employeeService.createNewEmployee(mockEmployeeDto);


        //assert



        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployeeDto.getEmail());
        assertThat(employeeDTO.getName()).isEqualTo(mockEmployeeDto.getName());
        assertThat(employeeDTO.getSalary()).isEqualTo(mockEmployeeDto.getSalary());
        ArgumentCaptor<EmployeeEntity> employeeEntityArgumentCaptor=ArgumentCaptor.forClass(EmployeeEntity.class);
        verify(employeeRepository).save(employeeEntityArgumentCaptor.capture());
        EmployeeEntity capturedEmployee=employeeEntityArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployeeDto.getEmail());
        assertThat(capturedEmployee.getName()).isEqualTo(mockEmployeeDto.getName());
        assertThat(capturedEmployee.getSalary()).isEqualTo(mockEmployeeDto.getSalary());

    }

    @Test
    public void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException() {
        //arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        //act and assert

        assertThatThrownBy(()-> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("There already exists with the given email - "+mockEmployee.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository,never()).save(any());

    }


}