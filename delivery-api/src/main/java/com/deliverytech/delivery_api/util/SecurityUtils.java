package com.deliverytech.delivery_api.util;

import com.deliverytech.delivery_api.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class SecurityUtils {

    public Usuario getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return (Usuario) auth.getPrincipal();
    }

    public Long getCurrentUserId() {
        Usuario user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }

    public boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
