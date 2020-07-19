package tacos.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests().
			antMatchers("/design","/orders").
				access("hasRole('ROLE_USER')").
					antMatchers("/","/**").
						access("permitAll").
							and().httpBasic();
	}
	/*
	 * //인메모리 사용자 스토어
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}password2")
			.authorities("ROLE_USER");
	}
	*/
	
	/*
	 * //JDBC 기반 사용자 스토어
	@Autowired
	DataSource dataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.jdbcAuthentication().dataSource(dataSource);
	}	
	 */
	 
	//JDBC 기반 사용자 스토어
	@Autowired
	DataSource dataSource;

	//	spring 내부 기본 지정된 sql 사용함
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		auth.jdbcAuthentication().dataSource(dataSource);
//	}	
//	
	// 쿼리 커스터마이징
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select username, password, enable from users where username = ?")
		.authoritiesByUsernameQuery("select username, authority from authorities where username = ?")
		.passwordEncoder(new BCryptPasswordEncoder());//패스워드 인코딩
	}	

}
