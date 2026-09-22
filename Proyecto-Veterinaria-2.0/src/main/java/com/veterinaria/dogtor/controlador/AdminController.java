package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

    private boolean noEsAdmin(HttpSession session) {
        return !"ADMIN".equals(session.getAttribute("rol"));
    }

    @GetMapping
    public String panel(HttpSession session) {
        if (noEsAdmin(session)) return "redirect:/login";
        return "redirect:/admin/clientes";
    }

    // ---------- Clientes ----------

    @GetMapping("/clientes")
    public String listarClientes(HttpSession session, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("clientes", duenoService.findAll());
        return "admin-clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(HttpSession session, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        Dueno cliente = new Dueno();
        cliente.setUsuario(new Usuario());
        model.addAttribute("cliente", cliente);
        return "admin-cliente-form";
    }

    @PostMapping("/clientes")
    public String crearCliente(HttpSession session, @ModelAttribute Dueno cliente) {
        if (noEsAdmin(session)) return "redirect:/login";
        cliente.getUsuario().setRol(RolUsuario.DUENO);
        cliente.getUsuario().setActivo(true);
        duenoService.save(cliente);
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarClienteForm(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("cliente", duenoService.findById(id));
        return "admin-cliente-form";
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(HttpSession session, @PathVariable("id") Integer id, @ModelAttribute Dueno cliente) {
        if (noEsAdmin(session)) return "redirect:/login";
        Dueno existente = duenoService.findById(id);
        existente.setNombre(cliente.getNombre());
        existente.setTelefono(cliente.getTelefono());
        existente.setDireccion(cliente.getDireccion());
        existente.getUsuario().setCorreo(cliente.getUsuario().getCorreo());
        existente.getUsuario().setPassword(cliente.getUsuario().getPassword());
        duenoService.save(existente);
        return "redirect:/admin/clientes";
    }

    @PostMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(HttpSession session, @PathVariable("id") Integer id) {
        if (noEsAdmin(session)) return "redirect:/login";
        duenoService.delete(id);
        return "redirect:/admin/clientes";
    }

    // ---------- Mascotas ----------

    @GetMapping("/mascotas")
    public String listarMascotas(HttpSession session, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("mascotas", mascotaService.findAll());
        return "admin-mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascotaForm(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("mascota", mascotaService.findById(id));
        model.addAttribute("duenos", duenoService.findAll());
        return "admin-mascota-form";
    }

    @PostMapping("/mascotas/{id}")
    public String actualizarMascota(HttpSession session, @PathVariable("id") Integer id,
                                     @ModelAttribute Mascota mascota, @RequestParam("duenoId") Integer duenoId) {
        if (noEsAdmin(session)) return "redirect:/login";
        Mascota existente = mascotaService.findById(id);
        existente.setNombre(mascota.getNombre());
        existente.setRaza(mascota.getRaza());
        existente.setEdad(mascota.getEdad());
        existente.setFotoUrl(mascota.getFotoUrl());
        existente.setVacunas(mascota.getVacunas());
        existente.setEnAdopcion(mascota.isEnAdopcion());
        existente.setDueno(duenoService.findById(duenoId));
        mascotaService.save(existente);
        return "redirect:/admin/mascotas";
    }

    @PostMapping("/mascotas/{id}/estado")
    public String cambiarEstadoMascota(HttpSession session, @PathVariable("id") Integer id) {
        if (noEsAdmin(session)) return "redirect:/login";
        Mascota mascota = mascotaService.findById(id);
        mascota.setActiva(!mascota.isActiva());
        mascotaService.save(mascota);
        return "redirect:/admin/mascotas";
    }

    // ---------- Veterinarios ----------

    @GetMapping("/veterinarios")
    public String listarVeterinarios(HttpSession session, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("veterinarios", usuarioService.findAll().stream()
                .filter(u -> u.getRol() != RolUsuario.DUENO)
                .toList());
        return "admin-veterinarios";
    }

    @GetMapping("/veterinarios/nuevo")
    public String nuevoVeterinarioForm(HttpSession session, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        Usuario veterinario = new Usuario();
        veterinario.setActivo(true);
        model.addAttribute("veterinario", veterinario);
        return "admin-veterinario-form";
    }

    @PostMapping("/veterinarios")
    public String crearVeterinario(HttpSession session, @ModelAttribute Usuario veterinario) {
        if (noEsAdmin(session)) return "redirect:/login";
        veterinario.setRol(RolUsuario.VETERINARIO);
        usuarioService.save(veterinario);
        return "redirect:/admin/veterinarios";
    }

    @GetMapping("/veterinarios/editar/{id}")
    public String editarVeterinarioForm(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(session)) return "redirect:/login";
        model.addAttribute("veterinario", usuarioService.findById(id));
        return "admin-veterinario-form";
    }

    @PostMapping("/veterinarios/{id}")
    public String actualizarVeterinario(HttpSession session, @PathVariable("id") Integer id, @ModelAttribute Usuario veterinario) {
        if (noEsAdmin(session)) return "redirect:/login";
        Usuario existente = usuarioService.findById(id);
        existente.setNombre(veterinario.getNombre());
        existente.setCorreo(veterinario.getCorreo());
        existente.setPassword(veterinario.getPassword());
        existente.setActivo(veterinario.isActivo());
        usuarioService.save(existente);
        return "redirect:/admin/veterinarios";
    }

    @PostMapping("/veterinarios/{id}/eliminar")
    public String eliminarVeterinario(HttpSession session, @PathVariable("id") Integer id) {
        if (noEsAdmin(session)) return "redirect:/login";
        usuarioService.delete(id);
        return "redirect:/admin/veterinarios";
    }
}
