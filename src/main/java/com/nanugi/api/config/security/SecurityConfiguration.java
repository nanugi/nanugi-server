package com.nanugi.api.config.security;

import com.nanugi.api.entity.User;
import com.nanugi.api.repo.UserJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserJpaRepo userJpaRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${test.user.id}")
    private String testUserId;

    @Value("${test.user.password}")
    private String testUserPassword;

    @Value("${admin.user.id}")
    private String adminUserId;

    @Value("${admin.user.password}")
    private String adminUserPassword;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                    .antMatchers("/*/signin", "/*/signin/**", "/*/signup", "/*/signup/**", "/*/email-verification/**").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .antMatchers(HttpMethod.GET, "/exception/**", "/helloworld/**","/actuator/health", "/v1/board/**", "/favicon.ico").permitAll() // 등록된 GET요청 리소스는 누구나 접근가능
                    .antMatchers(HttpMethod.POST, "/*/board/*").hasRole("ADMIN")
                    .anyRequest().hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
            .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 넣어라.


    }

    @Override // ignore swagger security config
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**");

        userJpaRepo.save(User.builder()
                .uid(testUserId)
                .password(passwordEncoder.encode(testUserPassword))
                .name("test user")
                .isVerified(true)
                .verifyCode("")
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        userJpaRepo.save(User.builder()
                .uid(adminUserId)
                .password(passwordEncoder.encode(adminUserPassword))
                .name("admin user")
                .isVerified(true)
                .verifyCode("")
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .build());

    }
}
