package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.entidad.Veterinario;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.VeterinarioService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final VeterinarioService veterinarioService;

    private boolean noEsAdmin(HttpServletRequest request) {
        return !"ADMIN".equals(request.getAttribute("rol"));
    }

    @GetMapping
    public String panel(HttpServletRequest request) {
        if (noEsAdmin(request)) return "auth-shell";
        return "redirect:/admin/clientes";
    }

    // ---------- Clientes ----------

    @GetMapping("/clientes")
    public String listarClientes(HttpServletRequest request, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("clientes", duenoService.findAll());
        return "admin-clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoClienteForm(HttpServletRequest request, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        Dueno cliente = new Dueno();
        cliente.setUsuario(new Usuario());
        model.addAttribute("cliente", cliente);
        return "admin-cliente-form";
    }

    @PostMapping("/clientes")
    public String crearCliente(HttpServletRequest request, @ModelAttribute Dueno cliente) {
        if (noEsAdmin(request)) return "redirect:/login";
        cliente.getUsuario().setRol(RolUsuario.DUENO);
        cliente.getUsuario().setActivo(true);
        duenoService.save(cliente);
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarClienteForm(HttpServletRequest request, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("cliente", duenoService.findById(id));
        return "admin-cliente-form";
    }

    @PostMapping("/clientes/{id}")
    public String actualizarCliente(HttpServletRequest request, @PathVariable("id") Integer id, @ModelAttribute Dueno cliente) {
        if (noEsAdmin(request)) return "redirect:/login";
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
    public String eliminarCliente(HttpServletRequest request, @PathVariable("id") Integer id) {
        if (noEsAdmin(request)) return "redirect:/login";
        duenoService.delete(id);
        return "redirect:/admin/clientes";
    }

    // ---------- Mascotas ----------

    @GetMapping("/mascotas")
    public String listarMascotas(HttpServletRequest request, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("mascotas", mascotaService.findAll());
        return "admin-mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascotaForm(HttpServletRequest request, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("mascota", mascotaService.findById(id));
        model.addAttribute("duenos", duenoService.findAll());
        return "admin-mascota-form";
    }

    @PostMapping("/mascotas/{id}")
    public String actualizarMascota(HttpServletRequest request, @PathVariable("id") Integer id,
                                     @ModelAttribute Mascota mascota, @RequestParam("duenoId") Integer duenoId) {
        if (noEsAdmin(request)) return "redirect:/login";
        Mascota existente = mascotaService.findById(id);
        existente.setNombre(mascota.getNombre());
        existente.setRaza(mascota.getRaza());
        existente.setEdad(mascota.getEdad());
        existente.setFotoUrl(mascota.getFotoUrl());
        existente.setVacunas(mascota.getVacunas());
        existente.setDueno(duenoService.findById(duenoId));
        mascotaService.save(existente);
        return "redirect:/admin/mascotas";
    }

    @PostMapping("/mascotas/{id}/estado")
    public String cambiarEstadoMascota(HttpServletRequest request, @PathVariable("id") Integer id) {
        if (noEsAdmin(request)) return "redirect:/login";
        Mascota mascota = mascotaService.findById(id);
        mascota.setActiva(!mascota.isActiva());
        mascotaService.save(mascota);
        return "redirect:/admin/mascotas";
    }

    // ---------- Veterinarios ----------

    @GetMapping("/veterinarios")
    public String listarVeterinarios(HttpServletRequest request, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("veterinarios", veterinarioService.findAll());
        return "admin-veterinarios";
    }

    @GetMapping("/veterinarios/nuevo")
    public String nuevoVeterinarioForm(HttpServletRequest request, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        Veterinario veterinario = new Veterinario();
        veterinario.setUsuario(new Usuario());
        model.addAttribute("veterinario", veterinario);
        return "admin-veterinario-form";
    }

    @PostMapping("/veterinarios")
    public String crearVeterinario(HttpServletRequest request, @ModelAttribute Veterinario veterinario) {
        if (noEsAdmin(request)) return "redirect:/login";
        veterinario.getUsuario().setRol(RolUsuario.VETERINARIO);
        veterinario.getUsuario().setActivo(true);
        veterinarioService.save(veterinario);
        return "redirect:/admin/veterinarios";
    }

    @GetMapping("/veterinarios/editar/{id}")
    public String editarVeterinarioForm(HttpServletRequest request, @PathVariable("id") Integer id, Model model) {
        if (noEsAdmin(request)) return "auth-shell";
        model.addAttribute("veterinario", veterinarioService.findById(id));
        return "admin-veterinario-form";
    }

    @PostMapping("/veterinarios/{id}")
    public String actualizarVeterinario(HttpServletRequest request, @PathVariable("id") Integer id, @ModelAttribute Veterinario veterinario) {
        if (noEsAdmin(request)) return "redirect:/login";
        Veterinario existente = veterinarioService.findById(id);
        existente.setEspecialidad(veterinario.getEspecialidad());
        existente.setNumeroLicencia(veterinario.getNumeroLicencia());
        existente.getUsuario().setNombre(veterinario.getUsuario().getNombre());
        existente.getUsuario().setCorreo(veterinario.getUsuario().getCorreo());
        existente.getUsuario().setPassword(veterinario.getUsuario().getPassword());
        veterinarioService.save(existente);
        return "redirect:/admin/veterinarios";
    }

    @PostMapping("/veterinarios/{id}/eliminar")
    public String eliminarVeterinario(HttpServletRequest request, @PathVariable("id") Integer id) {
        if (noEsAdmin(request)) return "redirect:/login";
        veterinarioService.delete(id);
        return "redirect:/admin/veterinarios";
    }
}
