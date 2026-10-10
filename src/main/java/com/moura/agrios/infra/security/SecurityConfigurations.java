package com.moura.agrios.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity 
public class SecurityConfigurations {

    @Autowired 
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(authorize -> authorize

    // Permite o encaminhamento para o tratamento de erros.
    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

    // Recursos estáticos
    .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()

    // Rotas de visualização (páginas HTML)
    .requestMatchers(HttpMethod.GET, "/", "/login", "/dashboard", "/clientes/**", "/servicos/**", "/produtos/**", "/maquinas/**", "/ordens-servico/**", "/contas-receber/**", "/usuarios/**").permitAll()

    // Login e registro
    .requestMatchers(HttpMethod.POST,"/auth/login","/auth/register").permitAll()

    // Cadastrar cliente: apenas aDMIN
    .requestMatchers(HttpMethod.POST,"/clientes").hasRole("ADMIN")

    // Cadastrar fazenda: apenas ADMIN
    .requestMatchers(HttpMethod.POST,"/clientes/*/fazendas").hasRole("ADMIN")

    // Atualizar cliente: apenas ADMIN
    .requestMatchers(HttpMethod.PUT,"/clientes/*").hasRole("ADMIN")

    // Atualizar fazenda: apenas ADMIN
    .requestMatchers(HttpMethod.PUT,"/clientes/*/fazendas/*").hasRole("ADMIN")

    // Cadastro servico - maquina - produto: apenas ADMIN
    .requestMatchers(HttpMethod.POST, "/servicos", "/maquinas", "/produtos").hasRole("ADMIN")

    // Atualizar servico - maquina - produto: apenas ADMIN
    .requestMatchers(HttpMethod.PUT, "/servicos/*", "/maquinas/*", "/produtos/*").hasRole("ADMIN")


    // OS

    // Cadastrar OS
    .requestMatchers(HttpMethod.POST,"/ordens-servico").hasRole("ADMIN")

    // Editar OS
    .requestMatchers(HttpMethod.PUT,"/ordens-servico/*").hasRole("ADMIN")

    // Finalizar OS
    .requestMatchers(HttpMethod.PATCH,"/ordens-servico/*/finalizar").hasRole("ADMIN")

    // Cancelar OS
    .requestMatchers(HttpMethod.PATCH,"/ordens-servico/*/cancelar").hasRole("ADMIN")


    // Criar Conta a Receber manual
    .requestMatchers(HttpMethod.POST,"/contas-receber").hasRole("ADMIN")

    // Registrar pagamento
    .requestMatchers(HttpMethod.POST, "/contas-receber/*/pagamentos").hasRole("ADMIN")

    // Todas as demais rotas exigem autenticação
    .anyRequest().authenticated()
)
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
