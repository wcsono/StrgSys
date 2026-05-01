// ===============================
// Guardar artículo con fetch
// ===============================
function guardarArticulo() {
    const form = document.getElementById('formEditarArticulo');
    const formData = new FormData(form);

    fetch('/actualizar', {
        method: 'POST',
        body: new URLSearchParams(formData),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Error en la respuesta del servidor');
        return response.json();
    })
    .then(data => {
        if (data.status === 'OK') {
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalArticulo'));
            if (modal) modal.hide();
            location.reload(); // Refresca la tabla para mostrar los cambios
        } else {
            alert('Error al actualizar el artículo');
        }
    })
    .catch(error => {
        console.error('Error al actualizar:', error);
        alert('Error de conexión al actualizar');
    });
}

// ===============================
// Confirmar eliminación
// ===============================
function confirmarEliminacion(idArt, stk) {
    if (stk > 0) {
        alert('No se puede eliminar el artículo porque tiene stock.');
        return;
    }

    if (confirm('¿Estás seguro de que deseas eliminar este artículo?')) {
        window.location.href = '/eliminar/' + idArt;
    }
}

// ===============================
// Cargar modal con datos del artículo
// ===============================
function cargarModal(idArt) {
    $.get("/ver/" + idArt, function (html) {
        $("#modalContenido").html(html);
        const modal = new bootstrap.Modal(document.getElementById("modalArticulo"));
        modal.show();
    }).fail(function () {
        alert("Error al cargar el artículo.");
    });
}

// ===============================
// Mostrar artículos en tabla con mensaje si no hay coincidencias
// ===============================
function mostrarArticulos(articulos) {
    const tablaBody = document.querySelector("table tbody");
    tablaBody.innerHTML = "";

    if (!articulos || articulos.length === 0) {
        const fila = document.createElement("tr");
        const celda = document.createElement("td");
        celda.colSpan = 6; // número de columnas de la tabla
        celda.className = "text-center text-muted";
        celda.textContent = "No se encontraron artículos para los criterios seleccionados.";
        fila.appendChild(celda);
        tablaBody.appendChild(fila);
        return;
    }

    articulos.forEach(articulo => {
        const fila = document.createElement("tr");
        fila.className = "text-center";

        fila.innerHTML = `
            <td>${articulo.idArt}</td>
            <td>${articulo.codArt}</td>
            <td>${articulo.desArt}</td>
            <td>${articulo.udm}</td>
            <td>${articulo.stk}</td>
            <td>
                <div class="d-flex justify-content-center gap-2">
                    <a href="/editarArt/${articulo.idArt}" class="btn btn-dark btn-sm" title="Editar">
                        <i class="bi bi-pencil-square"></i>
                    </a>
                    <a href="/eliminar/${articulo.idArt}" class="btn btn-danger btn-sm" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </a>
                </div>
            </td>
        `;
        tablaBody.appendChild(fila);
    });
}

// ===============================
// Validación de codArt en tiempo real
// ===============================
document.addEventListener("DOMContentLoaded", function() {
    const codArtInput = document.getElementById("codArt");

    // --- Validación en agregarArt ---
    const btnAgregar = document.getElementById("btnAgregar");
    const errorCodigo = document.getElementById("errorCodigo");

    if (codArtInput && btnAgregar && errorCodigo) {
        codArtInput.addEventListener("input", function() {
            const codArt = codArtInput.value.trim();
            if (codArt.length > 0) {
                fetch(`/validarCodigoArt?codArt=${encodeURIComponent(codArt)}`)
                    .then(response => response.json())
                    .then(existe => {
                        if (existe) {
                            btnAgregar.disabled = true;
                            errorCodigo.innerHTML = '<span class="badge bg-danger">Error!! Código de Artículo ya registrado</span>';
                            errorCodigo.style.display = "block";
                        } else {
                            btnAgregar.disabled = false;
                            errorCodigo.style.display = "none";
                        }
                    })
                    .catch(err => console.error("Error en validación:", err));
            } else {
                btnAgregar.disabled = false;
                errorCodigo.style.display = "none";
            }
        });
    }

    // --- Validación en editarArt ---
    const btnGuardar = document.getElementById("btnGuardar");
    const errorCodigoEditar = document.getElementById("errorCodigoEditar");
    const idArtField = document.getElementById("idArt"); // hidden con el ID del artículo

    if (codArtInput && btnGuardar && errorCodigoEditar && idArtField) {
        codArtInput.addEventListener("input", function() {
            const codArt = codArtInput.value.trim();
            const idArt = idArtField.value;

            if (codArt.length > 0) {
                fetch(`/validarCodigoArt?codArt=${encodeURIComponent(codArt)}&idArt=${idArt}`)
                    .then(response => response.json())
                    .then(existe => {
                        if (existe) {
                            btnGuardar.disabled = true;
                            errorCodigoEditar.innerHTML = '<span class="badge bg-danger">Error!! Código de Artículo ya registrado</span>';
                            errorCodigoEditar.style.display = "block";
                        } else {
                            btnGuardar.disabled = false;
                            errorCodigoEditar.style.display = "none";
                        }
                    })
                    .catch(err => console.error("Error en validación:", err));
            } else {
                btnGuardar.disabled = false;
                errorCodigoEditar.style.display = "none";
            }
        });
    }
});
