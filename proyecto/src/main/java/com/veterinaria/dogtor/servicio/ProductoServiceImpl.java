package com.veterinaria.dogtor.servicio;
import com.veterinaria.dogtor.errores.ResourceNotFoundException;

import com.veterinaria.dogtor.entidad.Producto;
import com.veterinaria.dogtor.repositorio.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> searchAll() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> findByCategoria(com.veterinaria.dogtor.entidad.CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto searchById(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }
}
