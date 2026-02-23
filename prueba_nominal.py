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

rutaNom = "algoritmos/eqnauac-lib.jar"
arg_1 = "AU"
arg_2 = "SIMPLE"

def serialize_data(astt):
    lines = []

    for func in astt.get("func", []):
        args = ", ".join(func.get("args", []))
        body = ", ".join(func.get("body", []))

        lines.append(f"{func['name']}({args},{body})")

    for call in astt.get("calls", []):
        lines.append(call["term"])  

    for assign in astt.get("assign", []):
        if "term" in assign:
            lines.append(assign["term"])

    return lines


pprint(serialize_data(collector1.data))


def a_cmd(lines1, lines2):

    if not lines1 or not lines2:
        print("Please insert both codes!")
        return

    max_args = max(len(lines1), len(lines2))
    
    while len(lines1) < max_args:
        lines1.append(" null")
        
    while len(lines2) < max_args:
        lines2.append(" null")

    a1 = "f(" + ", ".join(lines1) + ")"
    a2 = "f(" + ", ".join(lines2) + ")"

    arg_3 = a1 + "=^=" + a2
    arg_3 = arg_3.replace(" ", "")
    

    print("EQUATION:")
    pprint(arg_3)
    print(type(arg_3))

    cmd = [
        "java",
        "-jar",
        rutaNom,
        arg_1,
        arg_2,
        arg_3,
        "",
        "Add, Mul",
        "Add, Mul"
    ]

    subprocess.run(cmd)



lines1 = serialize_data(collector1.data)
lines2 = serialize_data(collector2.data)
a_cmd(lines1,lines2)



"""print(out)
print(err)"""

