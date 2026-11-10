package com.veterinaria.dogtor.e2e;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Base de las pruebas de extremo a extremo (e2e) con Selenium WebDriver.
// Abren Chrome contra la aplicación REAL: Angular en el 4200 y la API en el 8090 deben estar encendidos
// (la API, de preferencia, con el perfil e2e: H2 en memoria con los datos de prueba).
// Se corren con: ./mvnw test -Pe2e   (no corren con ./mvnw test a secas, están en el grupo "e2e")
//
// Propiedades opcionales (-D):
//   dogtor.e2e.frontUrl   URL de Angular   (http://localhost:4200)
//   dogtor.e2e.apiUrl     URL de la API    (http://localhost:8090/api)
//   dogtor.e2e.headless   true = Chrome sin ventana (para CI); por defecto se ve el navegador
@Tag("e2e")
public abstract class E2eTestBase {

    protected static final String FRONT = System.getProperty("dogtor.e2e.frontUrl", "http://localhost:4200");
    protected static final String API = System.getProperty("dogtor.e2e.apiUrl", "http://localhost:8090/api");

    // Usuarios de prueba (DataLoader)
    protected static final String VET_CORREO = "veterinario@dogtor.com";
    protected static final String VET_PASSWORD = "vet123";
    protected static final String VET_CEDULA = "800000001";
    protected static final String VET_NOMBRE = "Andrés Felipe Mora";
    protected static final String ADMIN_CORREO = "admin@dogtor.com";
    protected static final String ADMIN_PASSWORD = "admin123";

    protected static WebDriver driver;
    protected static WebDriverWait wait;

    private static final HttpClient http = HttpClient.newHttpClient();
    private static final ObjectMapper json = new ObjectMapper();

    @BeforeAll
    static void abrirNavegador() {
        ChromeOptions opciones = new ChromeOptions();
        // Ancho de escritorio: con una ventana angosta la navbar de Bootstrap se colapsa y los enlaces no se ven
        opciones.addArguments("--window-size=1280,900");
        if (Boolean.getBoolean("dogtor.e2e.headless")) {
            opciones.addArguments("--headless=new");
        }
        // Selenium Manager descarga solo el chromedriver que corresponde al Chrome instalado
        driver = new ChromeDriver(opciones);
        // La API tarda un poco en la primera petición (H2 + Hibernate): margen de 10 s para cada espera
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ===== Navegación =====

    protected static void abrir(String ruta) {
        driver.get(FRONT + ruta);
    }

    // Va a /login, escribe el correo y la contraseña y pulsa "Ingresar".
    // No valida el resultado: cada prueba revisa si entró o si vio el error
    protected static void login(String correo, String password) {
        abrir("/login");
        escribir("correo", correo);
        escribir("password", password);
        clic(By.cssSelector("form button[type='submit']"));
    }

    protected static void esperarRuta(String ruta) {
        wait.until(ExpectedConditions.urlToBe(FRONT + ruta));
    }

    protected static void esperarRutaQueCoincida(String regex) {
        wait.until(ExpectedConditions.urlMatches(regex));
    }

    protected static String rutaActual() {
        return driver.getCurrentUrl().substring(FRONT.length());
    }

    // ===== Interacción =====

    // Borra el campo (por id) y escribe el texto. sendKeys dispara los eventos que escucha el formulario reactivo
    protected static void escribir(String id, String texto) {
        WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        campo.clear();
        campo.sendKeys(texto);
    }

    // Centra el elemento en la pantalla antes de pulsarlo: la navbar es fija (sticky) y tapa lo que queda
    // justo en el borde superior al hacer scroll
    protected static void clic(By localizador) {
        WebElement elemento = wait.until(ExpectedConditions.elementToBeClickable(localizador));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'})", elemento);
        elemento.click();
    }

    // Espera a que el elemento aparezca y devuelve su texto visible
    protected static String texto(By localizador) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(localizador)).getText();
    }

    protected static void esperarTexto(By localizador, String contenido) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(localizador, contenido));
    }

    // ===== Utilidades =====

    // "$ 1.234.567" o "12 unidades" -> 1234567 / 12. Angular pinta con formato es-CO (punto de miles, símbolo
    // de moneda) y sin decimales ('1.0-0'), así que se comparan solo los dígitos
    protected static long soloDigitos(String textoConFormato) {
        String digitos = textoConFormato.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Long.parseLong(digitos);
    }

    // Fecha de hoy como la muestra el pipe date 'dd/MM/yyyy'
    protected static String hoy() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    protected static Pattern regex(String regex) {
        return Pattern.compile(regex);
    }

    // GET a la API y respuesta como árbol JSON. Sirve para leer el estado real antes y después de la prueba
    protected static JsonNode apiGet(String ruta) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(API + ruta)).GET().build();
            HttpResponse<String> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                throw new IllegalStateException("GET " + ruta + " respondió " + respuesta.statusCode());
            }
            return json.readTree(respuesta.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("No se pudo consultar la API en " + API + ruta + ". ¿Está encendida?", e);
        }
    }
}
