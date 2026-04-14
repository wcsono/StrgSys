// =======================
// Sección Inventario
// =======================
let sortDir = '';
let sortField = '';

document.addEventListener("DOMContentLoaded", function() {
    console.log("Reportes cargado. La tabla inicial la pinta Thymeleaf.");
});

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

// =======================
// Sección Movimientos
// =======================
let todosLosArticulos = [];

document.addEventListener("DOMContentLoaded", () => {
    cargarTodos();
});

async function cargarTodos() {
    try {
        const response = await fetch("http://localhost:8080/api/kardex/todos");
        const data = await response.json();
        console.log("Datos recibidos:", data);
        todosLosArticulos = data;
        mostrarMovimientos(data);
    } catch (error) {
        console.error("Error cargando movimientos:", error);
    }
}

function filtrarMovimientos() {
    const chk = document.getElementById("chkSoloMovimientos");
    if (chk.checked) {
        const filtrados = todosLosArticulos.filter(item => Number(item.entradas) !== 0 || Number(item.salidas) !== 0);
        mostrarMovimientos(filtrados);
    } else {
        mostrarMovimientos(todosLosArticulos);
    }
}

function mostrarMovimientos(data) {
    const tbody = document.getElementById("cuerpoMovimientos");
    if (!tbody) return;
    tbody.innerHTML = "";

    data.sort((a, b) => {
        if (a.articulo < b.articulo) return -1;
        if (a.articulo > b.articulo) return 1;
        if (a.anio !== b.anio) return a.anio - b.anio;
        return a.mes - b.mes;
    });

    data.forEach(item => {
        const entradas = Number(item.entradas) || 0;
        const salidas = Number(item.salidas) || 0;
        const claseFila = (entradas !== 0 || salidas !== 0) ? "con-movimiento" : "sin-movimiento";

        const fila = `
            <tr class="${claseFila}">
                <td>${item.articulo}</td>
                <td>${item.anio}</td>
                <td>${item.mes}</td>
                <td>${entradas}</td>
                <td>${salidas}</td>
                <td>${item.saldoFinal}</td>
                <td>${item.valorMovido}</td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', fila);
    });
}

// =======================
// Sección Alertas / Gráficos
// =======================
// Aquí puedes añadir funciones para manejar alertas o gráficos si lo necesitas.