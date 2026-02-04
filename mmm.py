import ast
from nominalCall import Collector
a= open("test_1.py", 'r')
f = a.read()

fa = ast.parse(f)
"""
print(ast.dump(fa, indent=4))
#print(ast.dump(fa, include_attributes=True))
#print(list(ast.iter_fields(fa)))

a.close()
#print(compile(f))"""

collector = Collector()
collector.visit(fa)

from pprint import pprint
pprint(collector.data)