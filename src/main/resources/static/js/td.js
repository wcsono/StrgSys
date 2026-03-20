// td.js
document.addEventListener("DOMContentLoaded", function () {
    // --- Lógica del modal ---
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

    // --- Validación de codTd en tiempo real ---
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
                .catch(error => console.error("Error en la validación:", error));
        });
    } else {
        console.warn("No se encontraron los elementos de validación codTd/btnAgregarTd/errorCodigoTd");
    }
});