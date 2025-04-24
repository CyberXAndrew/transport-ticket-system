package com.github.cyberxandrew.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthorizationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        String json = String.format("{\"status\": %d, \"error\": \"Forbidden\", \"message\": \"%s\"," +
                " \"path\": \"%s\"}", HttpServletResponse.SC_FORBIDDEN, ex.getMessage(), request.getRequestURI());
        response.getWriter().write(json);
    }
}
