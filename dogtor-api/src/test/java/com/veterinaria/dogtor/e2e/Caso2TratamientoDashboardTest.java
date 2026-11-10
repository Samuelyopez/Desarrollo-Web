package com.veterinaria.dogtor.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.fasterxml.jackson.databind.JsonNode;

// Caso 2 (e2e): el veterinario suministra un medicamento a una mascota.
// Busca la mascota, le da un tratamiento y guarda. Para verificar, la busca otra vez, entra al detalle y ve el
// tratamiento guardado. Después entra el administrador y revisa en el dashboard que la cantidad de medicamentos
// suministrados y las ganancias sean correctas.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Caso2TratamientoDashboardTest extends E2eTestBase {

    private static final String MASCOTA = "Rocky"; // de los datos de prueba (María López), activa
    private static final int CANTIDAD = 2;
    // Título "Historial de tratamientos (N)" del detalle (en el portal veterinario hay otro h2, "Dueño")
    private static final By HISTORIAL = By.xpath("//h2[contains(., 'Historial de tratamientos')]");

    // Estado ANTES de la prueba, leído de la API, para calcular lo que debe mostrar el dashboard DESPUÉS
    private static JsonNode dashboardAntes;
    private static String medicamentoNombre;
    private static double medicamentoPrecio;
    private static long tratamientosAntes;

    @BeforeAll
    static void leerEstadoInicial() {
        dashboardAntes = apiGet("/dashboard");
        JsonNode conUnidades = null;
        for (JsonNode medicamento : apiGet("/medicamentos?disponibles=true")) {
            if (medicamento.get("unidadesDisponibles").asInt() >= CANTIDAD) {
                conUnidades = medicamento;
                break;
            }
        }
        assertNotNull(conUnidades, "Debe haber un medicamento con al menos " + CANTIDAD + " unidades");
        medicamentoNombre = conUnidades.get("nombre").asText();
        medicamentoPrecio = conUnidades.get("precioVenta").asDouble();
    }

    @Test
    @Order(1)
    @DisplayName("El veterinario busca la mascota, le da un tratamiento y lo ve guardado en el detalle")
    void veterinarioDaTratamientoYLoVerifica() {
        login(VET_CORREO, VET_PASSWORD);
        esperarRuta("/vet/" + VET_CEDULA);

        // --- Busca la mascota en la barra de búsqueda y la selecciona ---
        buscarMascotaYAbrirDetalle();
        tratamientosAntes = soloDigitos(texto(HISTORIAL));

        // --- Le da un nuevo tratamiento ---
        clic(By.partialLinkText("Dar tratamiento"));
        // Desde el detalle se llega con la mascota preseleccionada: .../tratamiento/new?mascota=<id>
        esperarRutaQueCoincida("/vet/" + VET_CEDULA + "/tratamiento/new\\?mascota=\\d+$");
        wait.until(ExpectedConditions.attributeToBe(By.id("veterinario"), "value", VET_NOMBRE));
        assertEquals(hoy(), driver.findElement(By.id("fecha")).getAttribute("value"));
        // La mascota viene preseleccionada desde su detalle (?mascota=id)
        wait.until(d -> new Select(d.findElement(By.id("mascota"))).getFirstSelectedOption().getText().contains(MASCOTA));
        // Se elige el medicamento por su nombre (el texto de la opción trae también precio y unidades)
        seleccionarMedicamento(medicamentoNombre);
        escribir("cantidad", String.valueOf(CANTIDAD));
        clic(By.xpath("//button[contains(., 'Registrar tratamiento')]"));

        // Vuelve al detalle con el mensaje de confirmación
        esperarRutaQueCoincida("/mascota/\\d+$");
        String confirmacion = texto(By.cssSelector(".alert-success"));
        assertTrue(confirmacion.contains("Se registró " + medicamentoNombre + " x" + CANTIDAD + " para " + MASCOTA + "."), confirmacion);

        // --- Verifica: busca la mascota otra vez, entra al detalle y el tratamiento está guardado ---
        buscarMascotaYAbrirDetalle();
        esperarTexto(HISTORIAL, "Historial de tratamientos (" + (tratamientosAntes + 1) + ")");
        // El historial va del más reciente al más antiguo: el nuevo es la primera fila
        List<WebElement> celdas = driver.findElements(By.cssSelector("app-tratamiento-list tbody tr:first-child td"));
        assertEquals(hoy(), celdas.get(0).getText().trim());
        assertEquals(medicamentoNombre, celdas.get(1).getText().trim());
        assertTrue(celdas.get(2).getText().contains(VET_NOMBRE));
        assertEquals(String.valueOf(CANTIDAD), celdas.get(3).getText().trim());
        assertEquals(Math.round(medicamentoPrecio * CANTIDAD), soloDigitos(celdas.get(5).getText()));
    }

    @Test
    @Order(2)
    @DisplayName("El administrador ve en el dashboard los medicamentos suministrados y las ganancias correctas")
    void administradorVerificaDashboard() {
        login(ADMIN_CORREO, ADMIN_PASSWORD);
        esperarRuta("/admin");
        clic(By.linkText("Dashboard"));
        esperarRuta("/admin/dashboard");
        esperarTexto(By.tagName("h1"), "Dashboard");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-kpi-card")));

        // Lo esperado: el dashboard de antes + el tratamiento que se acaba de dar
        long tratamientosEsperados = dashboardAntes.get("tratamientosUltimoMes").asLong() + 1;
        long unidadesEsperadas = dashboardAntes.get("unidadesUltimoMes").asLong() + CANTIDAD;
        double ventasEsperadas = dashboardAntes.get("ventasTotales").asDouble() + medicamentoPrecio * CANTIDAD;

        // 1. La API ya refleja el tratamiento (ventas exactas; la ganancia sube sin superar la venta,
        //    el precio de compra no se expone al front)
        JsonNode despues = apiGet("/dashboard");
        assertEquals(tratamientosEsperados, despues.get("tratamientosUltimoMes").asLong(), "tratamientos del mes");
        assertEquals(unidadesEsperadas, despues.get("unidadesUltimoMes").asLong(), "unidades del mes");
        assertEquals(ventasEsperadas, despues.get("ventasTotales").asDouble(), 0.01, "ventas totales");
        double ganancia = despues.get("gananciasTotales").asDouble() - dashboardAntes.get("gananciasTotales").asDouble();
        assertTrue(ganancia > 0, "La ganancia debe subir: " + ganancia);
        assertTrue(ganancia <= medicamentoPrecio * CANTIDAD + 0.01, "La ganancia no puede superar la venta: " + ganancia);

        // 2. Y el dashboard muestra exactamente esos números
        assertEquals(tratamientosEsperados, soloDigitos(kpi("Tratamientos del mes", "valor")));
        String detalleUnidades = kpi("Tratamientos del mes", "detalle");
        assertTrue(detalleUnidades.contains("unidades suministradas"), detalleUnidades);
        assertEquals(unidadesEsperadas, soloDigitos(detalleUnidades));
        assertEquals(Math.round(ventasEsperadas), soloDigitos(kpi("Ventas totales", "valor")));
        assertEquals(Math.round(despues.get("gananciasTotales").asDouble()), soloDigitos(kpi("Ganancias totales", "valor")));

        // 3. En "Tratamientos por medicamento" el medicamento suministrado suma las unidades de hoy
        long unidadesMedicamentoAntes = 0;
        for (JsonNode fila : dashboardAntes.get("tratamientosPorMedicamento")) {
            if (fila.get("medicamento").asText().equals(medicamentoNombre)) {
                unidadesMedicamentoAntes = fila.get("unidades").asLong();
            }
        }
        String unidadesEnTabla = texto(By.xpath("//h2[contains(., 'Tratamientos por medicamento')]"
                + "/ancestor::div[contains(@class, 'card-body')]//tbody/tr[td[1][normalize-space()='" + medicamentoNombre + "']]/td[3]"));
        assertEquals(unidadesMedicamentoAntes + CANTIDAD, soloDigitos(unidadesEnTabla));
    }

    // Desde el portal: va a Mascotas, escribe el nombre en la barra de búsqueda y pulsa "Ver" en su fila
    private static void buscarMascotaYAbrirDetalle() {
        clic(By.linkText("Mascotas"));
        esperarRuta("/vet/" + VET_CEDULA + "/mascotas");
        escribir("buscarMascota", MASCOTA);
        // La búsqueda tiene debounce y va a la API: se espera a que la tabla de activas quede filtrada (1 fila)
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("h3"), "Mascotas activas (1)"));
        clic(By.xpath("//app-mascota-table//tr[contains(., '" + MASCOTA + "')]//button[contains(., 'Ver')]"));
        esperarRutaQueCoincida("/vet/" + VET_CEDULA + "/mascota/\\d+$");
        esperarTexto(By.tagName("h1"), MASCOTA);
    }

    // Las opciones son "NOMBRE — $ precio — N disponibles": se busca la que empieza por el nombre
    private static void seleccionarMedicamento(String nombre) {
        wait.until(d -> new Select(d.findElement(By.id("medicamento"))).getOptions().size() > 1);
        Select medicamentos = new Select(driver.findElement(By.id("medicamento")));
        for (WebElement opcion : medicamentos.getOptions()) {
            if (opcion.getText().startsWith(nombre + " —")) {
                medicamentos.selectByVisibleText(opcion.getText());
                return;
            }
        }
        throw new AssertionError("No está el medicamento " + nombre + " en la lista");
    }

    // Texto de una parte ("valor" o "detalle") de la tarjeta KPI con ese título
    private static String kpi(String titulo, String parte) {
        return texto(By.xpath("//app-kpi-card[contains(., '" + titulo + "')]//p[contains(@class, '" + parte + "')]"));
    }
}
