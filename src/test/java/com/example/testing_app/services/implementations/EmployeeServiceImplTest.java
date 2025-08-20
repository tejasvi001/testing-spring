package com.example.testing_app.services.implementations;

import com.example.testing_app.TestContainerConfiguration;
import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.entities.EmployeeEntity;
import com.example.testing_app.exceptions.ResourceNotFoundException;
import com.example.testing_app.repositories.EmployeeRepository;
import com.example.testing_app.services.implementations.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //to use docker container only
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;    //to create mock

    @Spy
    private ModelMapper modelMapper;


    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeEntity mockEmployeeEntity;
    private EmployeeDTO mockEmployeeDto;

    private Long id = 1L;

    //Create an employee
    @BeforeEach
    void setup(){
        mockEmployeeEntity = EmployeeEntity
                .builder()
                .id(id)
                .name("Alic")
                .email("alice@gmail.com")
                .salary(100000l)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployeeEntity,EmployeeDTO.class);
    }

    // Test getEmployeeById() when EmployeeId is present then return EmployeeDto
    @Test
    public void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto() throws ResourceNotFoundException {

        //assign
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployeeEntity));  // to stub mock

        //act
        EmployeeDTO employeeDto = employeeService.getEmployeeById(1L);
        //assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(mockEmployeeEntity.getId());
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeEntity.getEmail());

        verify(employeeRepository,only()).findById(id);     //to verify mock

    }

    // Test getEmployeeById() when EmployeeId is not present then throw exception
    @Test
    public void testGetEmployeeById_WhenEmployeeIdIsNotPresent_ThenThrowException(){

        //assign
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);


    }

    // Test createNewEmployee() when employee valid then create new employee
    @Test
    public void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){

        //arrange
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(mockEmployeeEntity);


        //act
        EmployeeDTO employeeDto = employeeService.createNewEmployee(mockEmployeeDto);  //to stub mock

        //assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

        ArgumentCaptor<EmployeeEntity> employeeEntityArgumentCaptor = ArgumentCaptor.forClass(EmployeeEntity.class);  //argument captor

        verify(employeeRepository).save(employeeEntityArgumentCaptor.capture());    //to verify mock

        EmployeeEntity captorEmployeeEntity = employeeEntityArgumentCaptor.getValue();
        assertThat(captorEmployeeEntity.getEmail()).isEqualTo(mockEmployeeEntity.getEmail());

    }

    // Test createNewEmployee() when attempting to create employee with existing email then throw exception
    @Test
    public void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException() {
        //arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployeeEntity));

        //act and assert

        assertThatThrownBy(()-> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployeeEntity.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository,never()).save(any());

    }

    // Test update employee when employee does not exist then throw exception
    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        //arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    // Test update employee when attempting to update email then throw exception
    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException() {  //7.6
        //arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployeeEntity));
        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setEmail("random@gmail.com");

        //act and assert
        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository, never()).save(any());

    }

    // Test update employee when valid employee then update employee
    @Test
    public void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee() throws ResourceNotFoundException {  //7.6
        //        arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployeeEntity));
        mockEmployeeDto.setName("Random name");
        mockEmployeeDto.setSalary(299000l);


        EmployeeEntity newEmployee = modelMapper.map(mockEmployeeDto, EmployeeEntity.class);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(newEmployee);

        //act
        EmployeeDTO updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto);

        //assert
        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);  //for this we have to add hashCode and equals method in our dto class

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());


    }

    //Test delete employee when employee does not exist then throw exception
    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {   //7.6

        //arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);

        //act and assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + 1L);

        verify(employeeRepository, never()).deleteById(anyLong());
    }


    // Test delete employee when employee
    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {    //7.6
        //        arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        //act and assert
        assertThatCode(() -> employeeService.deleteEmployee(1L))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }

}