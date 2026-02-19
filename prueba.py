"""import subprocess
#comandoAyuda = "java "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java" -h"
path = "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java"
problem = "f(a,b,c,d) =^= f(a,b,c)"
cmd = [
    "java",
    path,
    "-a",
    problem
]
subprocess.run(cmd)"""

import subprocess, ast
from nominalCall import Collector
from pprint import pprint

r = open("test_1.py", "r")
h = open("test_2.py", "r")
ff = h.read()
f = r.read()    

p2 = ast.parse(ff)
p1 = ast.parse(f)
collector1 = Collector()
collector1.visit(p1)
collector2 = Collector()
collector2.visit(p2)

#pprint(collector1.data)

rutaNom = "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java"
var2 = "-a"

def serialize_data(astt):
    lines = []

    for func in astt.get("func", []):
        args = ", ".join(func.get("args", []))
        body = ", ".join(func.get("body", []))

        lines.append(f"fFUNC(a{func['name']},{args},{body})")

    for call in astt.get("calls", []):
        lines.append(call["term"])  

    for assign in astt.get("assign", []):
        if "term" in assign:
            lines.append(assign["term"])

    return lines


pprint(serialize_data(collector1.data))


def a_cmd(lines1, lines2):

    if not lines1 or not lines2:
        print("No hay términos para comparar")
        return

    a1 = "fPROG(" + ", ".join(lines1) + ")"
    a2 = "fPROG(" + ", ".join(lines2) + ")"

    problema = a1 + "=^=" + a2
    problema = problema.replace(" ", "")

    print("EQUATION:")
    pprint(problema)
    print(type(problema))

    cmd = [
        "java",
        rutaNom,
        var2,
        problema
    ]

    subprocess.run(cmd)



lines1 = serialize_data(collector1.data)
lines2 = serialize_data(collector2.data)
a_cmd(lines1,lines2)



"""print(out)
print(err)"""

