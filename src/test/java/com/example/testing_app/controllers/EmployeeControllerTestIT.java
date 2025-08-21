package com.example.testing_app.controllers;

import com.example.testing_app.TestContainerConfiguration;
import com.example.testing_app.dtos.EmployeeDTO;
import com.example.testing_app.entities.EmployeeEntity;
import com.example.testing_app.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;


@AutoConfigureWebTestClient(timeout = "100000")
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
class EmployeeControllerIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity testEmployeeEntity;
    private EmployeeDTO testEmployeeDto;

    private Long id = 1L;


    @BeforeEach
    public void setUp(){
        testEmployeeEntity = EmployeeEntity
                .builder()
                .id(id)
                .name("ABC")
                .email("abc@gmail.com")
                .salary(1000L)
                .build();

        testEmployeeDto = EmployeeDTO
                .builder()
                .id(id)
                .name("ABC")
                .email("abc@gmail.com")
                .salary(10000L)
                .build();

        employeeRepository.deleteAll();
    }


    @Test
    public void testGetEmployeeById_Success(){
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployeeEntity);

        webTestClient
                .get()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());

    }


    @Test
    public void testGetEmployeeById_Failure(){

        webTestClient
                .get()
                .uri("/employees/{id}",id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreateNewEmployee_WhenEmployeeAlreadyExist_ThenThrowException(){
        EmployeeEntity employee = employeeRepository.save(testEmployeeEntity);

        webTestClient
                .post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    public void testCreateNewEmployee_WhenEmployeeDoseNotExist_ThenCreateNewEmployee(){
        webTestClient
                .post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }

    @Test
    public void testUpdateEmployee_WhenEmployeeDoesNotExist_ThenThrowException(){
        webTestClient
                .put()
                .uri("/employees/{id}",id)
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void testUpdateEmployee_WhenAttemptingToUpdateEmail_ThenThrowException(){
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployeeEntity);
        testEmployeeDto.setEmail("xyz@gmail.com");
        testEmployeeDto.setSalary(20000L);

        webTestClient
                .put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployeeEntity);
        testEmployeeDto.setName("XYZ");
        testEmployeeDto.setSalary(20000L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDTO.class)
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.delete()
                .uri("/employees/{id}",id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployeeEntity);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();
    }


}