// =======================
// Dashboard dinámico
// =======================

document.addEventListener("DOMContentLoaded", () => {
    actualizarDashboard();
    actualizarTopProductos();
    actualizarUltimosMovimientos();
    actualizarGraficoEntradasVsSalidas();
});

function actualizarDashboard() {
    const totalProductos = articulos.length || 0;
    const fecha = new Date();
    const mesActual = fecha.getMonth() + 1;
    const anioActual = fecha.getFullYear();

    const valorMovidoMes = todosLosMovimientos.reduce((acc, item) => {
        if (Number(item.anio) === anioActual && Number(item.mes) === mesActual) {
            acc += (Number(item.valorMovido) || 0);
        }
        return acc;
    }, 0);

    const valorTotal = articulos.reduce((acc, art) => acc + (art.stk * art.costo), 0);

    document.querySelector('.card-green p').textContent = totalProductos;
    document.querySelector('.card-blue p').textContent = window.formatoMoneda(valorMovidoMes);
    document.querySelector('.card-darkgreen p').textContent = window.formatoMoneda(valorTotal);
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
    lista.innerHTML = '';
    top.forEach(([articulo, total]) => {
        lista.insertAdjacentHTML('beforeend', `<li>${articulo}: ${total} movimientos</li>`);
    });
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
    lista.innerHTML = '';
    lista.insertAdjacentHTML('beforeend', `<li><strong>Ingresos:</strong> ${window.formatoMoneda(ingresos)}</li>`);
    lista.insertAdjacentHTML('beforeend', `<li><strong>Salidas:</strong> ${window.formatoMoneda(salidas)}</li>`);
    lista.insertAdjacentHTML('beforeend', `<li><strong>Balance:</strong> ${window.formatoMoneda(ingresos - salidas)}</li>`);
}

async function actualizarGraficoEntradasVsSalidas() {
    const canvas = document.getElementById('graficoEntradasVsSalidas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    try {
        const response = await fetch("/reportes/entradas-vs-salidas");
        const data = await response.json();

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [
                    { label: 'Entradas', data: data.entradas, backgroundColor: '#28a745' },
                    { label: 'Salidas', data: data.salidas, backgroundColor: '#dc3545' }
                ]
            },
            options: { responsive: true, plugins: { legend: { position: 'top' } } }
        });
    } catch (error) {
        console.error("No se pudo cargar el gráfico Entradas vs Salidas:", error);
    }
}

//_____________________________________
// Modales separados
//_____________________________________

document.querySelectorAll('.dashboard .card').forEach(card => {
  card.addEventListener('click', () => {
    const header = card.querySelector('h4')?.textContent || "Detalle";

    if (header.includes("Entradas vs Salidas")) {
      // 🔹 Modal exclusivo del gráfico
      const graficoModalEl = document.getElementById("graficoModal");
      const graficoModal = new bootstrap.Modal(graficoModalEl);
      graficoModal.show();

      const ctx = document.getElementById("graficoEntradasVsSalidasModal").getContext("2d");

      fetch("/reportes/entradas-vs-salidas")
        .then(resp => resp.json())
        .then(data => {
          new Chart(ctx, {
            type: 'bar',
            data: {
              labels: data.labels,
              datasets: [
                { label: 'Entradas', data: data.entradas, backgroundColor: '#28a745' },
                { label: 'Salidas', data: data.salidas, backgroundColor: '#dc3545' }
              ]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: { position: 'top' },
                title: { display: true, text: 'Entradas vs Salidas (Órdenes cerradas)' }
              }
            }
          });
        })
        .catch(err => console.error("Error cargando gráfico en modal:", err));
    } else {
      // 🔹 Modal general para números y texto
      const cardModalEl = document.getElementById("cardModal");
      const cardModal = new bootstrap.Modal(cardModalEl);
      const modalTitle = cardModalEl.querySelector(".modal-title");
      const modalBody = cardModalEl.querySelector("#modal-body");
      const modalContent = cardModalEl.querySelector(".modal-content");

      modalTitle.textContent = header;

      const contenidoClonado = card.cloneNode(true);
      const h4 = contenidoClonado.querySelector("h4");
      if (h4) h4.remove();

      contenidoClonado.querySelectorAll("p").forEach(p => {
        if (!isNaN(p.textContent.replace(/[^0-9]/g, ""))) {
          p.classList.add("numero-grande");
        } else {
          p.classList.add("texto-grande");
        }
      });

      contenidoClonado.querySelectorAll("ul, ol, li").forEach(el => {
        el.classList.add("texto-grande");
      });

      // 🔹 aplicar las clases del card al modal-content para que todo el modal herede el color
      modalContent.className = "modal-content " + card.className.replace("card", "").trim();

      modalBody.innerHTML = contenidoClonado.innerHTML;
      cardModal.show();
    }
  });
});
