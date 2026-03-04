import ast
from collectors.nominalCall import Collector
from collectors.unrankedCall import Collector2
f = open("test_1.py")
e = f.read()
def parse_CodeN(file):
    code = ast.parse(file)
    collector = Collector()
    collector.visit(code) #recorre recursivamente el ast
    problem = [line for line in collector.data if line]
    
    return ", ".join(problem)

def parse_CodeU(file):
    code = ast.parse(file)
    collector = Collector2()
    collector.visit(code) #recorre recursivamente el ast
    problem = [line for line in collector.data if line]
    
    return ", ".join(problem)
    

#print(parse_CodeU(e))
