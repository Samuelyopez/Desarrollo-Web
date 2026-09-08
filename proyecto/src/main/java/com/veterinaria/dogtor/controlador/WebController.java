package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.SolicitudAdopcion;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.repositorio.DuenoRepository;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.ProductoService;
import com.veterinaria.dogtor.servicio.SolicitudAdopcionService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WebController {

    Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private DuenoService duenoService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private MascotaService mascotaService;
    @Autowired
    private SolicitudAdopcionService solicitudService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DuenoRepository duenoRepository;

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
        model.addAttribute("productos", productoService.findByCategoria(com.veterinaria.dogtor.entidad.CategoriaProducto.MEDICINAL));
        return "farmacia";
    }
    
    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        model.addAttribute("productos", productoService.findByCategoria(com.veterinaria.dogtor.entidad.CategoriaProducto.RECREACIONAL));
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
    public String formularioAdopcion(@PathVariable("id") Long mascotaId, Model model) {
        Mascota mascota = mascotaService.searchById(mascotaId);
        if(mascota == null || !mascota.isEnAdopcion()) {
            return "redirect:/adopciones";
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("solicitud", new SolicitudAdopcion());
        return "formulario-adopcion";
    }

    @PostMapping("/adopciones/formulario")
    public String enviarAdopcion(@ModelAttribute SolicitudAdopcion solicitud, @RequestParam("mascotaId") Long mascotaId) {
        Mascota mascota = mascotaService.searchById(mascotaId);
        solicitud.setMascota(mascota);
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(com.veterinaria.dogtor.entidad.EstadoSolicitud.PENDIENTE);
        }
        solicitudService.save(solicitud);
        return "redirect:/adopciones?exito=true";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
        if (dueno != null) {
            if (dueno.isAdmin()) {
                return "redirect:/admin/mascotas";
            }
            return "redirect:/perfil";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        Usuario usuario = usuarioService.authenticate(correo, password);
        if (usuario != null) {
            Dueno dueno = duenoRepository.findByCorreo(correo).orElse(null);
            if (dueno == null) {
                dueno = Dueno.builder().nombre(usuario.getCorreo()).usuario(usuario).build();
            }
            session.setAttribute("usuarioLogueado", dueno);
            if (dueno.isAdmin()) {
                return "redirect:/admin/mascotas";
            } else {
                return "redirect:/perfil";
            }
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }

    private boolean isAdmin(HttpSession session) {
        Dueno dueno = (Dueno) session.getAttribute("usuarioLogueado");
        return dueno != null && dueno.isAdmin();
    }

    @GetMapping("/admin/solicitudes")
    public String adminSolicitudes(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("solicitudes", solicitudService.searchAll());
        return "admin-solicitudes";
    }

    @PostMapping("/admin/solicitudes/estado/{id}")
    public String adminCambiarEstado(@PathVariable("id") Long id,
                                      @RequestParam("estado") com.veterinaria.dogtor.entidad.EstadoSolicitud estado,
                                      HttpSession session,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        solicitudService.actualizarEstado(id, estado);
        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado correctamente.");
        return "redirect:/admin/solicitudes";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }



    @GetMapping("/diagrama")
    public String diagrama() {
        return "diagrama";
    }
}
