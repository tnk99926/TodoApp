package com.codestep.TODOapp;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;



@WebMvcTest(TODOappController.class)
@Import(TODOappTestSecurityConfig.class) 
class TODOappApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TODOappApplication controller;
	
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}
	
	@Test
	@WithAnonymousUser
	void 未認証ユーザーはアプリケーションルートへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk());
	}
	
	@Test
	@WithAnonymousUser
	void 未認証ユーザーはシークレットページへアクセスできない() throws Exception {
		this.mockMvc.perform(get("/secret")).andExpect(status().isFound());
	}
	
	@Test
	@WithAnonymousUser
	void 未認証ユーザーは管理者ページへアクセスできない() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(roles= {"USER","ADMIN"})
	void ログインしたユーザーはアプリケーションルートへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles= {"USER","ADMIN"})
	void ログインしたユーザーはシークレットページへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/secret")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles= {"USER"})
	void 一般ユーザーは管理者ページへアクセスできない() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(roles= {"ADMIN"})
	void 管理者は管理者ページへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	
}
