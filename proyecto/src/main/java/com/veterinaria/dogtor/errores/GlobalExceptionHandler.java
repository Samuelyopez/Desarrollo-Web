package com.veterinaria.dogtor.errores;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(
        ResourceNotFoundException ex,
        Model model
    ) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(
        Exception ex,
        Model model
    ) {
        model.addAttribute("mensaje", "Ocurrió un error inesperado: " + ex.getMessage());
        return "error";
    }
}
