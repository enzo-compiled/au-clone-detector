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
#largo_llamado = java -Xmx2048m -Xss128m -cp algoritmos/urau-src/urau.jar:/tmp/urau-out at.jku.risc.stout.urau.AntiUnifyMain -a -m 3 -f "at.jku.risc.stout.urau.algo.RigidityFncSubstring" "f(a,b) =^= f(a,b,c)"
import subprocess, ast
from collectors.unrankedCall import Collector2
from pprint import pprint

r = open("test_1.py", "r")
h = open("test_2.py", "r")
ff = h.read()
f = r.read()    

p2 = ast.parse(ff)
p1 = ast.parse(f)
collector1 = Collector2()
collector1.visit(p1)
collector2 = Collector2()
collector2.visit(p2)

#pprint(collector1.data)

rutaNom = "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java"
var2 = "-a"

def serialize_data(astt):
    return [line for line in astt if line]


pprint(serialize_data(collector2.data))


def a_cmd(lines1, lines2):

    if not lines1 or not lines2:
        print("Empty code")
        return

    a1 =  ", ".join(lines1) 
    a2 =  ", ".join(lines2)
    print(a1)
    problema = a1 + "=^=" + a2

    cmd = [
        "java",
        rutaNom,
        var2,
        problema
    ]
    pprint(cmd)
    subprocess.run(cmd)



lines1 = serialize_data(collector1.data)
lines2 = serialize_data(collector2.data)
a_cmd(lines1,lines2)


