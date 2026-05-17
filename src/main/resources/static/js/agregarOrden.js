// agregarOrden.js
document.addEventListener("DOMContentLoaded", function () {
  console.log("✅ Script agregarOrden.js cargado correctamente");

  const codCliInput = document.getElementById("codCli");
  const nomCliInput = document.getElementById("nomCli");
  const dirCliInput = document.getElementById("dirCli");

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
});
