package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/veterinario")
@RequiredArgsConstructor
public class VeterinarioController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    private boolean sinAcceso(HttpSession session) {
        Object rol = session.getAttribute("rol");
        return !"VETERINARIO".equals(rol) && !"ADMIN".equals(rol);
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        model.addAttribute("clientes", duenoService.findAll());
        return "veterinario-dashboard";
    }

    // ---------- Clientes ----------

    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(HttpSession session, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        Dueno cliente = new Dueno();
        cliente.setUsuario(new Usuario());
        model.addAttribute("cliente", cliente);
        return "veterinario-cliente-form";
    }

    @PostMapping("/clientes")
    public String crearCliente(HttpSession session, @ModelAttribute Dueno cliente) {
        if (sinAcceso(session)) return "redirect:/login";
        cliente.getUsuario().setRol(RolUsuario.DUENO);
        cliente.getUsuario().setActivo(true);
        duenoService.save(cliente);
        return "redirect:/veterinario";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarClienteForm(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        model.addAttribute("cliente", duenoService.findById(id));
        return "veterinario-cliente-form";
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(HttpSession session, @PathVariable("id") Integer id, @ModelAttribute Dueno cliente) {
        if (sinAcceso(session)) return "redirect:/login";
        Dueno existente = duenoService.findById(id);
        existente.setNombre(cliente.getNombre());
        existente.setTelefono(cliente.getTelefono());
        existente.setDireccion(cliente.getDireccion());
        existente.getUsuario().setCorreo(cliente.getUsuario().getCorreo());
        existente.getUsuario().setPassword(cliente.getUsuario().getPassword());
        duenoService.save(existente);
        return "redirect:/veterinario";
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(HttpSession session, @PathVariable("id") Integer id) {
        if (sinAcceso(session)) return "redirect:/login";
        duenoService.delete(id);
        return "redirect:/veterinario";
    }

    // ---------- Mascotas ----------

    @GetMapping("/mascotas")
    public String listarMascotas(HttpSession session, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        model.addAttribute("mascotas", mascotaService.findAll());
        return "veterinario-mascotas";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevaMascotaForm(HttpSession session, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("duenos", duenoService.findAll());
        return "veterinario-mascota-form";
    }

    @PostMapping("/mascotas")
    public String crearMascota(HttpSession session, @ModelAttribute Mascota mascota, @RequestParam("duenoId") Integer duenoId) {
        if (sinAcceso(session)) return "redirect:/login";
        mascota.setDueno(duenoService.findById(duenoId));
        mascotaService.save(mascota);
        return "redirect:/veterinario/mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascotaForm(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        model.addAttribute("mascota", mascotaService.findById(id));
        model.addAttribute("duenos", duenoService.findAll());
        return "veterinario-mascota-form";
    }

    @PostMapping("/mascotas/{id}")
    public String actualizarMascota(HttpSession session, @PathVariable("id") Integer id,
                                     @ModelAttribute Mascota mascota, @RequestParam("duenoId") Integer duenoId) {
        if (sinAcceso(session)) return "redirect:/login";
        mascota.setId(id);
        mascota.setDueno(duenoService.findById(duenoId));
        mascotaService.save(mascota);
        return "redirect:/veterinario/mascotas";
    }

    @PostMapping("/mascotas/{id}/eliminar")
    public String eliminarMascota(HttpSession session, @PathVariable("id") Integer id) {
        if (sinAcceso(session)) return "redirect:/login";
        mascotaService.delete(id);
        return "redirect:/veterinario/mascotas";
    }
}
