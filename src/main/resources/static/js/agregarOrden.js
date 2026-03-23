// agregarOrden.js
document.addEventListener("DOMContentLoaded", function () {
  const numOrdInput = document.getElementById("numOrd");
  const errorBadge = document.getElementById("numOrdError");
  const submitBtn = document.getElementById("btnAgregar");

  if (numOrdInput && errorBadge && submitBtn) {
    submitBtn.disabled = true;
    let debounceTimer;

    numOrdInput.addEventListener("input", function () {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        const valor = numOrdInput.value.trim();
        if (valor.length > 0) {
          fetch(`/validarNumOrd?numOrd=${encodeURIComponent(valor)}`)
            .then(res => res.json())
            .then(data => {
              if (!data) {
                errorBadge.textContent = "Número de orden ya existe";
                errorBadge.classList.remove("d-none");
                submitBtn.disabled = true;
              } else {
                errorBadge.textContent = "";
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
      }, 300);
    });
  }
});