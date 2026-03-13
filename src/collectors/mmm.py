from collectors.nominalCall import Collector

code = """val = (1 + 2 + 3)
def nombre(value):
    print("es valor es:", value)
class clase():
    def __init__(self):
        self.data = 10
variable = val
nombre(variable)"""

import ast
tree = ast.parse(code)
c = Collector()
c.visit(tree)
for line in c.data:
    print(repr(line))