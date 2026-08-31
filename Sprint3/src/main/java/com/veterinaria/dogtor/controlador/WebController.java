package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.SolicitudAdopcion;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.ProductoService;
import com.veterinaria.dogtor.servicio.SolicitudAdopcionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final DuenoService duenoService;
    private final ProductoService productoService;
    private final MascotaService mascotaService;
    private final SolicitudAdopcionService solicitudService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
        if (dueno != null) {
            model.addAttribute("usuario", dueno);
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

    @GetMapping("/farmacia")
    public String farmacia(Model model) {
        model.addAttribute("productos", productoService.findByCategoria("MEDICINAL"));
        return "farmacia";
    }
    
    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        model.addAttribute("productos", productoService.findByCategoria("RECREACIONAL"));
        return "accesorios";
    }
    
    @GetMapping("/planes")
    public String planes() {
        return "planes";
    }

    @GetMapping("/adopciones")
    public String adopciones(Model model) {
        model.addAttribute("mascotas", mascotaService.findByEnAdopcion(true));
        return "adopciones";
    }

    @GetMapping("/adopciones/formulario/{id}")
    public String formularioAdopcion(@PathVariable("id") Integer mascotaId, Model model) {
        Mascota mascota = mascotaService.findById(mascotaId);
        if(mascota == null || !mascota.isEnAdopcion()) {
            return "redirect:/adopciones";
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("solicitud", new SolicitudAdopcion());
        return "formulario-adopcion";
    }

    @PostMapping("/adopciones/formulario")
    public String enviarAdopcion(@ModelAttribute SolicitudAdopcion solicitud, @RequestParam("mascotaId") Integer mascotaId) {
        Mascota mascota = mascotaService.findById(mascotaId);
        solicitud.setMascota(mascota);
        solicitudService.save(solicitud);
        return "redirect:/adopciones?exito=true";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/pacientes/mis-mascotas";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        if (duenoService.authenticate(correo, password)) {
            Dueno dueno = duenoService.findByCorreo(correo);
            session.setAttribute("usuarioLogueado", dueno);
            return "redirect:/pacientes/mis-mascotas";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/citas")
    public String citas() {
        return "citas";
    }

    @GetMapping("/diagrama")
    public String diagrama() {
        return "diagrama";
    }
}
