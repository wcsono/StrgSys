// =======================
// Sección Inventario
// =======================
let sortDir = '';
let sortField = '';

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

function ordenarPor(campo) {
    if (sortField !== campo) {
        sortDir = 'asc';
        sortField = campo;
    } else {
        sortDir = sortDir === 'asc' ? 'desc' : 'asc';
    }

    articulos.sort((a, b) => {
        let valA = a[campo];
        let valB = b[campo];

        if (!isNaN(valA) && !isNaN(valB)) {
            valA = Number(valA);
            valB = Number(valB);
        } else {
            if (typeof valA === 'string') valA = valA.toLowerCase();
            if (typeof valB === 'string') valB = valB.toLowerCase();
        }

        if (valA < valB) return sortDir === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir === 'asc' ? 1 : -1;
        return 0;
    });

    mostrarArticulos(articulos);
    actualizarIconos(campo, sortDir);
}

function actualizarIconos(campo, dir) {
    document.querySelectorAll('thead span').forEach(span => span.className = 'both');
    document.querySelectorAll('thead th').forEach(th => {
        th.classList.remove('active-sort');
        th.title = "Presionar para ordenar";
        th.removeAttribute("aria-sort");
    });

    const icon = document.getElementById('icon-' + campo);
    const th = document.getElementById('th-' + campo);
    if (icon) icon.className = dir;
    if (th) {
        th.classList.add('active-sort');
        th.title = dir === 'asc' ? "Orden ascendente" : "Orden descendente";
        th.setAttribute("aria-sort", dir === 'asc' ? "ascending" : "descending");
    }
}
