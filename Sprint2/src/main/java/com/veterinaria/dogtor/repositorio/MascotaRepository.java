package com.veterinaria.dogtor.repositorio;

import com.veterinaria.dogtor.entidad.Mascota;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MascotaRepository {
    
    private Map<Integer, Mascota> database = new HashMap<>();
    private Integer currentId = 1;

    public MascotaRepository() {
        // Inicializar con las 10 mascotas inventadas del Sprint 1
        save(new Mascota(null, "Max", "Golden Retriever", "3 años", "https://heronscrossing.vet/wp-content/uploads/Golden-Retriever-2048x1365.jpg=200", "Al día"));
        save(new Mascota(null, "Luna", "Gato Siamés", "2 años", "https://animalpets.co/wp-content/uploads/2024/11/Gato-Siames.png?w=200", "Al día"));
        save(new Mascota(null, "Rocky", "Bulldog", "5 años", "https://artero.com/media/wysiwyg/Bulldog_Franc_s2_1_.png?w=200", "Falta refuerzo"));
        save(new Mascota(null, "Coco", "Poodle", "1 año", "https://frenchpoodlecolombia.com/wp-content/uploads/2025/12/main-french-poodle-colombia-mini-poodle-tacita-de-te-poodle-rojo-poodle-chocolate-poodle-mini-toy-comprar-poodle-precios-poodle-colombia-4-webp.webp?w=200", "Al día"));
        save(new Mascota(null, "Bella", "Gato Persa", "4 años", "https://static.wikia.nocookie.net/gatopedia/images/0/09/Persa.jpg/revision/latest?cb=20120322010901&path-prefix=es?w=200", "Al día"));
        save(new Mascota(null, "Toby", "Beagle", "2 años", "https://www.zooplus.ie/magazine/wp-content/uploads/2018/05/2-Jahre-Beagle.webpw?=200", "Al día"));
        save(new Mascota(null, "Simba", "Gato Mestizo", "1 año", "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=200", "Falta rabia"));
        save(new Mascota(null, "Milo", "Labrador", "6 años", "https://upload.wikimedia.org/wikipedia/commons/9/9c/Yellow_Labrador_Retriever_2.jpg?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=original?w=200", "Al día"));
        save(new Mascota(null, "Kira", "Husky", "3 años", "https://disane.es/cdn/shop/articles/perros-husky-caracteristicas-y-cuidados-8719786.jpg?v=1780907969?w=200", "Al día"));
        save(new Mascota(null, "Thor", "Pastor Alemán", "4 años", "https://upload.wikimedia.org/wikipedia/commons/9/94/Cane_da_pastore_tedesco_adulto.jpg?utm_source=es.wikipedia.org&utm_campaign=imageinfo&utm_content=thumbnail_unscaled?w=200", "Al día"));
    }

    public List<Mascota> findAll() {
        return new ArrayList<>(database.values());
    }

    public Mascota findById(Integer id) {
        return database.get(id);
    }

    public Mascota save(Mascota mascota) {
        if (mascota.getId() == null) {
            mascota.setId(currentId++);
        }
        database.put(mascota.getId(), mascota);
        return mascota;
    }

    public void delete(Integer id) {
        database.remove(id);
    }
}
