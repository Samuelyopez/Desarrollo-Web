package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DuenoController {

    Logger log = LoggerFactory.getLogger(DuenoController.class);

    @Autowired
    private DuenoService duenoService;
    @Autowired
    private MascotaService mascotaService;
    @Autowired
    private com.veterinaria.dogtor.repositorio.SolicitudAdopcionRepository solicitudAdopcionRepository;

    private Dueno getDuenoLogueado(HttpSession session) {
        return (Dueno) session.getAttribute("usuarioLogueado");
    }

    private boolean isAdmin(HttpSession session) {
        Dueno dueno = getDuenoLogueado(session);
        return dueno != null && dueno.isAdmin();
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Dueno dueno) {
        dueno.setAdmin(false);
        log.info("Guardando dueno");
        duenoService.save(dueno);
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.searchById(duenoSession.getId());
        List<Mascota> mascotas = mascotaService.findByDueno(dueno);
        model.addAttribute("dueno", dueno);
        model.addAttribute("mascotas", mascotas);
        return "perfil";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.searchById(duenoSession.getId());
        model.addAttribute("dueno", dueno);
        return "editar-perfil";
    }

    @PostMapping("/perfil/editar")
    public String guardarPerfilEditar(@ModelAttribute Dueno formDueno, HttpSession session) {
        Dueno duenoSession = getDuenoLogueado(session);
        if (duenoSession == null) {
            return "redirect:/login";
        }
        Dueno dbDueno = duenoService.searchById(duenoSession.getId());
        dbDueno.setNombre(formDueno.getNombre());
        dbDueno.setCorreo(formDueno.getCorreo());
        dbDueno.setPassword(formDueno.getPassword());
        dbDueno.setTelefono(formDueno.getTelefono());
        dbDueno.setDireccion(formDueno.getDireccion());
        
        log.info("Guardando dueno");
        duenoService.save(dbDueno);
        session.setAttribute("usuarioLogueado", dbDueno);
        
        return "redirect:/perfil";
    }

    @GetMapping("/admin/duenos")
    public String adminDuenos(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("duenos", duenoService.searchAllIncluyendoInactivos());
        return "admin-duenos";
    }

    @GetMapping("/admin/duenos/editar/{id}")
    public String adminEditarDueno(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.searchById(id);
        model.addAttribute("dueno", dueno);
        return "admin-editar-dueno";
    }

    @PostMapping("/admin/duenos/editar")
    public String adminGuardarDueno(@ModelAttribute Dueno dueno, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dbDueno = duenoService.searchById(dueno.getId());
        if (dbDueno != null) {
            dbDueno.setNombre(dueno.getNombre());
            dbDueno.setCorreo(dueno.getCorreo());
            dbDueno.setPassword(dueno.getPassword());
            dbDueno.setTelefono(dueno.getTelefono());
            dbDueno.setDireccion(dueno.getDireccion());
            dbDueno.setAdmin(dueno.isAdmin());
            log.info("Guardando dueno");
        duenoService.save(dbDueno);
        }
        return "redirect:/admin/duenos";
    }

    @GetMapping("/admin/duenos/toggle/{id}")
    public String adminToggleDueno(@PathVariable("id") Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.searchById(id);
        if (dueno != null) {
            duenoService.toggleActivo(id);
        }
        return "redirect:/admin/duenos";
    }
}
