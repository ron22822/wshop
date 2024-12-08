package com.example.wshop;

import com.example.wshop.model.Role;
import com.example.wshop.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class WshopApplicationTests {

	@Test
	void contextLoads() {
	}

}
