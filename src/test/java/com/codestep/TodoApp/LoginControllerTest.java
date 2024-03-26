package com.codestep.TodoApp;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@SpringBootTest
class LoginControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TodoAppApplication controller;
	
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
		this.mockMvc.perform(get("/secret")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithAnonymousUser
	void 未認証ユーザーは管理者ページへアクセスできない() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithUserDetails(value="taro")
	void ログインしたユーザーはアプリケーションルートへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails(value="taro")
	void ログインしたユーザーはシークレットページへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/secret")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails(value="taro")
	void 一般ユーザーは管理者ページへアクセスできない() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails(value="admin")
	void 管理者は管理者ページへアクセスできる() throws Exception {
		this.mockMvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	
}
