// agregarOrden.js
document.addEventListener("DOMContentLoaded", function () {
  console.log("✅ Script agregarOrden.js cargado correctamente");

  const codCliInput = document.getElementById("codCli");
  const nomCliInput = document.getElementById("nomCli");
  const dirCliInput = document.getElementById("dirCli");

  // 🔹 Buscar cliente por código al presionar Enter
  if (codCliInput) {
    codCliInput.addEventListener("keydown", function (e) {
      console.log("🔹 Tecla presionada:", e.key);

      if (e.key === "Enter") {
        e.preventDefault();
        const codCli = codCliInput.value.trim();
        console.log("👉 Código ingresado:", codCli);

        if (codCli) {
          fetch(`/clientes/buscarPorCodigo?codCli=${encodeURIComponent(codCli)}`)
            .then(response => {
              console.log("📡 Respuesta del backend:", response.status);
              if (!response.ok) throw new Error("Cliente no encontrado");
              return response.json();
            })
            .then(data => {
              console.log("✅ Cliente encontrado:", data);
              nomCliInput.value = data.nomCli;
              dirCliInput.value = data.dirCli;
            })
            .catch(err => {
              console.warn("⚠️ Cliente no encontrado:", err.message);

              // Mostrar modal de alerta si existe en el DOM
              const modalAlerta = document.getElementById("modalClienteNoEncontrado");
              if (modalAlerta) {
                const alerta = new bootstrap.Modal(modalAlerta);
                alerta.show();

                const btnAceptar = document.getElementById("btnAceptarRegistrar");
                if (btnAceptar) {
                  btnAceptar.onclick = function () {
                    alerta.hide();
                    const modalAgregar = new bootstrap.Modal(document.getElementById("modalAgregarCliente"));
                    modalAgregar.show();
                  };
                }
              }
            });
        }
      }
    });
  }

  // 🔹 Interceptar el submit del formulario del modal
  const formAgregarCliente = document.getElementById("formAgregarCliente");
  if (formAgregarCliente) {
    formAgregarCliente.addEventListener("submit", function (e) {
      e.preventDefault();

      const formData = new FormData(formAgregarCliente);
      const cliente = Object.fromEntries(formData.entries());

      console.log("📤 Enviando cliente desde modal:", cliente);

      fetch("/guardarClienteAjax", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cliente)
      })
        .then(response => {
          console.log("📡 Respuesta guardarClienteAjax:", response.status);
          if (!response.ok) throw new Error("Error al registrar cliente");
          return response.json();
        })
        .then(data => {
          console.log("✅ Cliente registrado:", data);

          // Rellenar campos en agregarOrden.html
          if (codCliInput) codCliInput.value = data.codCli;
          if (nomCliInput) nomCliInput.value = data.nomCli;
          if (dirCliInput) dirCliInput.value = data.dirCli;

          // Cerrar modal
          const modalAgregar = bootstrap.Modal.getInstance(document.getElementById("modalAgregarCliente"));
          if (modalAgregar) modalAgregar.hide();
        })
        .catch(err => {
          console.error("❌ Error al registrar cliente:", err.message);
          alert("Error al registrar cliente: " + err.message);
        });
    });
  }
});
