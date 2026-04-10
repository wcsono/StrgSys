// Variables globales
let sortDir = '';
let sortField = '';

// Al cargar la página, dibujamos la tabla con los artículos que Thymeleaf inyectó
document.addEventListener("DOMContentLoaded", function() {
    mostrarArticulos(articulos);
});

// Renderiza el cuerpo de la tabla con formato de moneda
function mostrarArticulos(lista) {
    const cuerpo = document.getElementById('cuerpoInventario');
    if (!cuerpo) return;
    cuerpo.innerHTML = '';
    lista.forEach(art => {
        const fila = `
            <tr>
                <td>${art.codArt}</td>
                <td>${art.desArt}</td>
                <td>${art.stk}</td>
                <td>${window.formatoMoneda(art.costo)}</td>
                <td>${window.formatoMoneda(art.stk * art.costo)}</td>
            </tr>`;
        cuerpo.insertAdjacentHTML('beforeend', fila);
    });
}

// Ordena por el campo indicado
function ordenarPor(campo) {
    // Si cambiamos de columna, reiniciamos dirección
    if (sortField !== campo) {
        sortDir = 'asc';
        sortField = campo;
    } else {
        sortDir = sortDir === 'asc' ? 'desc' : 'asc';
    }

    // Ordenamiento genérico
    articulos.sort((a, b) => {
        let valA = a[campo];
        let valB = b[campo];

        // Normalizar strings
        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        if (valA < valB) return sortDir === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir === 'asc' ? 1 : -1;
        return 0;
    });

    // Redibujar tabla y actualizar iconos
    mostrarArticulos(articulos);
    actualizarIconos(campo, sortDir);
}

// Actualiza iconos y tooltips dinámicamente
function actualizarIconos(campo, dir) {
    // Todas las columnas vuelven al estado inicial
    document.querySelectorAll('thead span').forEach(span => span.className = 'both');
    document.querySelectorAll('thead th').forEach(th => {
        th.classList.remove('active-sort');
        th.title = "Presionar para ordenar";
    });

    // Solo la columna activa cambia dinámicamente
    const icon = document.getElementById('icon-' + campo);
    const th = document.getElementById('th-' + campo);
    if (icon) icon.className = dir;
    if (th) {
        th.classList.add('active-sort');
        th.title = dir === 'asc' ? "Orden ascendente" : "Orden descendente";
    }
}