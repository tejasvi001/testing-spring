package com.example.testing_app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Slf4j
//@SpringBootTest
class TestingAppApplicationTests {
	static int testNumber=0;
	@BeforeAll
	static void setUpOnce(){
		log.info("Setting Up the app");
		testNumber=0;
	}
	@AfterAll
	static void deleteOnce(){
		log.info("Freeing Up the app");
		testNumber=0;
	}
	@BeforeEach
	void setUp(){
		testNumber++;
        log.info("Setting Up the test case{}", testNumber);

	}
	@AfterEach
	 void delete(){
        log.info("Deallocating the testcase{}", testNumber);

	}
	@Test
	void contextLoads() {
		log.info("this is default test case");
	}

	@Test
	@DisplayName("sample test")
	void customTest(){
		int a=5,b=3;
		int result=sum(a,b);
		Assertions.assertEquals(8,result);

		 assertThat(result).isEqualTo(8);
	}
	@Test
	void testDivide(){
		int a=5,b=0;
		assertThatThrownBy(()->divide(a,b))
				.isInstanceOf(ArithmeticException.class);
	}

	int sum(int a,int b){
		return a+b;
	}
	int divide(int a,int b){
		try{
			return a/b;
		}catch(ArithmeticException e){
			log.error(e.getLocalizedMessage());
			throw new ArithmeticException(e.getLocalizedMessage());
		}
	}
}
