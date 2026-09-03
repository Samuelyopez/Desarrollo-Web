/**
 * script.js - Lógica e interactividad para DogTor
 * Utiliza estándares de ES6+ (const, let, querySelector, arrow functions)
 */

document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Manejo del Menú Móvil
    const mobileMenuBtn = document.querySelector('#mobile-menu-btn');
    const mobileMenu = document.querySelector('#mobile-menu');

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', () => {
            // Alternar la visibilidad del menú
            mobileMenu.classList.toggle('hidden');
            mobileMenu.classList.toggle('flex');
        });
    }

    // 2. Validación de Formulario en el lado del cliente
    const contactForm = document.querySelector('#contact-form');
    const formFeedback = document.querySelector('#form-feedback');

    if (contactForm) {
        contactForm.addEventListener('submit', (e) => {
            // Prevenir recarga de página obligatoria por requerimientos
            e.preventDefault();

            // Seleccionar los campos
            const nombre = document.querySelector('#nombre').value.trim();
            const email = document.querySelector('#email').value.trim();
            const mensaje = document.querySelector('#mensaje').value.trim();

            // Expresión Regular para correo básico
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (nombre === '' || email === '' || mensaje === '') {
                showFeedback('Por favor, completa todos los campos obligatorios.', 'text-red-600');
                return;
            }

            if (!emailRegex.test(email)) {
                showFeedback('Por favor, ingresa un correo electrónico válido.', 'text-red-600');
                return;
            }

            // Simular envío exitoso
            showFeedback('¡Mensaje enviado con éxito! Te contactaremos pronto.', 'text-primary');
            contactForm.reset();
        });
    }

    // Función auxiliar para mostrar retroalimentación
    const showFeedback = (mensaje, colorClass) => {
        if (formFeedback) {
            formFeedback.textContent = mensaje;
            formFeedback.className = `text-center text-sm font-semibold mt-4 block ${colorClass}`;
            
            // Ocultar mensaje después de 4 segundos
            setTimeout(() => {
                formFeedback.classList.add('hidden');
                formFeedback.classList.remove('block');
            }, 4000);
        }
    };

    // 3. Lógica del Carrusel (Vanilla JS)
    const track = document.querySelector('#carousel-track');
    const slides = Array.from(track ? track.children : []);
    const nextButton = document.querySelector('#next-slide');
    const prevButton = document.querySelector('#prev-slide');
    const dotsNav = document.querySelector('#carousel-dots');
    const dots = Array.from(dotsNav ? dotsNav.children : []);

    if (track && slides.length > 0) {
        let currentSlideIndex = 0;

        const updateCarousel = (index) => {
            // Mover el contenedor completo
            track.style.transform = `translateX(-${index * 100}%)`;
            
            // Actualizar indicadores (dots)
            dots.forEach(dot => {
                dot.classList.remove('bg-primary');
                dot.classList.add('bg-white/70');
            });
            dots[index].classList.add('bg-primary');
            dots[index].classList.remove('bg-white/70');
        };

        const moveToNextSlide = () => {
            currentSlideIndex = (currentSlideIndex + 1) % slides.length;
            updateCarousel(currentSlideIndex);
        };

        const moveToPrevSlide = () => {
            currentSlideIndex = (currentSlideIndex - 1 + slides.length) % slides.length;
            updateCarousel(currentSlideIndex);
        };

        // Listeners de botones
        if (nextButton) nextButton.addEventListener('click', moveToNextSlide);
        if (prevButton) prevButton.addEventListener('click', moveToPrevSlide);

        // Listeners de los indicadores (dots)
        if (dotsNav) {
            dotsNav.addEventListener('click', (e) => {
                const targetDot = e.target.closest('button');
                if (!targetDot) return;
                
                const targetIndex = dots.findIndex(dot => dot === targetDot);
                if(targetIndex !== -1) {
                    currentSlideIndex = targetIndex;
                    updateCarousel(currentSlideIndex);
                }
            });
        }

        // Autoplay del Carrusel cada 5 segundos
        setInterval(moveToNextSlide, 5000);
    }
});
