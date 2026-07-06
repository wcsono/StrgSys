// editarOrd.js

// Construcción dinámica del menú según estado y tipo de orden
function construirMenu(estadoOrden, tipoOrden) {
    const menuAcciones = document.getElementById("accionesMenu");
    if (!menuAcciones) {
        console.error("No se encontró el elemento accionesMenu en el DOM");
        return;
    }

    menuAcciones.innerHTML = "";

    switch (estadoOrden) {
        case "INICIAL":
            menuAcciones.innerHTML += `
                <li><a href="#" id="btnAgregar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-plus-circle"></i> Agregar Artículos</a></li>
            `;
            break;

        case "ABIERTA":
            if (tipoOrden === "INGRESO") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnAgregar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-plus-circle"></i> Agregar Artículos</a></li>
                    <li><a href="#" id="btnIngresar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-box-arrow-in-down"></i> Ingresar</a></li>
                    <li><a href="#" id="btnDevolver" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-arrow-counterclockwise"></i> Devolver</a></li>
                `;
            } else if (tipoOrden === "SALIDA") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnAgregar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-plus-circle"></i> Agregar Artículos</a></li>
                    <li><a href="#" id="btnFacturar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-receipt"></i> Facturar</a></li>
                `;
            }
            break;

        case "FACTURADA":
            if (tipoOrden === "SALIDA") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnEntregar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-box-arrow-up"></i> Entregar</a></li>
                `;
            }
            break;

        case "PREPARACION":
            if (tipoOrden === "INGRESO") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnIngresar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-box-arrow-in-down"></i> Ingresar</a></li>
                    <li><a href="#" id="btnDevolver" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-arrow-counterclockwise"></i> Devolver</a></li>
                `;
            } else if (tipoOrden === "SALIDA") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnEntregar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-box-arrow-up"></i> Entregar</a></li>
                    <li><a href="#" id="btnDevolver" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-arrow-counterclockwise"></i> Devolver</a></li>
                `;
            }
            break;

        case "INGRESADA":
            if (tipoOrden === "INGRESO") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnExtornar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-arrow-return-left"></i> Extornar</a></li>
                    <li><a href="#" id="btnCerrar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-x-circle"></i> Cerrar</a></li>
                `;
            }
            break;

        case "ENTREGADA":
            if (tipoOrden === "SALIDA") {
                menuAcciones.innerHTML += `
                    <li><a href="#" id="btnCerrar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-x-circle"></i> Cerrar</a></li>
                `;
            }
            break;

        case "CERRADA":
            menuAcciones.innerHTML += `
                <li><a href="#" id="btnExtornar" class="btn btn-outline-dark btn-sm w-100 text-start"><i class="bi bi-arrow-return-left"></i> Extornar</a></li>
            `;
            break;

        // Estados sin acciones dinámicas
        case "EXTORNADA":
        case "DEVUELTA":
        case "ANULADA":
            // No se agregan enlaces
            break;
    }

    // Si no se agregó nada, mostrar mensaje
    if (menuAcciones.innerHTML.trim() === "") {
        menuAcciones.innerHTML = `
            <li><span class="dropdown-item text-muted">Sin acciones disponibles</span></li>
        `;
    }
}

// Mostrar modal de confirmación para Entregar/Ingresar
function mostrarConfirmacionAccion(accion) {
    const titulo = document.getElementById("confirmarAccionTitulo");
    const mensaje = document.getElementById("confirmarAccionMensaje");
    const btnConfirmar = document.getElementById("btnConfirmarAccion");

    if (accion === "ENTREGAR") {
        titulo.textContent = "Confirmar Entrega";
        mensaje.textContent = "¿Está seguro que desea marcar la orden como ENTREGADA?";
    } else if (accion === "INGRESAR") {
        titulo.textContent = "Confirmar Ingreso";
        mensaje.textContent = "¿Está seguro que desea marcar la orden como INGRESADA?";
    }

    btnConfirmar.onclick = function () {
        const idOrd = document.querySelector("input[name='idOrd']").value;

        fetch(`/orden/entregadoIngresado/${idOrd}?accion=${accion}`, {
            method: "POST"
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url; // redirige al detalle de la orden
            } else {
                return response.text();
            }
        })
        .catch(error => {
            console.error("Error al actualizar estado:", error);
        });

        const modal = bootstrap.Modal.getInstance(document.getElementById("confirmarAccionModal"));
        modal.hide();
    };

    const modal = new bootstrap.Modal(document.getElementById("confirmarAccionModal"));
    modal.show();
}

// Registrar listeners para enlaces Entregar, Ingresar y Facturar
function registrarAcciones() {
    document.addEventListener("click", function(e) {
        const link = e.target.closest("a");
        if (!link) return;

        e.preventDefault(); // evita navegación

        if (link.id === "btnEntregar") {
            mostrarConfirmacionAccion("ENTREGAR");
        } else if (link.id === "btnIngresar") {
            mostrarConfirmacionAccion("INGRESAR");
        } else if (link.id === "btnFacturar") {
            const modal = new bootstrap.Modal(document.getElementById("facturarModal"));
            modal.show();
        }
    });
}

// Inicialización al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    const estadoOrden = document.body.getAttribute("data-estado-orden");
    const tipoOrden = document.body.getAttribute("data-tipo-orden");

    construirMenu(estadoOrden, tipoOrden);
    registrarAcciones();
});
