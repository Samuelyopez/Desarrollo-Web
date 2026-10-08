package com.veterinaria.dogtor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:dogtortest")
class DogtorApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
