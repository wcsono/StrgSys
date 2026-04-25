// =======================
// Dashboard dinámico
// =======================

function actualizarDashboard() {
    console.log("todosLosMovimientos:", todosLosMovimientos);

    const totalProductos = articulos.length || 0;

    const fecha = new Date();
    const mesActual = fecha.getMonth() + 1;
    const anioActual = fecha.getFullYear();

    // ✅ Sumar el valor monetario movido del mes actual
    const valorMovidoMes = todosLosMovimientos.reduce((acc, item) => {
        if (Number(item.anio) === anioActual && Number(item.mes) === mesActual) {
            acc += (Number(item.valorMovido) || 0);
        }
        return acc;
    }, 0);

    const valorTotal = articulos.reduce((acc, art) => acc + (art.stk * art.costo), 0);

    const cardTotalProductos = document.querySelector('.card-green p');
    const cardMovimientosMes = document.querySelector('.card-blue p');
    const cardValorTotal = document.querySelector('.card-darkgreen p');

    if (cardTotalProductos) cardTotalProductos.textContent = totalProductos;
    if (cardMovimientosMes) cardMovimientosMes.textContent = window.formatoMoneda(valorMovidoMes);
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

    const fecha = new Date();
    const mesActual = fecha.getMonth() + 1;
    const anioActual = fecha.getFullYear();

    let ingresos = 0;
    let salidas = 0;

    todosLosMovimientos.forEach(item => {
        if (Number(item.anio) === anioActual && Number(item.mes) === mesActual) {
            const valor = Number(item.valorMovido) || 0;
            const entradas = Number(item.entradas) || 0;
            const salidasUnidades = Number(item.salidas) || 0;

            if (entradas > 0) ingresos += valor;
            if (salidasUnidades > 0) salidas += valor;
        }
    });

    const lista = document.querySelector('.card-yellow ul');
    if (lista) {
        lista.innerHTML = '';
        const liIngresos = `<li><strong>Ingresos:</strong> ${window.formatoMoneda(ingresos)}</li>`;
        const liSalidas = `<li><strong>Salidas:</strong> ${window.formatoMoneda(salidas)}</li>`;
        const liBalance = `<li><strong>Balance:</strong> ${window.formatoMoneda(ingresos - salidas)}</li>`;
        lista.insertAdjacentHTML('beforeend', liIngresos);
        lista.insertAdjacentHTML('beforeend', liSalidas);
        lista.insertAdjacentHTML('beforeend', liBalance);
    }
}


function actualizarGraficoEntradasVsSalidas() {
    const ctx = document.getElementById('graficoEntradasVsSalidas').getContext('2d');
    const fecha = new Date();
    const anioActual = fecha.getFullYear();

    const entradasPorMes = Array(12).fill(0);
    const salidasPorMes = Array(12).fill(0);

    todosLosMovimientos.forEach(item => {
        if (Number(item.anio) === anioActual) {
            const idx = Number(item.mes) - 1;
            entradasPorMes[idx] += Number(item.entradas) || 0;
            salidasPorMes[idx] += Number(item.salidas) || 0;
        }
    });

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Ene','Feb','Mar','Abr','May','Jun','Jul','Ago','Sep','Oct','Nov','Dic'],
            datasets: [
                { label: 'Entradas', data: entradasPorMes, backgroundColor: '#28a745' },
                { label: 'Salidas', data: salidasPorMes, backgroundColor: '#dc3545' }
            ]
        },
        options: { responsive: true, plugins: { legend: { position: 'top' } } }
    });
}
//_____________________________________
// Modal de los cards del Dashbboard
//_____________________________________
const modalBody = document.getElementById("modal-body");
const modalTitle = document.querySelector("#cardModal .modal-title");
const cardModal = new bootstrap.Modal(document.getElementById("cardModal"));

document.querySelectorAll('.dashboard .card').forEach(card => {
  card.addEventListener('click', () => {
    const header = card.querySelector('h4')?.textContent || "Detalle";
    const contenido = card.innerHTML.replace(/<h4[^>]*>.*?<\/h4>/, "");

    modalTitle.textContent = header;
    modalBody.innerHTML = contenido;

    const modalContent = document.querySelector("#cardModal .modal-content");
    modalContent.className = "modal-content " + card.className.replace("card", "").trim();

    cardModal.show();
  });
});
