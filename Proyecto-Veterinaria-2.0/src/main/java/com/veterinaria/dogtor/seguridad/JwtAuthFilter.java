package com.veterinaria.dogtor.seguridad;

import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resuelve el usuario autenticado a partir de un JWT (header Authorization
 * o parámetro _token, usado por los formularios) y lo deja disponible como
 * atributos de request "rol" / "usuarioLogueado" — mismos nombres que antes
 * poblaba HttpSession, pero por request en vez de por sesión de servidor,
 * para que cada pestaña del navegador (con su propio token en sessionStorage)
 * pueda estar autenticada como un usuario distinto.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final DuenoService duenoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extraerToken(request);
        JwtService.Claims claims = jwtService.validar(token);

        if (claims != null) {
            Usuario usuario = usuarioService.findByCorreo(claims.correo());
            if (usuario != null && usuario.getRol().name().equals(claims.rol())) {
                request.setAttribute("rol", claims.rol());
                if (usuario.getRol() == RolUsuario.DUENO) {
                    request.setAttribute("usuarioLogueado", duenoService.findByCorreo(claims.correo()));
                } else {
                    request.setAttribute("usuarioLogueado", usuario);
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return request.getParameter("_token");
    }
}
