import ast
from collectors.nominalCall import Collector
from collectors.unrankedCall import Collector2
def parse_CodeN(file):
    code = ast.parse(file)
    collector = Collector()
    collector.visit(code) #recorre recursivamente el ast
    problem = [line for line in collector.data if line]
    var = ", ".join(problem)
    form = f"f({var})"
    collector.abstraccion_cont.append("")
    return ".".join(collector.abstraccion_cont) + form

def parse_CodeU(file1,file2):
    code1 = ast.parse(file1)
    collector1 = Collector2()
    collector1.visit(code1)
    problem1 = [line for line in collector1.data if line]

    code2 = ast.parse(file2)
    collector2 = Collector2()
    collector2.const_counter = collector1.const_counter
    collector2.visit(code2)
    problem2 = [line for line in collector2.data if line]

    p1 = ", ".join(problem1)
    p2 = ", ".join(problem2)
    return f"{p1} =^= {p2}"
    

#print(parse_CodeU(e))
