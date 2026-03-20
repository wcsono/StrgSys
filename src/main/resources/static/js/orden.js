const numOrdInput = document.getElementById("numOrd");
const errorBadge = document.getElementById("numOrdError");
const submitBtn = document.getElementById("btnAgregar");

if (numOrdInput && errorBadge && submitBtn) {
    submitBtn.disabled = true;

    numOrdInput.addEventListener("input", function() {
        const valor = numOrdInput.value.trim();
        if (valor.length > 0) {
            fetch(`/validarNumOrd?numOrd=${valor}`)
                .then(res => res.json())
                .then(data => {
                    if (!data) { // false = ya existe
                        errorBadge.classList.remove("d-none");
                        submitBtn.disabled = true;
                    } else {
                        errorBadge.classList.add("d-none");
                        submitBtn.disabled = false;
                    }
                })
                .catch(() => {
                    errorBadge.textContent = "Error de validación";
                    errorBadge.classList.remove("d-none");
                    submitBtn.disabled = true;
                });
        } else {
            errorBadge.classList.add("d-none");
            submitBtn.disabled = true;
        }
    });
}