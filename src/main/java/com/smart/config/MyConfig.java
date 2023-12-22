package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MyConfig {

	@Bean
	UserDetailsService getUserDetailsService() {

		return new userDetailServiceImpl();

	}

	@Bean
	PasswordEncoder passwordEncoder1() {

		return new BCryptPasswordEncoder();

	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration ac) throws Exception {

		return ac.getAuthenticationManager();

	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoauthenticationProvider = new DaoAuthenticationProvider();
		daoauthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoauthenticationProvider.setPasswordEncoder(passwordEncoder1());

		return daoauthenticationProvider;

	}

	@SuppressWarnings("removal")
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
		    .authorizeHttpRequests()
		    .requestMatchers("/admin/**")
		    .hasRole("ADMIN")
			.requestMatchers("/user/**")
			.hasRole("USER")
			.requestMatchers("/**")
			.permitAll().anyRequest()
			.authenticated().and()
			.formLogin(form -> form.loginPage("/signin")
			.defaultSuccessUrl("/user/index")
			.failureUrl("/signin?error=true").permitAll());
		

		// http.csrf().disable().authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
//		.requestMatchers("/user/**").hasRole("USER").requestMatchers("/**").permitAll().anyRequest()
//		.authenticated().and().formLogin();

//		http.formLogin(form -> form.loginPage("/signin")
//
//				.defaultSuccessUrl("/user/index")
//				.failureUrl("/signin?error=true")
//				.permitAll());
		http.authenticationProvider(authenticationProvider());

		return http.build();
	}

}
