const fs = require('fs');

const header = `<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DogTor | Clínica Veterinaria</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: '#2A9D8F',
                        secondary: '#F4A261',
                        accent: '#E9C46A',
                        bglight: '#FAFAFA',
                        textdark: '#264653'
                    }
                }
            }
        }
    </script>
    <link rel="stylesheet" href="css/style.css">
    <!-- Alpine.js para interactividad (modales y carrusel) -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"></script>
</head>
<body class="bg-bglight text-textdark font-sans flex flex-col min-h-screen">
    <header class="bg-white shadow-md sticky top-0 z-50">
        <div class="container mx-auto px-6 py-4 flex flex-col md:flex-row justify-between items-center">
            <a href="index.html" class="flex items-center gap-2 text-primary font-bold text-2xl mb-4 md:mb-0">DogTor</a>
            <nav class="flex flex-wrap justify-center gap-4 items-center text-sm font-medium">
                <a href="index.html" class="hover:text-primary">Inicio</a>
                <a href="nosotros.html" class="hover:text-primary">Nosotros</a>
                <a href="equipo.html" class="hover:text-primary">Equipo</a>
                <a href="farmacia.html" class="hover:text-primary">Tienda</a>
                <a href="adopciones.html" class="hover:text-primary">Adopciones</a>
                <a href="pacientes.html" class="hover:text-primary">Pacientes</a>
                <a href="login.html" class="hover:text-primary">Login</a>
                <a href="citas.html" class="bg-secondary text-white px-5 py-2 rounded-lg font-bold hover:bg-orange-500 shadow-md">Agendar Cita</a>
            </nav>
        </div>
    </header>
`;

const footer = `
    <footer class="bg-textdark text-gray-300 py-8 px-6 mt-auto">
        <div class="container mx-auto text-center">
            <p>&copy; 2026 DogTor Clínica Veterinaria. Todos los derechos reservados.</p>
        </div>
    </footer>
</body>
</html>`;

const pages = {};

// 1. INDEX
pages['index.html'] = `
<main class="flex-grow">
    <!-- Carrusel de Acceso Rápido -->
    <section x-data="{ activeSlide: 0, slides: [
        { img: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=1200', title: 'Reserva tu Cita', desc: 'Atención 24/7 para tu mascota.', link: 'citas.html', btn: 'Agendar Ahora' },
        { img: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=1200', title: 'Adopta un Amigo', desc: 'Conoce a las mascotas que buscan hogar.', link: 'adopciones.html', btn: 'Ver Adopciones' },
        { img: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=1200', title: 'Nuestra Tienda', desc: 'Los mejores productos medicinales y accesorios.', link: 'farmacia.html', btn: 'Ir a Tienda' },
        { img: 'https://images.unsplash.com/photo-1537151608804-ea6f117f73d2?w=1200', title: 'Nuestro Equipo', desc: 'Conoce a los especialistas de DogTor.', link: 'equipo.html', btn: 'Ver Equipo' }
    ]}" class="relative w-full h-[500px] overflow-hidden bg-gray-900">
        <template x-for="(slide, index) in slides" :key="index">
            <div x-show="activeSlide === index" class="absolute inset-0 transition-opacity duration-500">
                <img :src="slide.img" class="w-full h-full object-cover opacity-60">
                <div class="absolute inset-0 flex flex-col items-center justify-center text-center px-4">
                    <h2 class="text-4xl md:text-6xl font-bold text-white mb-4" x-text="slide.title"></h2>
                    <p class="text-xl text-white mb-8" x-text="slide.desc"></p>
                    <a :href="slide.link" class="bg-primary text-white px-8 py-3 rounded-lg font-bold text-lg hover:bg-teal-700" x-text="slide.btn"></a>
                </div>
            </div>
        </template>
        <button @click="activeSlide = activeSlide === 0 ? slides.length - 1 : activeSlide - 1" class="absolute left-4 top-1/2 -translate-y-1/2 bg-white/30 p-2 rounded-full text-white hover:bg-white/50">◄</button>
        <button @click="activeSlide = activeSlide === slides.length - 1 ? 0 : activeSlide + 1" class="absolute right-4 top-1/2 -translate-y-1/2 bg-white/30 p-2 rounded-full text-white hover:bg-white/50">►</button>
    </section>

    <!-- Info Rápida -->
    <section class="py-16 px-6 bg-white text-center">
        <h2 class="text-3xl font-bold text-primary mb-8">Bienvenidos a DogTor</h2>
        <p class="text-gray-600 max-w-2xl mx-auto text-lg">Usa el menú superior para navegar directamente a todas las páginas de nuestra clínica.</p>
    </section>
</main>
`;

// 2. PACIENTES (10)
const patients = [
    {n: 'Max', r: 'Golden Retriever', e: '3 años', i: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=200'},
    {n: 'Luna', r: 'Gato Siamés', e: '2 años', i: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=200'},
    {n: 'Rocky', r: 'Bulldog', e: '5 años', i: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?w=200'},
    {n: 'Coco', r: 'Poodle', e: '1 año', i: 'https://images.unsplash.com/photo-1588145899933-255d6b38c205?w=200'},
    {n: 'Bella', r: 'Gato Persa', e: '4 años', i: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=200'},
    {n: 'Toby', r: 'Beagle', e: '2 años', i: 'https://images.unsplash.com/photo-1537151608804-ea6f117f73d2?w=200'},
    {n: 'Simba', r: 'Gato Mestizo', e: '1 año', i: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=200'},
    {n: 'Milo', r: 'Labrador', e: '6 años', i: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=200'},
    {n: 'Kira', r: 'Husky', e: '3 años', i: 'https://images.unsplash.com/photo-1605568420125-4eb84e4f58c7?w=200'},
    {n: 'Thor', r: 'Pastor Alemán', e: '4 años', i: 'https://images.unsplash.com/photo-1589965716319-4a041b117f92?w=200'}
];
let patientCards = '';
patients.forEach((p) => {
patientCards += '<div @click="openModal = true; selected = {name: ' + ''' + p.n + ''' + ', breed: ' + ''' + p.r + ''' + ', img: ' + ''' + p.i + '''} " class="bg-white p-4 rounded-xl shadow cursor-pointer hover:shadow-lg border border-gray-100 flex items-center gap-4"><img src="' + p.i + '" class="w-16 h-16 rounded-full object-cover"><div><h3 class="font-bold text-primary text-lg">' + p.n + '</h3><p class="text-sm text-gray-500">' + p.r + ' � ' + p.e + '</p></div></div>';
});
pages['pacientes.html'] = '<main x-data="{ openModal: false, selected: {} }" class="flex-grow bg-bglight py-12 px-6"><div class="container mx-auto"><h2 class="text-3xl font-bold text-textdark mb-8 text-center">Tus Mascotas</h2><div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">' + patientCards + '</div></div><!-- Modal --><div x-show="openModal" class="fixed inset-0 bg-black/50 z-50 flex justify-end" style="display:none;"><div @click.away="openModal = false" class="w-full max-w-md bg-white h-full shadow-2xl p-6 flex flex-col overflow-y-auto"><button @click="openModal = false" class="self-end text-gray-500 hover:text-red-500 font-bold mb-4">X Cerrar</button><img :src="selected.img" class="w-full h-48 object-cover rounded-xl mb-4"><h3 class="text-3xl font-bold text-primary" x-text="selected.name"></h3><p class="text-gray-600 mb-4" x-text="selected.breed"></p><div class="bg-teal-50 p-4 rounded-lg flex-grow"><h4 class="font-bold mb-2">Historial Médico</h4><p><strong>Última consulta:</strong> Hace 2 meses</p><p><strong>Vacunas:</strong> Al día</p></div></div></div></main>';

// 3. FARMACIA Y ACCESORIOS (10)
const products = [
    {n: 'Probiótico Forte', p: '$45.000', c: 'Farmacia', img: 'https://images.unsplash.com/photo-1584308666744-24d5e478ac5c?w=200'}, 
    {n: 'Antipulgas Plus', p: '$60.000', c: 'Farmacia', img: 'https://images.unsplash.com/photo-1584308666744-24d5e478ac5c?w=200'}, 
    {n: 'Vitamina C Canina', p: '$30.000', c: 'Farmacia', img: 'https://images.unsplash.com/photo-1584308666744-24d5e478ac5c?w=200'},
    {n: 'Shampoo Piel', p: '$25.000', c: 'Farmacia', img: 'https://images.unsplash.com/photo-1584308666744-24d5e478ac5c?w=200'}, 
    {n: 'Gotas Óticas', p: '$35.000', c: 'Farmacia', img: 'https://images.unsplash.com/photo-1584308666744-24d5e478ac5c?w=200'}, 
    {n: 'Juguete Kong', p: '$55.000', c: 'Accesorio', img: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=200'},
    {n: 'Correa Retráctil 5m', p: '$40.000', c: 'Accesorio', img: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=200'}, 
    {n: 'Cama Ortopédica L', p: '$120.000', c: 'Accesorio', img: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=200'}, 
    {n: 'Rascador Gato', p: '$80.000', c: 'Accesorio', img: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=200'},
    {n: 'Collar LED', p: '$20.000', c: 'Accesorio', img: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=200'}
];
let prodCards = '';
products.forEach(p => {
prodCards += '<div class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col"><div class="h-32 bg-gray-200 rounded-lg mb-4 flex items-center justify-center overflow-hidden"><img src="' + p.img + '" class="w-full h-full object-cover opacity-80"></div><h4 class="font-bold text-textdark">' + p.n + '</h4><p class="text-primary font-bold text-lg my-2">' + p.p + '</p><button @click="cartOpen = true; item = ' + ''' + p.n + ''' + ' " class="mt-auto bg-secondary text-white py-2 rounded-lg font-bold hover:bg-orange-500">Comprar</button></div>';
});
pages['farmacia.html'] = '<main x-data="{ cartOpen: false, item: \\'\\' }" class="flex-grow bg-bglight py-12 px-6"><div class="container mx-auto text-center mb-8"><h2 class="text-3xl font-bold text-primary">Nuestra Tienda</h2><p class="text-gray-600">Encuentra farmacia y accesorios en un solo lugar.</p></div><div class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">' + prodCards + '</div><!-- Modal Carrito --><div x-show="cartOpen" class="fixed inset-0 bg-black/50 z-50 flex justify-end" style="display:none;"><div @click.away="cartOpen = false" class="w-full max-w-sm bg-white h-full shadow-2xl p-6 flex flex-col"><button @click="cartOpen = false" class="self-end text-gray-500 hover:text-red-500 font-bold mb-4">X Cerrar</button><h3 class="text-2xl font-bold text-primary mb-4">Tu Carrito</h3><p>Has agregado <strong x-text="item"></strong> al carrito.</p><div class="mt-auto border-t pt-4"><button class="w-full bg-primary text-white py-3 rounded-lg font-bold">Ir a Pagar</button></div></div></div></main>';

// 4. ADOPCIONES (10)
const adoptions = [
    {n: 'Manchas', r: 'Dálmata Mix', i: 'https://images.unsplash.com/photo-1537151608804-ea6f117f73d2?w=300'},
    {n: 'Oreo', r: 'Gato Blanco y Negro', i: 'https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=300'},
    {n: 'Rex', r: 'Mestizo Mediano', i: 'https://images.unsplash.com/photo-1517849845537-4d257902454a?w=300'},
    {n: 'Canela', r: 'Poodle Mix', i: 'https://images.unsplash.com/photo-1588145899933-255d6b38c205?w=300'},
    {n: 'Pelusa', r: 'Gato Angora', i: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=300'},
    {n: 'Boby', r: 'Beagle Mix', i: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=300'},
    {n: 'Garfield', r: 'Gato Naranja', i: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=300'},
    {n: 'Lola', r: 'Labrador Mix', i: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=300'},
    {n: 'Zeus', r: 'Husky Mix', i: 'https://images.unsplash.com/photo-1605568420125-4eb84e4f58c7?w=300'},
    {n: 'Dante', r: 'Pastor Mix', i: 'https://images.unsplash.com/photo-1589965716319-4a041b117f92?w=300'}
];
let adCards = '';
adoptions.forEach(a => {
    adCards += '<div class="bg-white rounded-xl shadow overflow-hidden"><img src="' + a.i + '" class="w-full h-48 object-cover"><div class="p-4"><h3 class="font-bold text-xl">' + a.n + '</h3><p class="text-gray-500 mb-4">' + a.r + '</p><button class="w-full bg-accent text-textdark font-bold py-2 rounded-lg hover:bg-yellow-500 transition" onclick="alert(\\'Solicitud de adopción enviada\\')">Quiero adoptarlo</button></div></div>';
});
pages['adopciones.html'] = '<main class="flex-grow bg-bglight py-12 px-6"><div class="container mx-auto text-center mb-8"><h2 class="text-3xl font-bold text-primary">Mascotas en Adopción</h2><p>Dales una segunda oportunidad.</p></div><div class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">' + adCards + '</div></main>';

// 5. EQUIPO (3)
pages['equipo.html'] = '<main class="flex-grow bg-bglight py-12 px-6"><div class="container mx-auto text-center mb-12"><h2 class="text-3xl font-bold text-primary">Nuestro Equipo Médico</h2></div><div class="container mx-auto grid grid-cols-1 md:grid-cols-3 gap-8"><div class="bg-white p-6 rounded-2xl shadow text-center border-t-4 border-primary"><img src="https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200" class="w-32 h-32 rounded-full mx-auto object-cover mb-4"><h3 class="text-xl font-bold">Dra. Ana López</h3><p class="text-secondary font-bold text-sm mb-2">Cirujana Veterinaria</p><p class="text-gray-600 text-sm">10 años de experiencia salvando vidas.</p></div><div class="bg-white p-6 rounded-2xl shadow text-center border-t-4 border-primary"><img src="https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200" class="w-32 h-32 rounded-full mx-auto object-cover mb-4"><h3 class="text-xl font-bold">Dr. Carlos Ruiz</h3><p class="text-secondary font-bold text-sm mb-2">Odontólogo Veterinario</p><p class="text-gray-600 text-sm">Especialista en profilaxis y salud dental.</p></div><div class="bg-white p-6 rounded-2xl shadow text-center border-t-4 border-primary"><img src="https://images.unsplash.com/photo-1594824436968-3014798e38f9?w=200" class="w-32 h-32 rounded-full mx-auto object-cover mb-4"><h3 class="text-xl font-bold">Dra. María Gómez</h3><p class="text-secondary font-bold text-sm mb-2">Comportamiento Felino</p><p class="text-gray-600 text-sm">Experta en gatos y cuidado sin estrés.</p></div></div></main>';

// 6. CITAS
pages['citas.html'] = '<main class="flex-grow flex items-center justify-center bg-teal-50 py-12 px-4"><div class="bg-white p-8 rounded-3xl shadow-xl w-full max-w-lg border border-teal-100"><h2 class="text-3xl font-bold text-primary mb-2 text-center">Agendar Cita</h2><p class="text-center text-gray-500 mb-6">Elige el servicio que necesita tu mascota</p><form class="flex flex-col gap-4"><div><label class="block text-sm font-bold text-gray-700 mb-1">Tu Nombre</label><input type="text" class="w-full p-3 border rounded-lg outline-none focus:border-primary"></div><div><label class="block text-sm font-bold text-gray-700 mb-1">Mascota</label><select class="w-full p-3 border rounded-lg"><option>Perro</option><option>Gato</option><option>Otro</option></select></div><div><label class="block text-sm font-bold text-gray-700 mb-1">Especialista</label><select class="w-full p-3 border rounded-lg"><option>Dra. Ana (Cirugía)</option><option>Dr. Carlos (Odontología)</option><option>Dra. María (Gatos)</option></select></div><div><label class="block text-sm font-bold text-gray-700 mb-1">Fecha</label><input type="date" class="w-full p-3 border rounded-lg"></div><button type="submit" class="w-full bg-primary text-white font-bold py-3 rounded-xl hover:bg-teal-700 mt-2">Confirmar Cita</button></form></div></main>';

// 7. LOGIN
pages['login.html'] = '<main class="flex-grow flex items-center justify-center bg-teal-50 py-12 px-4"><div class="bg-white p-8 rounded-3xl shadow-xl max-w-md w-full"><h2 class="text-3xl font-bold text-primary mb-6 text-center">Login DogTor</h2><form class="flex flex-col gap-4"><div><label class="block text-sm font-bold mb-1">Correo</label><input type="email" class="w-full p-3 border rounded-lg outline-none focus:border-primary" required></div><div><label class="block text-sm font-bold mb-1">Contraseña</label><input type="password" class="w-full p-3 border rounded-lg outline-none focus:border-primary" required></div><button class="w-full bg-secondary text-white font-bold py-3 rounded-xl hover:bg-orange-500 mt-2">Ingresar</button></form></div></main>';

// Write pages
for (let [filename, content] of Object.entries(pages)) {
    fs.writeFileSync(filename, header + '\\n' + content + '\\n' + footer);
    console.log("Reconstruido:", filename);
}

// Ensure Nosotros keeps content
try { 
    let nos = fs.readFileSync('nosotros.html', 'utf8'); 
    let mainMatch = nos.match(/<main[\\s\\S]*?<\\/main>/i);
    if(mainMatch) fs.writeFileSync('nosotros.html', header + '\\n' + mainMatch[0] + '\\n' + footer);
} catch(e){}

