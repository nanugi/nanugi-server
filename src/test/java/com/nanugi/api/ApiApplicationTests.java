package com.nanugi.api;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.repo.PostJpaRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberJpaRepo memberJpaRepo;
	@Autowired
	private PostJpaRepo postJpaRepo;

	@Test
	void contextLoads() {
	}

	@Test
	void checkTestDataEnabled(){
		Member member = memberJpaRepo.findById(1L).orElseThrow(CUserNotFoundException::new);
		assertEquals("나누기", member.getNickname());
		assertTrue(member.getIsVerified());
		assertEquals(26, postJpaRepo.findAll().size());
	}

	@Test
	void findAllPosts() throws Exception {
		mockMvc.perform(get("/v1/posts?page=0"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("success").value(true))
				.andExpect(jsonPath("data").hasJsonPath());
	}

}
