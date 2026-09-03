package com.veterinaria.dogtor.entidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mascota {
    private Integer id;
    private String nombre;
    private String raza;
    private String edad;
    private String fotoUrl;
    private String vacunas;
}
