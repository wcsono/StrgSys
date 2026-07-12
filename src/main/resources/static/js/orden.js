document.addEventListener("DOMContentLoaded", function () {
  console.log("cargando orden.js");

  const detalle = document.getElementById("detalleContenido");

  function estadoOrden(estOrd, extornada) {
    if (extornada) return "Extornada";
    return estOrd || "—";
  }

  function renderOrden(data, detalle) {
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
            <span><strong>Estado:</strong> ${estadoOrden(data.estOrd, data.extornada)}</span>
          </div>
          <div class="d-flex justify-content-between">
            <span><strong>Costo Total:</strong> ${window.formatoMoneda(data.cosOrd)}</span>
            <span><strong>Documento:</strong> ${data.tipoDocumento || "—"}</span>
          </div>
          <div class="d-flex justify-content-between">
            <span><strong>Fecha Estado:</strong> ${data.fechaEstado || "—"}</span>
            <span><strong>Usuario:</strong> ${data.usuarioAccion || "—"}</span>
          </div>
        </div>
      </div>
    `;

    if (data.detalles && data.detalles.length > 0) {
      html += renderTabla(data.detalles);
    }

    detalle.innerHTML = html;

    // 🔹 Actualizar también la fila en la tabla principal
    actualizarFilaOrden(data);
  }

  function renderTabla(detalles) {
    const total = detalles.reduce((sum, d) => sum + (d.subtotal || 0), 0);

    return `
      <div class="mt-3">
        <h5>Detalles</h5>
        <table class="table table-sm table-bordered table-compact">
          <thead>
            <tr>
              <th>Art.</th>
              <th>Ubi.</th>
              <th>Can.</th>
              <th>Valor</th>
              <th>Subtotal</th>
            </tr>
          </thead>
          <tbody>
            ${detalles.map(d => {
              const textoCompleto = `${d.codArt ?? ""}-${d.articulo ?? "—"}`;
              return `
                <tr>
                  <td class="text-truncate" title="${textoCompleto}">
                    ${textoCompleto}
                  </td>
                  <td>${d.ubicacion ?? "—"}</td>
                  <td>${d.cantidad ?? "—"}</td>
                  <td>${window.formatoMoneda(d.precioVenta ?? d.costo)}</td>
                  <td>${window.formatoMoneda(d.subtotal)}</td>
                </tr>
              `;
            }).join("")}
          </tbody>
          <tfoot>
            <tr class="table-primary">
              <td colspan="4" class="text-end"><strong>Total de la Orden:</strong></td>
              <td><strong>${window.formatoMoneda(total)}</strong></td>
            </tr>
          </tfoot>
        </table>
      </div>
    `;
  }

  // 🔹 Actualizar fila en tabla principal
  function actualizarFilaOrden(data) {
    const fila = document.querySelector(`#fila-orden-${data.idOrd}`);
    if (fila) {
      const estadoCell = fila.querySelector(".col-estado span");
      if (estadoCell) {
        estadoCell.textContent = data.estOrd;
        estadoCell.className = data.cssClass || ""; // aplica el estilo correcto
      }
      const fechaCell = fila.querySelector(".col-fechaEstado");
      if (fechaCell) {
        fechaCell.textContent = data.fechaEstado ? data.fechaEstado : "—";
      }
      const usuarioCell = fila.querySelector(".col-usuario");
      if (usuarioCell) {
        usuarioCell.textContent = data.usuarioAccion ? data.usuarioAccion : "—";
      }
    }
  }

  document.querySelectorAll(".ver-orden").forEach(btn => {
    btn.addEventListener("click", function () {
      const id = this.dataset.id;
      console.log("Click detectado en botón con ID:", id);

      detalle.innerHTML = `
        <div class="d-flex justify-content-center align-items-center">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>`;
      detalle.classList.remove("show");

      fetch(`/verOrd/${id}`)
        .then(res => {
          if (!res.ok) throw new Error("Orden no encontrada");
          return res.json();
        })
        .then(data => {
          renderOrden(data, detalle);
          detalle.classList.add("show");
        })
        .catch(err => {
          console.error("Error en fetch:", err);
        });
    });
  });
});
