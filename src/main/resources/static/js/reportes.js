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
let todosLosMovimientos = [];

document.addEventListener("DOMContentLoaded", () => {
    cargarMovimientos();
});

async function cargarMovimientos() {
    try {
        const response = await fetch("http://localhost:8080/api/movimientos/reportes");
        const data = await response.json();
        console.log("Datos recibidos:", data);

        // Guardamos todos los registros crudos de movimientos
        todosLosMovimientos = data;

        // Mostrar todos los artículos (con y sin movimientos)
        mostrarMovimientos(todosLosMovimientos);

        // Actualizar dashboard y demás secciones
        actualizarDashboard();
        actualizarTopProductos();
        actualizarUltimosMovimientos();
        // actualizarGraficoEntradasVsSalidas();  // <- comentar hasta que implementes el gráfico
    } catch (error) {
        console.error("Error cargando movimientos:", error);
    }
}

function filtrarMovimientos() {
    const chk = document.getElementById("chkSoloMovimientos");
    if (chk.checked) {
        // 🔹 Filtrar solo artículos con movimientos (entradas o salidas)
        const filtrados = todosLosMovimientos.filter(item => {
            const entradas = Number(item.entradas) || 0;
            const salidas = Number(item.salidas) || 0;
            return entradas !== 0 || salidas !== 0;
        });

        // Mostrar solo los artículos que tuvieron movimientos
        mostrarMovimientos(filtrados, true);
    } else {
        // Mostrar todos los artículos (con y sin movimientos)
        mostrarMovimientos(todosLosMovimientos, false);
    }
}

function mostrarMovimientos(data, soloConMovimientos = false) {
    const tbody = document.getElementById("cuerpoMovimientos");
    if (!tbody) return;
    tbody.innerHTML = "";

    // 🔹 Agrupar movimientos por articulo + año + mes
    const agrupado = {};
    data.forEach(item => {
        const clave = `${item.articulo}-${item.anio}-${item.mes}`;
        if (!agrupado[clave]) {
            agrupado[clave] = {
                articulo: item.articulo,
                anio: item.anio,
                mes: item.mes,
                entradas: 0,
                salidas: 0,
                saldoFinal: item.saldoFinal,
                valorMovido: 0
            };
        }
        agrupado[clave].entradas += Number(item.entradas) || 0;
        agrupado[clave].salidas += Number(item.salidas) || 0;
        agrupado[clave].valorMovido += Number(item.valorMovido) || 0;
    });

    // 🔹 Para cada artículo de la lista principal
    articulos.forEach(art => {
        const claves = Object.keys(agrupado).filter(k => k.startsWith(art.desArt));
        if (claves.length === 0) {
            // Artículo sin movimientos → solo mostrar si NO está filtrando
            if (!soloConMovimientos) {
                const fila = `
                    <tr class="sin-movimiento">
                        <td>${art.desArt}</td>
                        <td>-</td>
                        <td>-</td>
                        <td>0</td>
                        <td>0</td>
                        <td>${art.stk}</td>
                        <td>${window.formatoMoneda(0)}</td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', fila);
            }
        } else {
            // Artículo con movimientos → mostrar agrupados
            claves.forEach(clave => {
                const item = agrupado[clave];
                const claseFila = (item.entradas !== 0 || item.salidas !== 0) ? "con-movimiento" : "sin-movimiento";
                const fila = `
                    <tr class="${claseFila}">
                        <td>${item.articulo}</td>
                        <td>${item.anio}</td>
                        <td>${item.mes}</td>
                        <td>${item.entradas}</td>
                        <td>${item.salidas}</td>
                        <td>${item.saldoFinal}</td>
                        <td>${window.formatoMoneda(item.valorMovido)}</td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', fila);
            });
        }
    });
}

// =======================
// Dashboard dinámico
// =======================

function actualizarDashboard() {
    const totalProductos = articulos.length || 0;

    // ✅ Sumamos valor monetario de los movimientos
    const movimientosMes = todosLosMovimientos.reduce((acc, item) => {
        return acc + (Number(item.valorMovido) || 0);
    }, 0);

    const valorTotal = articulos.reduce((acc, art) => acc + (art.stk * art.costo), 0);

    const cardTotalProductos = document.querySelector('.card-green p');
    const cardMovimientosMes = document.querySelector('.card-blue p');
    const cardValorTotal = document.querySelector('.card-darkgreen p');

    if (cardTotalProductos) cardTotalProductos.textContent = totalProductos;
    if (cardMovimientosMes) cardMovimientosMes.textContent = window.formatoMoneda(movimientosMes);
    if (cardValorTotal) cardValorTotal.textContent = window.formatoMoneda(valorTotal);
}

function actualizarTopProductos() {
    if (!todosLosMovimientos || todosLosMovimientos.length === 0) return;

    const movimientosPorArticulo = {};
    todosLosMovimientos.forEach(item => {
        const entradas = Number(item.entradas) || 0;
        const salidas = Number(item.salidas) || 0;
        const totalMov = entradas + salidas;
        movimientosPorArticulo[item.articulo] = (movimientosPorArticulo[item.articulo] || 0) + totalMov;
    });

    const top = Object.entries(movimientosPorArticulo)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 3);

    const lista = document.querySelector('.card-red ol');
    if (lista) {
        lista.innerHTML = '';
        top.forEach(([articulo, total]) => {
            const li = `<li>${articulo}: ${total} movimientos</li>`;
            lista.insertAdjacentHTML('beforeend', li);
        });
    }
}

function actualizarUltimosMovimientos() {
    if (!todosLosMovimientos || todosLosMovimientos.length === 0) return;

    // Ordenar por año/mes descendente
    const ordenados = [...todosLosMovimientos].sort((a, b) => {
        if (a.anio !== b.anio) return b.anio - a.anio;
        return b.mes - a.mes;
    });

    const ultimos = ordenados.slice(0, 5);

    const lista = document.querySelector('.card-orange ol');
    if (lista) {
        lista.innerHTML = '';
        ultimos.forEach(item => {
            const li = `<li>${item.articulo} (${item.anio}-${item.mes}): ${window.formatoMoneda(item.valorMovido)}</li>`;
            lista.insertAdjacentHTML('beforeend', li);
        });
    }
}
