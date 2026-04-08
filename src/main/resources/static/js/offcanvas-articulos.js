document.addEventListener("DOMContentLoaded", () => {
    const codArtSelect = document.getElementById("codArt");
    const desArtSelect = document.getElementById("desArt");
    const stockInput = document.getElementById("stock");
    const precioInput = document.getElementById("precio");
    const cantidadInput = document.getElementById("cantidad");
    const btnGuardar = document.getElementById("btnGuardar");
    const mensaje = document.getElementById("mensajeValidacion");

    // TIPTD ya está definido en editarOrd.html con th:inline="javascript"
    // true = ingreso, false = salida

    function sincronizar(selectOrigen, selectDestino) {
        const selectedOption = selectOrigen.options[selectOrigen.selectedIndex];
        if (!selectedOption || !selectedOption.value) return;

        selectDestino.value = selectedOption.value;
        stockInput.value = selectedOption.dataset.stock || "";
        precioInput.value = selectedOption.dataset.precio || "";
    }

    if (codArtSelect && desArtSelect) {
        codArtSelect.addEventListener("change", () => sincronizar(codArtSelect, desArtSelect));
        desArtSelect.addEventListener("change", () => sincronizar(desArtSelect, codArtSelect));
    }

    // Validación dinámica con estilos Bootstrap
    if (cantidadInput) {
        cantidadInput.addEventListener("input", () => {
            if (!TIPTD) { // salida
                const cantidad = parseFloat(cantidadInput.value) || 0;
                const stock = parseFloat(stockInput.value) || 0;

                if (cantidad > stock) {
                    btnGuardar.disabled = true;
                    mensaje.style.display = "block";
                    cantidadInput.classList.add("is-invalid");
                } else {
                    btnGuardar.disabled = false;
                    mensaje.style.display = "none";
                    cantidadInput.classList.remove("is-invalid");
                }
            } else { // ingreso
                btnGuardar.disabled = false;
                mensaje.style.display = "none";
                cantidadInput.classList.remove("is-invalid");
            }
        });
    }
});