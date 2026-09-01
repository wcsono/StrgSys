// =======================
// Dashboard dinámico
// =======================

document.addEventListener("DOMContentLoaded", () => {
    actualizarDashboard();
    actualizarTopProductos();
    actualizarGraficoVentasPorMes(); // ✅ reemplaza la llamada vieja
    // actualizarCardTotalesMes(); // ❌ comentado porque no existe el endpoint
    actualizarResumenOrdenes();
});

// ❌ Función comentada porque no hay card ni endpoint
// async function actualizarCardTotalesMes() { ... }

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

    document.querySelector('.card-green p').textContent = totalProductos;
document.querySelector('.card-blue p').textContent =
    valorMovidoMes > 0 ? window.formatoMoneda(valorMovidoMes) : "0";
    document.querySelector('.card-darkgreen p').textContent = window.formatoMoneda(valorTotalInventario);
}

// 🔹 Top productos más vendidos
async function actualizarTopProductos() {
    try {
        const response = await fetch("/reportes/top-productos-vendidos");
        const data = await response.json();

        // Usar un id único para el card de Top productos
        const lista = document.querySelector('#topProductosCard ol');
        if (lista) {
            lista.innerHTML = '';
            data.slice(0, 3).forEach(item => {
                lista.insertAdjacentHTML(
                    'beforeend',
                    `<li>${item.articulo}: ${item.ventas} ventas</li>`
                );
            });
        }
    } catch (error) {
        console.error("Error al cargar top productos vendidos:", error);
    }
}

async function actualizarGraficoVentasPorMes() {
    const canvas = document.getElementById('graficoVentasPorMes');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    try {
        const response = await fetch("/reportes/ventas-por-mes");
        const data = await response.json();

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(item => "Mes " + item.mes),
                datasets: [
                    {
                        label: 'Ventas',
                        data: data.map(item => item.ventas),
                        backgroundColor: '#007bff'
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: 'Ventas por Mes' },
                    datalabels: {
                        anchor: 'end',
                        align: 'top',
                        formatter: (value) => window.formatoMoneda ? window.formatoMoneda(value) : value,
                        color: '#000',
                        font: { weight: 'bold' }
                    }
                }
            },
            plugins: [ChartDataLabels]
        });
    } catch (error) {
        console.error("No se pudo cargar el gráfico de ventas por mes:", error);
    }
}

// 🔹 Actualizar órdenes cerradas y no cerradas
async function actualizarResumenOrdenes() {
    try {
        const response = await fetch("/reportes/resumen-ordenes");
        const data = await response.json();

        const cerradasEl = document.getElementById("cardOrdenesCerradas");
        if (cerradasEl) cerradasEl.textContent = data.cerradasMes;

        const pendientesEl = document.getElementById("cardOrdenesPendientes");
        if (pendientesEl) pendientesEl.textContent = data.pendientes;
    } catch (error) {
        console.error("Error al cargar resumen de órdenes:", error);
    }
}

// 🔹 Actualizar usuarios inactivos
async function actualizarUsuariosInactivos() {
    try {
        const response = await fetch("/reportes/usuarios");
        const data = await response.json();

        const inactivosEl = document.getElementById("cardUsuariosInactivos");
        if (inactivosEl) inactivosEl.textContent = data.inactivos;
    } catch (error) {
        console.error("Error al cargar usuarios inactivos:", error);
    }
}

//_____________________________________
// Nueva función para modales
//_____________________________________
function abrirModalDesdeCard(cardTitulo, cardValor, esNumero, card) {
  // Título del modal
  document.getElementById("modalCardTitle").textContent = cardTitulo;

  // Contenido principal del modal
  const modalDato = document.getElementById("modalCardDato");

  // Limpia clases previas
  modalDato.className = "";

  // 👉 Si el contenido es una lista (<ol> o <ul>)
  if (!esNumero && cardValor.includes("<li")) {
    modalDato.innerHTML = `<ol>${cardValor}</ol>`; // reconstruimos la lista
    modalDato.classList.add("texto-grande");
  } else {
    // Caso normal: número o texto
    modalDato.textContent = cardValor;
    if (esNumero) {
      modalDato.classList.add("numero-grande");
    } else {
      modalDato.classList.add("texto-grande");
    }
  }

  // 👉 Aplica la clase del card al modal-content
  const modalContent = document.querySelector("#cardModal .modal-content");
  if (card && card.classList.length > 1) {
    modalContent.className = "modal-content " + card.classList[1];
  } else {
    modalContent.className = "modal-content";
  }

  // Mostrar el modal
  const modal = new bootstrap.Modal(document.getElementById("cardModal"));
  modal.show();
}


//_____________________________________
// Eventos de click en los cards
//_____________________________________
document.querySelectorAll('.dashboard .card').forEach(card => {
  card.addEventListener('click', () => {
    const titulo = card.querySelector('h4')?.textContent || "Detalle";

    // Caso especial: gráfico
    if (titulo.includes("Ventas por Mes")) {
      const graficoModalEl = document.getElementById("graficoModal");
      const graficoModal = new bootstrap.Modal(graficoModalEl);
      graficoModal.show();

      const ctx = document.getElementById("graficoVentasPorMesModal").getContext("2d");

      fetch("/reportes/ventas-por-mes")
        .then(resp => resp.json())
        .then(data => {
          new Chart(ctx, {
            type: 'bar',
            data: {
              labels: data.map(item => "Mes " + item.mes),
              datasets: [
                { label: 'Ventas', data: data.map(item => item.ventas), backgroundColor: '#007bff' }
              ]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: { display: false },
                title: { display: true, text: 'Ventas por Mes' },
                datalabels: {
                    anchor: 'end',
                    align: 'top',
                    formatter: (value) => window.formatoMoneda ? window.formatoMoneda(value) : value,
                    color: '#000',
                    font: { weight: 'bold' }
                }
              }
            },
            plugins: [ChartDataLabels]
          });
        })
        .catch(err => console.error("Error cargando gráfico en modal:", err));
      } else {
        let valor = "";
        let esNumero = false;

        // Si el card tiene <p>, usamos ese valor
        const p = card.querySelector('p');
        if (p) {
          valor = p.textContent || "";
          esNumero = !isNaN(valor.replace(/[^0-9]/g, ""));
        } else {
          // Si el card tiene lista (<ol> o <ul>), copiamos toda la lista
          const lista = card.querySelector('ol, ul');
          if (lista) {
            valor = lista.outerHTML; // copiamos la lista completa
            esNumero = false;
          }
        }

      abrirModalDesdeCard(titulo, valor, esNumero, card);
    }
  });
});
