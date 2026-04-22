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

        todosLosMovimientos = data;

        // Mostrar todos los artículos (con y sin movimientos)
        mostrarMovimientos(todosLosMovimientos);

        // Actualizar dashboard (funciones están en reportesDashboard.js)
        actualizarDashboard();
        actualizarTopProductos();
        actualizarUltimosMovimientos();
        // actualizarGraficoEntradasVsSalidas(); // pendiente
    } catch (error) {
        console.error("Error cargando movimientos:", error);
    }
}

function filtrarMovimientos() {
    const chk = document.getElementById("chkSoloMovimientos");
    if (chk.checked) {
        const filtrados = todosLosMovimientos.filter(item => {
            const entradas = Number(item.entradas) || 0;
            const salidas = Number(item.salidas) || 0;
            return entradas !== 0 || salidas !== 0;
        });
        mostrarMovimientos(filtrados, true);
    } else {
        mostrarMovimientos(todosLosMovimientos, false);
    }
}

function mostrarMovimientos(data, soloConMovimientos = false) {
    const tbody = document.getElementById("cuerpoMovimientos");
    if (!tbody) return;
    tbody.innerHTML = "";

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

    articulos.forEach(art => {
        const claves = Object.keys(agrupado).filter(k => k.startsWith(art.desArt));
        if (claves.length === 0) {
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
