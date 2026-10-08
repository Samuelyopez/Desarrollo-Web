package com.veterinaria.dogtor.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.veterinaria.dogtor.entities.Medicamento;
import com.veterinaria.dogtor.repository.MedicamentoRepository;

// Carga los medicamentos desde el Excel al iniciar la API (AC30).
// Solo INSERTA los que no existen (por nombre): la BD es un archivo y si se actualizaran los que ya
// están, cada reinicio borraría las unidades vendidas por la app. El Excel es la carga inicial.
// Corre antes que DataLoader (@Order), que usa estos medicamentos en los tratamientos de prueba
@Component
@Order(1)
public class MedicamentoExcelLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MedicamentoExcelLoader.class);

    // Columnas obligatorias de la primera fila (sin tildes ni mayúsculas)
    private static final List<String> COLUMNAS = List.of(
            "nombre", "precio compra", "precio venta", "unidades disponibles", "unidades vendidas");

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${dogtor.medicamentos.excel}")
    private String rutaExcel;

    private final DataFormatter formato = new DataFormatter();

    @Override
    @Transactional
    public void run(String... args) {
        Resource excel = resourceLoader.getResource(rutaExcel);
        if (!excel.exists()) {
            log.warn("No se encontró el Excel de medicamentos ({}): no se cargan medicamentos", rutaExcel);
            return;
        }

        try (InputStream entrada = excel.getInputStream(); Workbook libro = new XSSFWorkbook(entrada)) {
            Sheet hoja = libro.getSheet("Medicamentos") != null ? libro.getSheet("Medicamentos") : libro.getSheetAt(0);
            cargarHoja(hoja);
        } catch (IOException e) {
            log.error("No se pudo leer el Excel de medicamentos ({})", rutaExcel, e);
        }
    }

    private void cargarHoja(Sheet hoja) {
        Map<String, Integer> columnas = leerCabecera(hoja.getRow(0));
        List<String> faltantes = COLUMNAS.stream().filter(c -> !columnas.containsKey(c)).toList();
        if (!faltantes.isEmpty()) {
            log.error("Al Excel de medicamentos le faltan las columnas {}: no se carga", faltantes);
            return;
        }

        int insertados = 0, existentes = 0, invalidos = 0;
        Set<String> vistos = new HashSet<>();
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            String nombre = fila == null ? "" : texto(fila.getCell(columnas.get("nombre")));
            if (nombre.isBlank()) {
                continue;
            }

            Double precioCompra = numero(fila.getCell(columnas.get("precio compra")));
            Double precioVenta = numero(fila.getCell(columnas.get("precio venta")));
            Double disponibles = numero(fila.getCell(columnas.get("unidades disponibles")));
            Double vendidas = numero(fila.getCell(columnas.get("unidades vendidas")));
            String error = validar(precioCompra, precioVenta, disponibles, vendidas);
            if (error != null) {
                log.warn("Fila {} del Excel inválida ({}): {}", i + 1, nombre, error);
                invalidos++;
                continue;
            }

            if (!vistos.add(nombre.toLowerCase()) || medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
                existentes++;
                continue;
            }
            medicamentoRepository.save(new Medicamento(nombre, precioCompra, precioVenta,
                    disponibles.intValue(), vendidas == null ? 0 : vendidas.intValue()));
            insertados++;
        }
        log.info("Medicamentos desde Excel: {} insertados, {} ya existían, {} filas inválidas",
                insertados, existentes, invalidos);
    }

    private Map<String, Integer> leerCabecera(Row cabecera) {
        Map<String, Integer> columnas = new HashMap<>();
        if (cabecera != null) {
            for (Cell celda : cabecera) {
                columnas.put(normalizar(texto(celda)), celda.getColumnIndex());
            }
        }
        return columnas;
    }

    private String validar(Double precioCompra, Double precioVenta, Double disponibles, Double vendidas) {
        if (precioCompra == null || precioCompra <= 0) {
            return "precio de compra vacío o no positivo";
        }
        if (precioVenta == null || precioVenta <= 0) {
            return "precio de venta vacío o no positivo";
        }
        if (disponibles == null || disponibles < 0 || disponibles % 1 != 0) {
            return "unidades disponibles deben ser un entero mayor o igual a 0";
        }
        if (vendidas != null && (vendidas < 0 || vendidas % 1 != 0)) {
            return "unidades vendidas deben ser un entero mayor o igual a 0";
        }
        return null;
    }

    private String texto(Cell celda) {
        return celda == null ? "" : formato.formatCellValue(celda).trim();
    }

    // Acepta celdas numéricas o texto como "$ 15.000" o "8,5"
    private Double numero(Cell celda) {
        if (celda == null || celda.getCellType() == CellType.BLANK) {
            return null;
        }
        if (celda.getCellType() == CellType.NUMERIC || celda.getCellType() == CellType.FORMULA) {
            return celda.getNumericCellValue();
        }
        String valor = texto(celda).replace("$", "").replace(" ", "");
        if (valor.matches("\\d{1,3}(\\.\\d{3})+")) {
            valor = valor.replace(".", "");
        }
        try {
            return valor.isEmpty() ? null : Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
