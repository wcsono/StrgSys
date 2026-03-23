// orden.js
document.addEventListener("DOMContentLoaded", function () {
  console.log("cargando orden.js");

  const detalle = document.getElementById("detalleContenido");

  document.querySelectorAll(".ver-orden").forEach(btn => {
    btn.addEventListener("click", function () {
      const id = this.dataset.id;
      console.log("Click detectado en botón con ID:", id);
      console.log("Panel detalle encontrado:", !!detalle);

      // Spinner mientras carga
      detalle.innerHTML = `
        <div class="d-flex justify-content-center align-items-center">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>`;
      detalle.classList.remove("show"); // reinicia animación

      // Fetch al backend
      fetch(`/verOrd/${id}`)
        .then(res => {
          if (!res.ok) throw new Error("Orden no encontrada");
          return res.json();
        })
        .then(data => {
          renderOrden(data, detalle);
          detalle.classList.add("show"); // activa fade-in
        })
        .catch(err => {
          console.error("Error en fetch:", err);

          // Mock de prueba si falla el backend
          const mock = {
            numOrd: id,
            nomOrd: "Distribuidora Andina SAC",
            fecOrd: "2026-03-20",
            estOrd: false,
            extornada: false,
            cosOrd: 8150,
            tipoDocumento: { desTd: "Factura" },
            detalles: [
              { articulo: { desArt: "Laptop Lenovo ThinkPad" }, cantidad: 2, cosArt: 3500, subtotal: 7000 },
              { articulo: { desArt: "Mouse inalámbrico" }, cantidad: 5, cosArt: 80, subtotal: 400 },
              { articulo: { desArt: "Teclado mecánico" }, cantidad: 3, cosArt: 250, subtotal: 750 }
            ]
          };
          renderOrden(mock, detalle);
          detalle.classList.add("show"); // activa fade-in
        });
    });
  });

  function renderOrden(data, detalle) {
    const formatoMoneda = new Intl.NumberFormat("es-PE", {
      style: "currency",
      currency: "PEN"
    });

    let html = `
      <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
          Detalle de Orden #${data.numOrd}
        </div>
        <div class="card-body">
          <p><strong>N.Orden:</strong> ${data.numOrd}</p>
          <p><strong>Cliente/Proveedor:</strong> ${data.nomOrd}</p>
          <div class="d-flex justify-content-between">
            <span><strong>Fecha:</strong> ${data.fecOrd}</span>
            <span><strong>Estado:</strong> ${data.extornada ? "Extornada" : (data.estOrd ? "Cerrada" : "Abierta")}</span>
          </div>
          <div class="d-flex justify-content-between">
            <span><strong>Costo Total:</strong> ${formatoMoneda.format(data.cosOrd)}</span>
            <span><strong>Documento:</strong> ${data.tipoDocumento?.desTd || ""}</span>
          </div>
        </div>
      </div>
    `;

    if (data.detalles && data.detalles.length > 0) {
      html += renderTabla(data.detalles, formatoMoneda);
    }

    detalle.innerHTML = html;
  }

  function renderTabla(detalles, formatoMoneda) {
    const total = detalles.reduce((sum, d) => sum + d.subtotal, 0);

    return `
      <div class="mt-3">
        <h5>Detalles</h5>
        <table class="table table-sm table-bordered">
          <thead>
            <tr>
              <th>Artículo</th>
              <th>Cantidad</th>
              <th>Costo Unitario</th>
              <th>Subtotal</th>
            </tr>
          </thead>
          <tbody>
            ${detalles.map(d => `
              <tr>
                <td>${d.articulo?.desArt || ""}</td>
                <td>${d.cantidad}</td>
                <td>${formatoMoneda.format(d.cosArt)}</td>
                <td>${formatoMoneda.format(d.subtotal)}</td>
              </tr>
            `).join("")}
          </tbody>
          <tfoot>
            <tr class="table-primary">
              <td colspan="3" class="text-end"><strong>Total de la Orden:</strong></td>
              <td><strong>${formatoMoneda.format(total)}</strong></td>
            </tr>
          </tfoot>
        </table>
      </div>
    `;
  }
});