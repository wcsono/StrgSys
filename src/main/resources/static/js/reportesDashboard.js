// =======================
// Dashboard dinámico
// =======================

function actualizarDashboard() {
    const totalProductos = articulos.length || 0;

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
            lista.insertAdjacentHTML('