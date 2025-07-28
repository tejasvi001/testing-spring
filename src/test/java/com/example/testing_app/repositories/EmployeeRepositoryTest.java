package com.example.testing_app.repositories;

import com.example.testing_app.TestContainerConfiguration;
import com.example.testing_app.entities.EmployeeEntity;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@DataJpaTest

//@TestPropertySource(locations = "classpath:application-test.properties")
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    private EmployeeEntity employee;
    @BeforeEach
    void setUp(){
        employee=EmployeeEntity.builder()
                .email("anuj@gmail.com")
                .name("anuj")
                .salary(1000L)
                .build();
    }
    @Test
    void testFindByEmail_whenEmailIsValid_thenReturnEmployee() {
        //arrange, given
        employeeRepository.save(employee);
        //act, when
        List<EmployeeEntity> list=employeeRepository.findByEmail(employee.getEmail());
        //assert, then
        assertThat(list).isNotNull();
        assertThat(list.isEmpty()).isEqualTo(false);
        assertThat(list.get(0).getEmail()).isEqualTo(employee.getEmail());
    }
    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList(){
        //arrange, given
        String email="anyemail@gmail.com";
        //act, when
        List<EmployeeEntity> list=employeeRepository.findByEmail(email);
        //assert, then
        assertThat(list).isNotNull();
        assertThat(list.isEmpty()).isEqualTo(true);
    }
}