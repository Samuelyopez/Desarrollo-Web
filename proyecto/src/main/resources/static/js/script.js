
document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. Carousel Logic ---
    const slides = document.querySelectorAll('.carousel-slide');
    if (slides.length > 0) {
        let activeIndex = 0;
        const prevBtn = document.getElementById('carousel-prev');
        const nextBtn = document.getElementById('carousel-next');

        function showSlide(index) {
            slides.forEach((slide, i) => {
                if (i === index) {
                    slide.classList.remove('opacity-0', 'z-0', 'hidden');
                    slide.classList.add('opacity-100', 'z-10');
                } else {
                    slide.classList.remove('opacity-100', 'z-10');
                    slide.classList.add('opacity-0', 'z-0', 'hidden');
                }
            });
        }

        if (prevBtn && nextBtn) {
            prevBtn.addEventListener('click', () => {
                activeIndex = activeIndex === 0 ? slides.length - 1 : activeIndex - 1;
                showSlide(activeIndex);
            });
            nextBtn.addEventListener('click', () => {
                activeIndex = activeIndex === slides.length - 1 ? 0 : activeIndex + 1;
                showSlide(activeIndex);
            });
        }
    }

    // --- 2. Popover Logic (Pacientes) ---
    const popoverBtns = document.querySelectorAll('.toggle-popover-btn');
    popoverBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            // Close other popovers
            document.querySelectorAll('.popover-content').forEach(p => {
                if (p !== btn.nextElementSibling) {
                    p.classList.add('hidden');
                }
            });
            // Toggle current
            const popover = btn.nextElementSibling;
            if (popover && popover.classList.contains('popover-content')) {
                popover.classList.toggle('hidden');
            }
        });
    });

    // Close popover when clicking outside
    document.addEventListener('click', () => {
        document.querySelectorAll('.popover-content').forEach(p => p.classList.add('hidden'));
    });

    // --- 3. Shopping Cart Logic ---
    let cart = JSON.parse(localStorage.getItem('veterinaria_cart')) || [];
    
    const openCartBtn = document.getElementById('open-cart-btn');
    const closeCartBtn = document.getElementById('close-cart-btn');
    const cartDrawer = document.getElementById('cart-drawer');
    const cartPanel = document.getElementById('cart-panel');
    const cartCount = document.getElementById('cart-count');
    const emptyCartMsg = document.getElementById('empty-cart-msg');
    const cartItemsList = document.getElementById('cart-items-list');
    const cartTotal = document.getElementById('cart-total');
    const checkoutBtn = document.getElementById('checkout-btn');

    function saveCart() {
        localStorage.setItem('veterinaria_cart', JSON.stringify(cart));
    }

    function updateCartUI() {
        if (!cartCount) return;

        cartCount.textContent = cart.length;
        if (cart.length > 0) {
            cartCount.classList.remove('hidden');
            emptyCartMsg.classList.add('hidden');
            checkoutBtn.disabled = false;
        } else {
            cartCount.classList.add('hidden');
            emptyCartMsg.classList.remove('hidden');
            checkoutBtn.disabled = true;
        }

        cartItemsList.innerHTML = '';
        let total = 0;

        cart.forEach((item, index) => {
            total += parseFloat(item.price);
            const li = document.createElement('li');
            li.className = 'flex justify-between items-center border-b pb-2';
            li.innerHTML = `
                <div>
                    <p class="font-bold text-gray-800">${item.name}</p>
                    <p class="text-sm text-teal-600">$${item.price}</p>
                </div>
                <button class="remove-item-btn text-red-400 hover:text-red-600" data-index="${index}">
                    X
                </button>
            `;
            cartItemsList.appendChild(li);
        });

        cartTotal.textContent = '$' + total.toLocaleString('es-CO');

        // Add event listeners to remove buttons
        document.querySelectorAll('.remove-item-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const idx = parseInt(e.target.getAttribute('data-index'));
                cart.splice(idx, 1);
                saveCart();
                updateCartUI();
            });
        });
    }

    if (openCartBtn && cartDrawer) {
        openCartBtn.addEventListener('click', () => {
            cartDrawer.classList.remove('hidden');
            setTimeout(() => {
                cartPanel.classList.remove('translate-x-full');
            }, 10);
            updateCartUI();
        });

        closeCartBtn.addEventListener('click', () => {
            cartPanel.classList.add('translate-x-full');
            setTimeout(() => {
                cartDrawer.classList.add('hidden');
            }, 300);
        });
    }

    // Add to cart buttons
    const addBtns = document.querySelectorAll('.add-to-cart-btn');
    addBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const name = btn.getAttribute('data-nombre');
            const price = btn.getAttribute('data-precio');
            cart.push({ name, price });
            saveCart();
            updateCartUI();
            
            // Visual feedback
            const originalText = btn.textContent;
            btn.textContent = '¡Agregado!';
            btn.classList.replace('bg-secondary', 'bg-green-500');
            setTimeout(() => {
                btn.textContent = originalText;
                btn.classList.replace('bg-green-500', 'bg-secondary');
            }, 1000);
        });
    });

    // Initialize UI
    updateCartUI();
});
