package academy.devdojo.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;

import academy.devdojo.webflux.service.DevDojoUserDetailsService;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(csrf -> csrf.disable())
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(HttpMethod.POST, "/animes/**").hasRole("ADMIN")
						.pathMatchers(HttpMethod.PUT, "/animes/**").hasRole("ADMIN")
						.pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN")
						.pathMatchers(HttpMethod.GET, "/animes/**").hasRole("USER")
						.pathMatchers("/webjars/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
						.permitAll()
						.anyExchange().authenticated())
				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	@Bean
	ReactiveAuthenticationManager authenticationManager(DevDojoUserDetailsService devDojoUserDetailsService) {
		return new UserDetailsRepositoryReactiveAuthenticationManager(devDojoUserDetailsService);
	}
}
