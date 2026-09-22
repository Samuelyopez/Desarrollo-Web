package com.veterinaria.dogtor.controlador;

import com.veterinaria.dogtor.entidad.Droga;
import com.veterinaria.dogtor.entidad.Dueno;
import com.veterinaria.dogtor.entidad.Mascota;
import com.veterinaria.dogtor.entidad.RegistroMedico;
import com.veterinaria.dogtor.entidad.RolUsuario;
import com.veterinaria.dogtor.entidad.Usuario;
import com.veterinaria.dogtor.servicio.DrogaService;
import com.veterinaria.dogtor.servicio.DuenoService;
import com.veterinaria.dogtor.servicio.MascotaService;
import com.veterinaria.dogtor.servicio.RegistroMedicoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/veterinario")
@RequiredArgsConstructor
public class VeterinarioController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;
    private final RegistroMedicoService registroMedicoService;
    private final DrogaService drogaService;

    private boolean sinAcceso(HttpSession session) {
        Object rol = session.getAttribute("rol");
        return !"VETERINARIO".equals(rol) && !"ADMIN".equals(rol);
    }

    @GetMapping
    public String panel(HttpSession session, Model model,
                         @RequestParam(value = "nombre", required = false) String nombre) {
        if (sinAcceso(session)) return "redirect:/login";
        boolean buscando = nombre != null && !nombre.isBlank();
        model.addAttribute("clientes", buscando ? duenoService.findByNombreContaining(nombre) : duenoService.findAll());
        model.addAttribute("nombre", nombre);
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
    public String listarMascotas(HttpSession session, Model model,
                                  @RequestParam(value = "nombre", required = false) String nombre) {
        if (sinAcceso(session)) return "redirect:/login";
        boolean buscando = nombre != null && !nombre.isBlank();
        model.addAttribute("mascotas", buscando ? mascotaService.findByNombreContaining(nombre) : mascotaService.findAll());
        model.addAttribute("nombre", nombre);
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
        mascota.setActiva(true);
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
        Mascota existente = mascotaService.findById(id);
        existente.setNombre(mascota.getNombre());
        existente.setRaza(mascota.getRaza());
        existente.setEdad(mascota.getEdad());
        existente.setFotoUrl(mascota.getFotoUrl());
        existente.setVacunas(mascota.getVacunas());
        existente.setDueno(duenoService.findById(duenoId));
        mascotaService.save(existente);
        return "redirect:/veterinario/mascotas";
    }

    @PostMapping("/mascotas/{id}/estado")
    public String cambiarEstadoMascota(HttpSession session, @PathVariable("id") Integer id) {
        if (sinAcceso(session)) return "redirect:/login";
        Mascota mascota = mascotaService.findById(id);
        mascota.setActiva(!mascota.isActiva());
        mascotaService.save(mascota);
        return "redirect:/veterinario/mascotas";
    }

    // ---------- Detalle y registro médico ----------

    @GetMapping("/mascotas/{id}")
    public String verMascota(HttpSession session, @PathVariable("id") Integer id, Model model) {
        if (sinAcceso(session)) return "redirect:/login";
        Mascota mascota = mascotaService.findById(id);
        if (mascota == null) {
            return "redirect:/veterinario/mascotas";
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("registros", registroMedicoService.findByMascota(mascota));
        model.addAttribute("registro", new RegistroMedico());
        model.addAttribute("drogas", drogaService.findAll());
        return "veterinario-mascota-detalle";
    }

    @PostMapping("/mascotas/{id}/registros")
    public String crearRegistroMedico(HttpSession session, @PathVariable("id") Integer id,
                                       @ModelAttribute RegistroMedico registro,
                                       @RequestParam(value = "drogaIds", required = false) List<Integer> drogaIds) {
        if (sinAcceso(session)) return "redirect:/login";
        Mascota mascota = mascotaService.findById(id);
        if (mascota != null && mascota.isActiva()) {
            // El path variable "id" (de la mascota) se filtra al binding porque RegistroMedico
            // también tiene un campo "id"; se limpia para forzar un INSERT y no un UPDATE accidental.
            registro.setId(null);
            registro.setMascota(mascota);
            registro.setVeterinario((Usuario) session.getAttribute("usuarioLogueado"));
            if (drogaIds != null) {
                registro.setDrogas(drogaIds.stream().map(drogaService::findById).toList());
            }
            registroMedicoService.save(registro);
        }
        return "redirect:/veterinario/mascotas/" + id;
    }
}
