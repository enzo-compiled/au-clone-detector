const body = document.querySelector("body");
const nav = document.querySelector("nav");
const modee = document.querySelector(".dark-light");


modee.addEventListener("click" , () => {
    modee.classList.toggle("active");
    body.classList.toggle("dark")
});