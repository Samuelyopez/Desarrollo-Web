package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.seguridad.JwtService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        if ("DUENO".equals(request.getAttribute("rol"))) {
            model.addAttribute("usuario", request.getAttribute("usuarioLogueado"));
        }
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/equipo")
    public String equipo() {
        return "equipo";
    }

    @GetMapping("/planes")
    public String planes() {
        return "planes";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> procesarLogin(@RequestParam("correo") String correo,
                                                               @RequestParam("password") String password) {
        if (!usuarioService.authenticate(correo, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
        Usuario usuario = usuarioService.findByCorreo(correo);
        String redirectUrl = switch (usuario.getRol()) {
            case ADMIN -> "/admin";
            case VETERINARIO -> "/veterinario";
            case DUENO -> "/pacientes/mis-mascotas";
        };
        String token = jwtService.generarToken(correo, usuario.getRol().name());
        return ResponseEntity.ok(Map.of("token", token, "redirectUrl", redirectUrl));
    }

    @GetMapping("/diagrama")
    public String diagrama() {
        return "diagrama";
    }
}
