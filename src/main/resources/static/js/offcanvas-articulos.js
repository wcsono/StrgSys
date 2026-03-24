document.addEventListener("DOMContentLoaded", () => {
    const codArtSelect = document.getElementById("codArt");
    const desArtSelect = document.getElementById("desArt");
    const stockInput = document.getElementById("stock");
    const precioInput = document.getElementById("precio");

    function sincronizar(selectOrigen, selectDestino) {
        const selectedOption = selectOrigen.options[selectOrigen.selectedIndex];
        if (!selectedOption || !selectedOption.value) return;

        console.log("Seleccionado:", selectedOption.value,
                    selectedOption.dataset.descripcion,
                    selectedOption.dataset.stock,
                    selectedOption.dataset.precio);

        // Sincronizar el otro select
        selectDestino.value = selectedOption.value;

        // Llenar campos
        stockInput.value = selectedOption.dataset.stock || "";
        precioInput.value = selectedOption.dataset.precio || "";
    }

    codArtSelect.addEventListener("change", () => sincronizar(codArtSelect, desArtSelect));
    desArtSelect.addEventListener("change", () => sincronizar(desArtSelect, codArtSelect));
});