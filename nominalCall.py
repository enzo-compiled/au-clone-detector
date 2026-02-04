import ast, astor

class Collector(ast.NodeVisitor):
    def __init__(self):
        self.data = { #dict de datos a guardar
            "assign" : [],
            "variables" : {
                "load": set(),
                "store": set()
            },
            "func" : [],
            "classes" : [],
            "calls" : [],
            "controlflow" : [],
        }

    #class ast.Assign(targets, value, type_comment)
    def visit_Assign(self, node): 
        self.data["assign"].append({
            "targets": [x.id for x in node.targets if isinstance(x, ast.Name)],
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)

    #class ast.Name(id, ctx)
    def visit_Name(self, node):
        if isinstance(node.ctx, ast.Load):
            self.data["variables"]["load"].add(node.id)
        elif isinstance(node.ctx, ast.Store):
            self.data["variables"]["store"].add(node.id)
        self.generic_visit(node)

    #class ast.FunctionDef(name, args, body, decorator_list, returns, type_comment, type_params) 
    def visit_FunctionDef(self, node):
        self.data["func"].append({
            "name": node.name,
            "args": [i.arg for i in node.args.args],
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)

    #class ast.ClassDef(name, bases, keywords, body, decorator_list, type_params)
    def visit_ClassDef(self, node):
        self.data["classes"].append({
            "name": node.name,
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)
    
    #class ast.Call(func, args, keywords)
    def visit_Call(self, node):
        if isinstance(node.func, ast.Name):
            name = node.func.id
        elif isinstance(node.func, ast.Attribute):
            name = node.func.attr
        else:
            name = "placeholder"

        self.data["calls"].append({
            "name": name,
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)
    #control de flujo

    #class ast.If(test, body, orelse)

    def visit_If(self, node):
        self.data["controlflow"].append({
            "type": "if",
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)

    #class ast.For(target, iter, body, orelse, type_comment)

    def visit_For(self, node):
        self.data["controlflow"].append({
            "type": "for",
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset  
        })
        self.generic_visit(node)

    #class ast.While(test, body, orelse)

    def visit_While(self, node):
        self.data["controlflow"].append({
            "type": "while",
            "line": node.lineno,
            "col": node.col_offset,
            "col_end": node.end_col_offset 
        })
        self.generic_visit(node)


"""with open("test_1.py","r") as f:
    file = f.read()
    tree = ast.parse(file)
    #print(astor.dump_tree(tree))
    for nodo in ast.walk(tree):
        print(type(nodo).__name__)"""

