document.addEventListener("DOMContentLoaded", function() {
    const codSelect = document.getElementById("codArt");
    const desSelect = document.getElementById("desArt");
    const precioInput = document.getElementById("precio");

    function syncSelect(sourceSelect, targetSelect) {
        const selectedValue = sourceSelect.value;
        if (selectedValue) {
            // Sincronizar el otro select
            const optionToSelect = targetSelect.querySelector(`option[value="${selectedValue}"]`);
            if (optionToSelect) {
                targetSelect.value = selectedValue;
            }

            // Obtener precio del artículo seleccionado
            const selectedOption = sourceSelect.options[sourceSelect.selectedIndex];
            const precio = selectedOption.getAttribute("data-precio");
            console.log("Precio leído:", precio);
            // Depuración en consola
             console.log("Precio leído desde data-precio:", precio);

            precioInput.value = precio ? precio : "";
        } else {
            targetSelect.value = "";
            precioInput.value = "";
        }
    }

    // Eventos de cambio en ambos selects
    codSelect.addEventListener("change", function() {
        syncSelect(codSelect, desSelect);
    });

    desSelect.addEventListener("change", function() {
        syncSelect(desSelect, codSelect);
    });
});