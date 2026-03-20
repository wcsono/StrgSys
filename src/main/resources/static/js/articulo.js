// Guardar artículo con fetch
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
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalVer'));
            if (modal) modal.hide();
            location.reload(); // Recarga la tabla para reflejar los cambios
        } else {
            alert('Error al actualizar el artículo');
        }
    })
    .catch(error => {
        console.error('Error al actualizar:', error);
        alert('Error de conexión al actualizar');
    });
}

// Confirmar eliminación con fetch
function confirmarEliminacion(idArt, stk) {
    if (stk > 0) {
        alert('No se puede eliminar el artículo porque tiene stock.');
        return;
    }

    if (confirm('¿Estás seguro de que deseas eliminar este artículo?')) {
        window.location.href = '/eliminar/' + idArt;
    }
}

// Cargar el modal con los datos del artículo
function cargarModal(idArt) {
    $.get("/ver/" + idArt, function (html) {
        $("#modalContenido").html(html);
        const modal = new bootstrap.Modal(document.getElementById("modalArticulo"));
        modal.show();
    }).fail(function () {
        alert("Error al cargar el artículo.");
    });
}

// Guardar artículo con jQuery
function guardarArticulo() {
    const formData = $("#formEditarArticulo").serialize();

    $.post("/actualizar", formData, function (response) {
        if (response.status === "OK") {
            const modal = bootstrap.Modal.getInstance(document.getElementById("modalArticulo"));
            modal.hide();
            location.reload(); // Refresca la tabla para mostrar los cambios
        } else {
            alert("Error al actualizar el artículo.");
        }
    }).fail(function () {
        alert("Error de conexión al actualizar.");
    });
}

// Confirmar y ejecutar la eliminación del artículo
function confirmarEliminacion(idArt, stk) {
    if (stk === 0) {
        if (confirm("¿Estás seguro de que deseas eliminar este artículo?")) {
            window.location.href = "/eliminar/" + idArt;
        }
    } else {
        alert("No se puede eliminar el artículo porque su stock no es cero.");
    }
}

// ===============================
// Validación de codArt en tiempo real
// ===============================
document.addEventListener("DOMContentLoaded", function() {
    const codArtInput = document.getElementById("codArt");
    const btnAgregar = document.getElementById("btnAgregar");
    const errorCodigo = document.getElementById("errorCodigo");

    if (codArtInput) {
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
});