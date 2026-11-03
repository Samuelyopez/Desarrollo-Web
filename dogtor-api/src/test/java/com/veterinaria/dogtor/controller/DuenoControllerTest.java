package com.veterinaria.dogtor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.dogtor.dto.DuenoRequest;
import com.veterinaria.dogtor.entities.Dueno;
import com.veterinaria.dogtor.entities.Rol;
import com.veterinaria.dogtor.entities.Usuario;
import com.veterinaria.dogtor.errors.NotFoundException;
import com.veterinaria.dogtor.service.DuenoService;

// Pruebas del controlador de dueños simulando peticiones HTTP con MockMvc (sin arrancar el servidor).
// @WebMvcTest carga solo la capa web (el controlador + GlobalExceptionHandler) y el servicio
// que hay debajo se simula con @MockitoBean (reemplazo de @MockBean desde Spring Boot 3.4).
// ObjectMapper pasa los objetos a JSON. Cubre GET, POST, PUT y DELETE
@WebMvcTest(controllers = DuenoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DuenoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DuenoService duenoService;

    private final Dueno ana = dueno("1010101010", "Ana Gómez", "ana@correo.com");

    @Test
    void DuenoController_Listar_RetornaListaDeDuenos() throws Exception {
        // Arrange
        Dueno carlos = dueno("2020202020", "Carlos Ruiz", "carlos@correo.com");
        when(duenoService.listar(null)).thenReturn(List.of(ana, carlos));

        // Act
        ResultActions response = mockMvc.perform(get("/api/duenos"));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", CoreMatchers.is(2)))
                .andExpect(jsonPath("$[0].nombre", CoreMatchers.is("Ana Gómez")));
    }

    @Test
    void DuenoController_Listar_PasaElFiltroAlServicio() throws Exception {
        // Arrange
        when(duenoService.listar("gom")).thenReturn(List.of(ana));

        // Act
        ResultActions response = mockMvc.perform(get("/api/duenos").param("buscar", "gom"));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(1)))
                .andExpect(jsonPath("$[0].correo", CoreMatchers.is("ana@correo.com")));
    }

    @Test
    void DuenoController_BuscarPorCedula_RetornaDueno() throws Exception {
        // Arrange
        when(duenoService.buscarPorCedula("1010101010")).thenReturn(ana);

        // Act
        ResultActions response = mockMvc.perform(get("/api/duenos/{cedula}", "1010101010"));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula", CoreMatchers.is("1010101010")))
                .andExpect(jsonPath("$.nombre", CoreMatchers.is("Ana Gómez")))
                .andExpect(jsonPath("$.usuario").doesNotExist());
    }

    @Test
    void DuenoController_BuscarPorCedula_RetornaNotFound() throws Exception {
        // Arrange
        when(duenoService.buscarPorCedula("999999"))
                .thenThrow(new NotFoundException("No existe un dueño con la cédula 999999"));

        // Act
        ResultActions response = mockMvc.perform(get("/api/duenos/{cedula}", "999999"));

        // Assert
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", CoreMatchers.is("No existe un dueño con la cédula 999999")));
    }

    @Test
    void DuenoController_Crear_RetornaCreated() throws Exception {
        // Arrange
        DuenoRequest request = new DuenoRequest("2020202020", "Carlos Ruiz", "3109998877", "carlos@correo.com", "123456");
        when(duenoService.crear(any(DuenoRequest.class)))
                .thenReturn(dueno("2020202020", "Carlos Ruiz", "carlos@correo.com"));

        // Act
        ResultActions response = mockMvc.perform(post("/api/duenos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cedula", CoreMatchers.is(request.cedula())))
                .andExpect(jsonPath("$.nombre", CoreMatchers.is(request.nombre())))
                .andExpect(jsonPath("$.correo", CoreMatchers.is(request.correo())))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(duenoService).crear(request);
    }

    @Test
    void DuenoController_Crear_RetornaBadRequestConDatosInvalidos() throws Exception {
        // Arrange: celular inválido
        DuenoRequest invalido = new DuenoRequest("2020202020", "Carlos Ruiz", "123", "carlos@correo.com", "123456");

        // Act
        ResultActions response = mockMvc.perform(post("/api/duenos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)));

        // Assert
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", CoreMatchers.is("El celular debe tener 10 dígitos y empezar por 3")));
        verify(duenoService, never()).crear(any());
    }

    @Test
    void DuenoController_Actualizar_RetornaDuenoActualizado() throws Exception {
        // Arrange
        DuenoRequest request = new DuenoRequest("1010101010", "Ana María Gómez", "3205556677", "ana@correo.com", null);
        when(duenoService.actualizar(eq("1010101010"), any(DuenoRequest.class)))
                .thenReturn(dueno("1010101010", "Ana María Gómez", "ana@correo.com"));

        // Act
        ResultActions response = mockMvc.perform(put("/api/duenos/{cedula}", "1010101010")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", CoreMatchers.is("Ana María Gómez")));
    }

    @Test
    void DuenoController_Eliminar_RetornaNoContent() throws Exception {
        // Act
        ResultActions response = mockMvc.perform(delete("/api/duenos/{cedula}", "1010101010"));

        // Assert
        response.andExpect(status().isNoContent());
        verify(duenoService).eliminar("1010101010");
    }

    private static Dueno dueno(String cedula, String nombre, String correo) {
        return new Dueno(cedula, nombre, "3001112233", new Usuario(correo, "123456", Rol.DUENO));
    }
}
