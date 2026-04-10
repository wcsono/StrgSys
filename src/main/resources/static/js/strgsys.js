// strgsys.js
console.log("cargando strgsys.js");

// --- Detectar país del navegador ---
function detectarPais() {
  const locale = navigator.language || "es-PE"; // ej: "es-MX", "es-419"
  const parts = locale.split("-");
  const pais = parts.length > 1 ? parts[1].toUpperCase() : "PE";

  // Normalizar casos especiales
  if (pais === "419") return "PE"; // Latinoamérica genérico → Perú por defecto
  return pais;
}

// --- Obtener símbolo desde monedas.json ---
async function obtenerSimbolo() {
  const pais = detectarPais();
  try {
    const response = await fetch("/js/monedas.json");
    const data = await response.json();
    const registro = data.find(item => item.pais === pais);
    if (registro) return registro.simbolo;

    // Fallback especial para Perú
    if (pais === "PE") return "S/.";
    return "$";
  } catch (err) {
    console.error("Error cargando monedas.json:", err);
    return "S/.";
  }
}

// --- Variable global para el símbolo ---
let simboloGlobal = "S/.";

// --- Función global para formatear moneda ---
window.formatoMoneda = function(valor) {
  const num = Number(valor);

  if (valor === null || valor === undefined || valor === "" || isNaN(num)) {
    return "—";
  }

  if (num === 0) {
    return `${simboloGlobal} -`;
  }

  return `${simboloGlobal} ${num.toFixed(2)}`;
};

// --- Inicializar símbolo y aplicar formateo al cargar ---
document.addEventListener("DOMContentLoaded", async function () {
  simboloGlobal = await obtenerSimbolo();

  document.querySelectorAll("td.costo").forEach(td => {
    const valor = td.textContent.trim() || td.getAttribute("data-valor");
    td.textContent = window.formatoMoneda(valor);
  });
});