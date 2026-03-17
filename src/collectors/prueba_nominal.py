import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import ast, subprocess
from collectors.parseCode import parse_CodeN
"""if variable:
    print("exito")"""
a1 = open("src/collectors/test_2.py")
f1 = a1.read()
a2 = open("src/collectors/test_3.py")
f2 = a2.read()

"""print(parse_CodeN(f1))
print("===========")
print(parse_CodeN(f2))"""

problem = f"{parse_CodeN(f1)}) =^= {parse_CodeN(f2)}"

subprocess.run(
    [
        "java", "-jar", "algoritmos/eqnauac-lib.jar", "AU", "SIMPLE", problem, "", 
        "oAdd, oMult", "oAdd, oMult"
    ]
)