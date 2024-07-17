package gift.configuration;

import gift.domain.member.ROLE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@EnableWebSecurity //스프링 시큐리티 활성화
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable);

        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/products/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/join/**", "/home/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(f -> {
                            f
                            .loginPage("/login")
                            .usernameParameter("email")
                            .defaultSuccessUrl("/products",true)
                            .permitAll();
                        }
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/error")
                .requestMatchers(toH2Console());
    }
}