package com.veterinaria.dogtor.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

// Caso 1 (e2e): llega un cliente nuevo con su mascota.
// El veterinario se equivoca al ingresar y entra al segundo intento; registra al dueño (se equivoca en un campo,
// corrige y guarda) y luego registra la mascota asociada al dueño. El dueño entra al portal de clientes con su
// cédula (va en la URL) y ve que los datos de la mascota son correctos.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Caso1RegistroClienteMascotaTest extends E2eTestBase {

    // Datos únicos por ejecución: la API rechaza (409) una cédula o un correo repetidos
    private static final String SUFIJO = String.valueOf(System.currentTimeMillis()).substring(5); // 8 dígitos
    private static final String DUENO_CEDULA = "12" + SUFIJO; // 10 dígitos
    private static final String DUENO_NOMBRE = "Laura Prueba";
    private static final String DUENO_CELULAR_MALO = "12345"; // no cumple el patrón (10 dígitos y empezar por 3)
    private static final String DUENO_CELULAR = "3" + SUFIJO + "5";
    private static final String DUENO_CORREO = "laura." + SUFIJO + "@correo.com";
    private static final String DUENO_PASSWORD = "cliente123";

    private static final String MASCOTA_NOMBRE = "Kira";
    private static final String MASCOTA_RAZA = "Beagle";
    private static final String MASCOTA_EDAD = "3";
    private static final String MASCOTA_PESO = "9.5";
    private static final String MASCOTA_ENFERMEDAD = "Otitis";

    @Test
    @Order(1)
    @DisplayName("El veterinario ingresa (al segundo intento), registra al dueño (al segundo intento) y a la mascota")
    void veterinarioRegistraDuenoYMascota() {
        // --- Login: primer intento con la contraseña mal ---
        login(VET_CORREO, "contrasena-mala");
        String error = texto(By.cssSelector(".alert-danger"));
        assertTrue(error.contains("Correo o contraseña incorrectos"), "Debe mostrar el error del login: " + error);
        assertEquals("/login", rutaActual());

        // --- Segundo intento: entra a su portal ---
        escribir("password", VET_PASSWORD);
        clic(By.cssSelector("form button[type='submit']"));
        esperarRuta("/vet/" + VET_CEDULA);
        esperarTexto(By.tagName("h1"), "Hola, " + VET_NOMBRE);

        // --- Registro de clientes: se equivoca en el celular ---
        clic(By.linkText("Dueños"));
        esperarRuta("/vet/" + VET_CEDULA + "/duenos");
        clic(By.partialLinkText("Nuevo Dueño"));
        esperarRuta("/vet/" + VET_CEDULA + "/dueno/new");

        escribir("cedula", DUENO_CEDULA);
        escribir("nombre", DUENO_NOMBRE);
        escribir("celular", DUENO_CELULAR_MALO);
        escribir("correo", DUENO_CORREO);
        escribir("password", DUENO_PASSWORD);
        clic(By.cssSelector("form button[type='submit']"));

        // El formulario no se envía: muestra el error del campo y sigue en la misma página
        String errorCelular = texto(By.cssSelector(".invalid-feedback"));
        assertEquals("El celular debe tener 10 dígitos y empezar por 3.", errorCelular);
        assertTrue(driver.findElement(By.id("celular")).getAttribute("class").contains("is-invalid"));
        assertEquals("/vet/" + VET_CEDULA + "/dueno/new", rutaActual());

        // --- Corrige e intenta de nuevo: el cliente queda registrado ---
        escribir("celular", DUENO_CELULAR);
        clic(By.cssSelector("form button[type='submit']"));
        esperarRuta("/vet/" + VET_CEDULA + "/duenos");
        assertTrue(texto(By.cssSelector(".alert-success")).contains("Se registró a " + DUENO_NOMBRE + "."));
        String filaDueno = texto(By.xpath("//app-dueno-table//tr[td[normalize-space()='" + DUENO_CEDULA + "']]"));
        assertTrue(filaDueno.contains(DUENO_NOMBRE), "La tabla debe listar al dueño nuevo: " + filaDueno);

        // --- Mascotas: registra la mascota y la asocia al dueño. Todo sale bien a la primera ---
        clic(By.linkText("Mascotas"));
        esperarRuta("/vet/" + VET_CEDULA + "/mascotas");
        clic(By.partialLinkText("Nueva Mascota"));
        esperarRuta("/vet/" + VET_CEDULA + "/mascota/new");

        escribir("nombre", MASCOTA_NOMBRE);
        escribir("raza", MASCOTA_RAZA);
        escribir("edad", MASCOTA_EDAD);
        escribir("peso", MASCOTA_PESO);
        escribir("enfermedad", MASCOTA_ENFERMEDAD);
        // La lista de dueños llega de la API: se espera a que tenga opciones y se elige al dueño recién creado
        wait.until(d -> new Select(d.findElement(By.id("dueno"))).getOptions().size() > 1);
        new Select(driver.findElement(By.id("dueno"))).selectByVisibleText(DUENO_NOMBRE + " (C.C. " + DUENO_CEDULA + ")");
        clic(By.cssSelector("form button[type='submit']"));

        esperarRuta("/vet/" + VET_CEDULA + "/mascotas");
        String confirmacion = texto(By.cssSelector(".alert-success"));
        assertTrue(confirmacion.contains("Se registró a " + MASCOTA_NOMBRE + " (dueño: " + DUENO_NOMBRE + ")."), confirmacion);
        String filaMascota = texto(By.xpath("//app-mascota-table//tr[contains(., '" + DUENO_NOMBRE + "')]"));
        assertTrue(filaMascota.contains(MASCOTA_NOMBRE), "La tabla debe listar la mascota nueva: " + filaMascota);
    }

    @Test
    @Order(2)
    @DisplayName("El dueño ingresa al portal de clientes con su cédula y ve los datos de su mascota")
    void duenoIngresaYVeSuMascota() {
        // El login lo lleva a su portal: la cédula va en la URL (/cliente/<cédula>)
        login(DUENO_CORREO, DUENO_PASSWORD);
        esperarRuta("/cliente/" + DUENO_CEDULA);
        esperarTexto(By.tagName("h1"), "Hola, " + DUENO_NOMBRE);

        clic(By.linkText("Mis mascotas"));
        esperarRuta("/cliente/" + DUENO_CEDULA + "/mascotas");
        clic(By.xpath("//app-mascota-card[contains(., '" + MASCOTA_NOMBRE + "')]//a"));
        esperarRutaQueCoincida("/cliente/" + DUENO_CEDULA + "/mascota/\\d+$");

        // Datos de la mascota tal como los registró el veterinario
        esperarTexto(By.tagName("h1"), MASCOTA_NOMBRE);
        assertEquals("En la clínica", texto(By.cssSelector(".badge")));
        assertEquals(MASCOTA_RAZA, celda("Raza"));
        assertEquals(MASCOTA_EDAD + " año(s)", celda("Edad"));
        assertEquals(MASCOTA_PESO + " kg", celda("Peso"));
        assertEquals(MASCOTA_ENFERMEDAD, celda("Enfermedad"));
        assertEquals("Historial de tratamientos (0)", texto(By.xpath("//h2[contains(., 'Historial de tratamientos')]")));
        assertTrue(driver.getPageSource().contains("Esta mascota aún no tiene tratamientos registrados."));
    }

    // Valor de una fila de la tabla de datos de la mascota (<tr><th>Raza</th><td>Beagle</td></tr>)
    private static String celda(String etiqueta) {
        WebElement td = driver.findElement(By.xpath("//tr[th[normalize-space()='" + etiqueta + "']]/td"));
        return td.getText().trim();
    }
}
