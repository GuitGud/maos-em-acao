package br.org.maosemacao.infrastructure;
import br.org.maosemacao.application.Store;
import br.org.maosemacao.domain.Volunteer;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
@Configuration
public class Security {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(Store store) {
        return email -> {
            Volunteer v=store.all(Volunteer.class).stream().filter(u->u.email().equalsIgnoreCase(email)).findFirst().orElseThrow(()->new UsernameNotFoundException("Credenciais inválidas"));
            return User.withUsername(v.email()).password(v.passwordHash()).roles(v.role()).build();
        };
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a->a.requestMatchers("/api/register","/api/csrf","/api/login","/","/index.html","/*.js","/*.css","/*.ico","/assets/**").permitAll().anyRequest().authenticated());
        http.csrf(c->c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
        http.formLogin(f->f.loginProcessingUrl("/api/login").usernameParameter("email")
            .successHandler((req,res,auth)->{res.setStatus(204);})
            .failureHandler((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"E-mail ou senha inválidos\"}");}));
        http.logout(l->l.logoutUrl("/api/logout").logoutSuccessHandler((req,res,auth)->res.setStatus(204)));
        http.exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->res.sendError(401)).accessDeniedHandler((req,res,ex)->res.sendError(403)));
        http.headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'")));
        return http.build();
    }
}
