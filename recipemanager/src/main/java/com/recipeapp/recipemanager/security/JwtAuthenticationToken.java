package com.recipeapp.recipemanager.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String token;

    // stores user details and JWT token
    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    // return token when needed
    @Override
    public Object getCredentials(){
        return token;
    }

    // return user when needed
    @Override
    public Object getPrincipal(){
        return principal;
    }
}
