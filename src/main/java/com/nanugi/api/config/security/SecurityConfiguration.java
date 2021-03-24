package com.nanugi.api.config.security;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.entity.Post;
import com.nanugi.api.repo.PostJpaRepo;
import com.nanugi.api.repo.MemberJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Random;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberJpaRepo userJpaRepo;
    private final PostJpaRepo postJpaRepo;
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

    @Bean
    public FilterRegistrationBean processCorsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("X-AUTH-TOKEN");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);


        final FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/*/signin", "/*/signin/**", "/*/signup", "/*/signup/**", "/*/email-verification/**", "/*/set-new-password/**", "/*/send-certcode/**", "/*/posts").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .antMatchers(HttpMethod.GET, "/exception/**", "/helloworld/**","/actuator/health", "/favicon.ico").permitAll() // 등록된 GET요청 리소스는 누구나 접근가능
                    .antMatchers(HttpMethod.POST, "/*/board/*").hasRole("ADMIN")
                    .anyRequest().hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
            .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 넣어라.


        Member test_user;
        try{

            test_user = Member
                    .build(testUserId,
                            "나누기",
                            "",
                            passwordEncoder.encode(testUserPassword));
            test_user.addRole("ROLE_USER");
            test_user.setIsVerified(true);
            userJpaRepo.save(test_user);

            Member admin_user = Member.build(adminUserId, "관리자", "", passwordEncoder.encode(adminUserPassword));
            admin_user.addRole("ADMIN_USER");
            admin_user.setIsVerified(true);
            userJpaRepo.save(admin_user);
        }
        catch (Exception e){
            System.out.println(e.getClass()+e.getMessage());
            test_user = userJpaRepo.findByNickname("나누기").orElseThrow(CUserNotFoundException::new);
        }

        Random random = new Random();

        try{
            if(postJpaRepo.findAll().size() < 20){
                Post post = Post.build(test_user,
                        "나누기를 시작하세요!",
                        "새로운 나누기 글을 생성하세요!\n버그가 발생하는 경우, 글 삭제를 원하는 경우 고객문의를 이용해주세요.\n",
                        0,
                        99999,
                        0,
                        "");
                postJpaRepo.save(post);

                for(int i=0;i<25; i++){
                    post = Post.build(test_user,
                            "Testing Post " + (i+1),
                            "This is just a random post passing by...",
                            random.nextInt(40000),
                            random.nextInt(10)+5,
                            random.nextInt(3)+1,
                            "http://openchat.com/1");
                    postJpaRepo.save(post);
                    sleep(1000);
                }
            }
        }
        catch(Exception e){
            System.out.println(e.getClass() + e.getMessage());
        }

    }

    @Override // ignore swagger security config
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**").and().ignoring().antMatchers(HttpMethod.OPTIONS);
    }
}
