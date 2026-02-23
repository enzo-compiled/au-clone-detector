const body = document.querySelector("body");
const nav = document.querySelector("nav");
const modee = document.querySelector(".dark-light");


modee.addEventListener("click" , () => {
    modee.classList.toggle("active");
    body.classList.toggle("dark")
});


const compareBtn = document.querySelector("#compare-btn");
const outputContainer = document.querySelector("#output-container");
const resultText = document.querySelector("#result-text");

compareBtn.addEventListener("click", () => {
    const code1 = document.querySelector("#code1").value;
    const code2 = document.querySelector("#code2").value;

    if (code1.trim() === "" || code2.trim() === "") {
        alert("Por favor, llena ambos campos de código.");
        return;
    }

    // Aquí iría la lógica de tu algoritmo. 
    // Por ahora, simularemos un resultado:
    resultText.innerText = "Analizando coincidencias en Python...";
    
    // Mostramos la caja de output quitando la clase 'hidden'
    outputContainer.classList.remove("hidden");
});