import ast, subprocess, re
from nominalCall import Collector

rutaNom = ("algoritmos/eqnauac-lib.jar","algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java")
var1 = "java"
arg_1 = "AU"
arg_2 = ["SIMPLE", "VERBOSE", "PROGRESS", "ALL", "SILENT"] 
problem1 = "f(assign(var, Add(a10,Mul(a10,a30))), print(a25))"
problem2 = "f(assign(var, Add(a10,Mul(a10,a30))), print(a30))"


cmd = [
    var1,
    "-jar",
    rutaNom[0],
    arg_1,
    arg_2[0],
    problem1 + " =^= " + problem2,
    "",
    "Add, Mul",
    "Add, Mul"
]

def parse_nau_output(output):
    # Buscamos todas las secciones de "Result branch X: <context; abstraction>"
    branches = re.findall(r"Result branch \d+: <.*?; (.*?)>", output, re.DOTALL)
    
    if not branches:
        return None

    best_abstraction = ""
    highest_score = -1

    for abs_text in branches:
        # Limpiamos espacios y saltos de línea del string
        clean_abs = abs_text.replace("\n", "").replace("  ", " ").strip()
        
        # Calculamos un puntaje de similitud simple:
        # Contamos cuántos elementos estructurales quedaron (similitud)
        # vs cuántas variables de generalización 'Z' o '?' aparecieron (diferencia)
        structural_elements = len(re.findall(r"(fASSIGN|f|Add|Mul|gAdd|gMult|assign)", clean_abs))
        generalizations = len(re.findall(r"Z\d+|\?", clean_abs))
        
        # Puntaje: Mientras más estructura y menos variables, mejor es el match
        score = structural_elements / (generalizations + 1)
        
        if score > highest_score:
            highest_score = score
            best_abstraction = clean_abs

    return best_abstraction, highest_score



def main():
    #subprocess.run(cmd)
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.stdout:
        best_abs, score = parse_nau_output(result.stdout)
        print(f"\n--- MEJOR ABSTRACCIÓN ENCONTRADA (Score: {score:.2f}) ---")
        print(best_abs)


if __name__ == "__main__":
    main()