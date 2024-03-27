package com.codestep.TodoApp;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
public class TodoAppLoginTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TodoAppApplication controller;
	
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}
	
	@Test
	void DB上に存在するユーザはログインできる() throws Exception {
        this.mockMvc.perform(formLogin("/login")
                .user("taro")
                .password("yamada"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/secret"));
    }
	
	@Test
	void DB上に存在するユーザがパスワードを間違えるとログインできない() throws Exception {
        this.mockMvc.perform(formLogin("/login")
                .user("taro")
                .password("typo"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));
    }
	
	@Test
	void DB上に存在しないユーザはログインできない() throws Exception {
        this.mockMvc.perform(formLogin("/login")
                .user("user")
                .password("password"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));
    }
	
}
