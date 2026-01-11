package com.class_manager.class_responsibility_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class ClassResponsibilityServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testMainMethod() {
		// Given - Mock SpringApplication to prevent actual application startup
		try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
			// Mock the run method to return null (ApplicationContext)
			mockedSpringApplication.when(() -> SpringApplication.run(
					eq(ClassResponsibilityServiceApplication.class), 
					any(String[].class)))
					.thenReturn(null);

			// When - Call the main method
			String[] args = new String[]{};
			ClassResponsibilityServiceApplication.main(args);

			// Then - Verify that SpringApplication.run was called with correct arguments
			mockedSpringApplication.verify(() -> SpringApplication.run(
					eq(ClassResponsibilityServiceApplication.class),
					eq(args)));
		}
	}

	@Test
	void testMainMethod_WithArguments() {
		// Given - Mock SpringApplication with command line arguments
		try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
			mockedSpringApplication.when(() -> SpringApplication.run(
					eq(ClassResponsibilityServiceApplication.class),
					any(String[].class)))
					.thenReturn(null);

			// When - Call main with arguments
			String[] args = new String[]{"--spring.profiles.active=test"};
			ClassResponsibilityServiceApplication.main(args);

			// Then - Verify SpringApplication.run was called
			mockedSpringApplication.verify(() -> SpringApplication.run(
					eq(ClassResponsibilityServiceApplication.class),
					eq(args)));
		}
	}

}
