// td.js
document.addEventListener("DOMContentLoaded", function () {
    // --- Lógica del modal de agregar ---
    const modalElement = document.getElementById("modalTdAgregar");

    const abrirModal = document.body.getAttribute("data-abrir-modal");
    if (abrirModal === "true" && modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    }

    if (modalElement) {
        modalElement.addEventListener("hidden.bs.modal", function () {
            const errorDiv = modalElement.querySelector(".alert-danger");
            if (errorDiv) {
                errorDiv.remove();
            }
        });
    } else {
        console.warn("No se encontró el elemento con id 'modalTdAgregar'");
    }

    // ===============================
    // Validación de codTd en tiempo real (Agregar)
    // ===============================
    const codTdInput = document.getElementById("codTd");
    const btnAgregarTd = document.getElementById("btnAgregarTd");
    const errorCodigoTd = document.getElementById("errorCodigoTd");

    if (codTdInput && btnAgregarTd && errorCodigoTd) {
        codTdInput.addEventListener("input", function () {
            const codTd = codTdInput.value.trim();
            if (codTd.length === 0) {
                errorCodigoTd.style.display = "none";
                btnAgregarTd.disabled = false;
                return;
            }

            fetch(`/validarCodigoTd?codTd=${encodeURIComponent(codTd)}`)
                .then(response => response.json())
                .then(existe => {
                    if (existe) {
                        btnAgregarTd.disabled = true;
                        errorCodigoTd.innerHTML =
                            '<span class="badge bg-danger">⚠️ El código ya está registrado</span>';
                        errorCodigoTd.style.display = "block";
                    } else {
                        btnAgregarTd.disabled = false;
                        errorCodigoTd.style.display = "none";
                    }
                })
                .catch(error => console.error("Error en la validación (agregar):", error));
        });
    }

    // ===============================
    // Validación de codTd en tiempo real (Editar)
    // ===============================
    const btnGuardarTd = document.getElementById("btnGuardarTd");
    const errorCodigoTdEditar = document.getElementById("errorCodigoTdEditar");
    const idTdField = document.getElementById("idTd"); // hidden con el ID del tipoDocumento

    if (codTdInput && btnGuardarTd && errorCodigoTdEditar && idTdField) {
        codTdInput.addEventListener("input", function () {
            const codTd = codTdInput.value.trim();
            const idTd = idTdField.value;

            if (codTd.length === 0) {
                errorCodigoTdEditar.style.display = "none";
                btnGuardarTd.disabled = false;
                return;
            }

            fetch(`/validarCodigoTd?codTd=${encodeURIComponent(codTd)}&idTd=${idTd}`)
                .then(response => response.json())
                .then(existe => {
                    if (existe) {
                        btnGuardarTd.disabled = true;
                        errorCodigoTdEditar.innerHTML =
                            '<span class="badge bg-danger">⚠️ El código ya está registrado</span>';
                        errorCodigoTdEditar.style.display = "block";
                    } else {
                        btnGuardarTd.disabled = false;
                        errorCodigoTdEditar.style.display = "none";
                    }
                })
                .catch(error => console.error("Error en la validación (editar):", error));
        });
    }
});