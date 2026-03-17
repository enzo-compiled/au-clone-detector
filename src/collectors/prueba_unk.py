import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import ast, subprocess
from collectors.parseCode import parse_CodeU
a1 = open("src/collectors/test_2.py")
f1 = a1.read()
a2 = open("src/collectors/test_3.py")
f2 = a2.read()

"""print(parse_CodeN(f1))
print("===========")
print(parse_CodeN(f2))"""

problem = f"{parse_CodeU(f1,f2)}"
subprocess.run(
    [
        "java", "algoritmos/urau-src/src/at/jku/risc/stout/urau/AntiUnifyMain.java", "-a", problem
    ]
)