document.addEventListener("DOMContentLoaded", () => {
    const codArtSelect = document.getElementById("codArt");
    const desArtSelect = document.getElementById("desArt");
    const stockInput = document.getElementById("stock");
    const precioInput = document.getElementById("precio");
    const cantidadInput = document.getElementById("cantidad");
    const btnGuardar = document.getElementById("btnGuardar");
    const mensaje = document.getElementById("mensajeValidacion");
    const form = document.querySelector("#offcanvasArticulos form");
    const offcanvasEl = document.getElementById("offcanvasArticulos");
    const offcanvas = bootstrap.Offcanvas.getOrCreateInstance(offcanvasEl);

    // TIPTD ya está definido en ordenDetalle.html con th:inline="javascript"
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

    // Guardar artículo vía fetch y refrescar tabla
    form.addEventListener("submit", function(e) {
        e.preventDefault();

        fetch(form.action, {
            method: "POST",
            body: new FormData(form)
        })
        .then(res => {
            if (!res.ok) throw new Error("Error al guardar artículo");
            // ✅ refrescar detalle de la orden
            return fetch(`/ordenDetalle/${ordenId}`);
        })
        .then(res => res.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");

            const nuevaTabla = doc.querySelector("#tablaArticulos");
            const nuevoTotal = doc.querySelector("#totalOrdenFragment");

            document.querySelector("#tablaArticulos").innerHTML = nuevaTabla.innerHTML;
            document.querySelector("#totalOrdenFragment").innerHTML = nuevoTotal.innerHTML;

            offcanvas.hide();
            form.reset();
        })
        .catch(err => console.error(err));
    });
});
