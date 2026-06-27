// Tabla de estados con botones visibles
const estados = {
    INICIAL:       ["btnAgregarArticulos","btnListado"],
    ABIERTA:       ["btnAgregarArticulos","btnFacturar","btnListado"], // se ajusta según tipo
    FACTURADA:     ["btnEntregar","btnIngresar","btnListado"],
    EN_PREPARACION:["btnEntregar","btnDevolver","btnListado"],
    ENTREGADA:     ["btnExtornar","btnCerrar","btnListado"],
    INGRESADA:     ["btnExtornar","btnCerrar","btnListado"],
    EXTORNADA:     ["btnListado"],
    DEVUELTA:      ["btnListado"],
    CERRADA:       ["btnListado"],
    ANULADA:       ["btnListado"]
};

// Plantillas de cada botón
const botonesHTML = {
    btnAgregarArticulos: `
<li>
  <button id="btnAgregarArticulos" class="dropdown-item bg-primary text-white"
          type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasArticulos">
    <i class="bi bi-plus-circle me-1"></i> Agregar Artículos
  </button>
</li>`,

    btnFacturar: `
<li>
  <a id="btnFacturar" class="dropdown-item bg-info text-white" href="#"
     data-bs-toggle="modal" data-bs-target="#facturarModal">
    <i class="bi bi-receipt me-1"></i> Facturar
  </a>
</li>`,

    btnEntregar: `
<li>
  <a id="btnEntregar" class="dropdown-item bg-success text-white" href="#">
    <i class="bi bi-truck me-1"></i> Entregar
  </a>
</li>`,

    btnIngresar: `
<li>
  <a id="btnIngresar" class="dropdown-item bg-dark text-white" href="#">
    <i class="bi bi-box-arrow-in-down me-1"></i> Ingresar
  </a>
</li>`,

    btnDevolver: `
<li>
  <a id="btnDevolver" class="dropdown-item bg-danger text-white" href="#">
    <i class="bi bi-arrow-return-left me-1"></i> Devolver
  </a>
</li>`,

    btnExtornar: `
<li>
  <a id="btnExtornar" class="dropdown-item bg-warning text-dark" href="#">
    <i class="bi bi-arrow-counterclockwise me-1"></i> Extornar
  </a>
</li>`,

    btnCerrar: `
<li>
  <a id="btnCerrar" class="dropdown-item bg-success text-white" href="#">
    <i class="bi bi-lock-fill me-1"></i> Cerrar
  </a>
</li>`,

    btnListado: `
<li>
  <a id="btnListado" class="dropdown-item bg-secondary text-white" href="/ordenes">
    <i class="bi bi-arrow-left-circle me-1"></i> Regresar al Listado
  </a>
</li>`
};

// Construir menú dinámico
function construirMenu(estOrd, tipoOrd) {
    const menu = document.querySelector("#accionesMenu").nextElementSibling;
    if (!menu) return;

    menu.innerHTML = "";
    let visibles = estados[estOrd];
    console.log("Estado recibido:", estOrd, "Tipo:", tipoOrd, "Botones visibles:", visibles);

    if (!visibles) return;

    // Ajuste especial: estado ABIERTA
    if (estOrd === "ABIERTA") {
        if (tipoOrd === "SALIDA") {
            visibles = ["btnAgregarArticulos", "btnFacturar", "btnListado"];
        } else if (tipoOrd === "INGRESO") {
            visibles = ["btnAgregarArticulos", "btnListado"];
        }
    }

    visibles.forEach(btnId => {
        menu.insertAdjacentHTML("beforeend", botonesHTML[btnId]);
    });
}

// Inicialización
document.addEventListener("DOMContentLoaded", () => {
    const estadoOrden = document.body.getAttribute("data-estado-orden");
    const tipoOrden = document.body.getAttribute("data-tipo-orden");
    construirMenu(estadoOrden, tipoOrden);
});
