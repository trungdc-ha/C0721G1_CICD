package vn.codegym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import vn.codegym.services.impl.MyUserDetailServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailServiceImpl myUserDetailService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*Cài đặt cách lấy thông tin UserDetail, cơ chế Encode pass..*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService).passwordEncoder(passwordEncoder());
    }

    /*Cấu hình security bằng HTTP basic*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic().disable();
        http.csrf().disable()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/student").permitAll() //khi login bang /login thi khi thanh cong se vao /student
        .and()
                /*
                 * Đã login
                 *  /student : ADMIN, USER
                 *  /create : ADMIN
                 * Chưa login
                 *  home page
                 * */
                .authorizeRequests().antMatchers("/home").permitAll()
//                .antMatchers("/student").hasRole("ADMIN")
//                .antMatchers("/student").hasRole("USER")
                .antMatchers("/student").hasAnyRole("ADMIN","USER")
                .antMatchers("/student/create").hasRole("ADMIN")
                .anyRequest().authenticated(); // tat ca request goi len server deu phai login


        http.authorizeRequests().and().rememberMe()
                .tokenRepository(this.persistentTokenRepository()).tokenValiditySeconds(60*60*10);

    }

    /*Cau hinh noi luu tri thong tin remember me*/
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        InMemoryTokenRepositoryImpl memoryTokenRepository = new InMemoryTokenRepositoryImpl();
        return memoryTokenRepository;
    }

//    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic().disable();
//    }
}
