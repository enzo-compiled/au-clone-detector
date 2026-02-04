import ast, astor, subprocess

rutaNom = "algoritmos\eqnauac-src\eqnauac\eqnauac-lib.jar"
var1 = "java"
var2 = "AU"
var3 = "SIMPLE"
problem1 = "f(print(\"hola\"),b,d)"
problem2 = "f(print(\"hola\"),b,c)"

def main():
    """with open("test_1.py","r") as f:
        file = f.read()
        tree = ast.parse(file).body[0]
        print(astor.dump_tree(tree))"""
    subprocess.run(cmd)

cmd = [
    var1,
    "-jar",
    rutaNom,
    var2,
    var3,
    problem1 + " =^= " + problem2
]

if __name__ == "__main__":
    main()