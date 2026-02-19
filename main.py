import ast, subprocess
from nominalCall import Collector
"""a= open("test_1.py", 'r')
f = a.read()

fa = ast.parse(f)
collector = Collector()
collector.visit(fa)"""


rutaNom = "algoritmos/eqnauac-lib.jar"
var1 = "java"
var2 = "AU"
var3 = ["SIMPLE", "VERBOSE", "PROGRESS", "ALL", "SILENT"] 
problem1 = "f([a])"
problem2 = "f([a])"


cmd = [
    var1,
    "-jar",
    rutaNom,
    var2,
    var3[0],
    problem1 + " =^= " + problem2
]

def main():
    subprocess.run(cmd)


if __name__ == "__main__":
    main()