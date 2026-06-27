document.addEventListener("DOMContentLoaded", () => {
    const codArtSelect = document.getElementById("codArt");
    const desArtSelect = document.getElementById("desArt");
    const stockInput = document.getElementById("stock");
    const costoInput = document.getElementById("costo");
    const precioVentaInput = document.getElementById("precioVenta");
    const precioInput = document.getElementById("precio"); // hidden usado para cálculo
    const cantidadInput = document.getElementById("cantidad");
    const btnGuardar = document.getElementById("btnGuardar");
    const mensaje = document.getElementById("mensajeValidacion");
    const form = document.querySelector("#offcanvasArticulos form");
    const offcanvasEl = document.getElementById("offcanvasArticulos");
    const offcanvas = bootstrap.Offcanvas.getOrCreateInstance(offcanvasEl);

    // Sincronizar selects y llenar campos
    function sincronizar(selectOrigen, selectDestino) {
        const selectedOption = selectOrigen.options[selectOrigen.selectedIndex];
        if (!selectedOption || !selectedOption.value) return;

        selectDestino.value = selectedOption.value;
        stockInput.value = selectedOption.dataset.stock || "";
        costoInput.value = selectedOption.dataset.costo || "";
        precioVentaInput.value = selectedOption.dataset.precioventa || "";

        // El campo oculto "precio" se llena según tipoMovimiento
        if (typeof tipoMovimiento !== "undefined" && tipoMovimiento === "VENTA") {
            precioInput.value = selectedOption.dataset.precioventa || "";
        } else {
            precioInput.value = selectedOption.dataset.costo || "";
        }
    }

    if (codArtSelect && desArtSelect) {
        codArtSelect.addEventListener("change", () => sincronizar(codArtSelect, desArtSelect));
        desArtSelect.addEventListener("change", () => sincronizar(desArtSelect, codArtSelect));
    }

    // Validación dinámica con tipoMovimiento
    if (cantidadInput) {
        cantidadInput.addEventListener("input", () => {
            if (tipoMovimiento === "SALIDA") { // salida
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

        const datos = new FormData(form);

        fetch(form.action, {
            method: "POST",
            body: datos
        })
        .then(res => {
            if (!res.ok) throw new Error("Error al guardar artículo");

            // Detectar página actual
            const esEditar = document.body.getAttribute("data-estado-orden") !== null;
            const urlRefresco = esEditar ? `/editarOrd/${ordenId}` : `/ordenDetalle/${ordenId}`;

            return fetch(urlRefresco);
        })
        .then(res => res.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");

            const nuevaTabla = doc.querySelector("#tablaArticulos");
            const nuevoTotal = doc.querySelector("#totalOrdenFragment");

            if (nuevaTabla && nuevoTotal) {
                document.querySelector("#tablaArticulos").innerHTML = nuevaTabla.innerHTML;
                document.querySelector("#totalOrdenFragment").innerHTML = nuevoTotal.innerHTML;
            }

            offcanvas.hide();
            form.reset();
        })
        .catch(err => {
            console.error(err);
            offcanvas.hide(); // cerrar aunque haya error
        });
    });
});
