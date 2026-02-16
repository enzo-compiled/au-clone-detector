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
        self.const_map = {}
        self.const_counter = 1

    def build_term(self, node):
        if isinstance(node, ast.Constant):
            return self.handle_constant(node)

        elif isinstance(node, ast.Name):
            return f"a{node.id}"

        elif isinstance(node, ast.BinOp): #ej 1 + 2 + 3 = Add(Add(1,2),3)
            left = self.build_term(node.left)
            right = self.build_term(node.right)
            op = type(node.op).__name__
            return f"{op}({left},{right})"

        elif isinstance(node, ast.UnaryOp):
            operand = self.build_term(node.operand)
            op = type(node.op).__name__
            return f"{op}({operand})"

        elif isinstance(node, ast.BoolOp):
            values = [self.build_term(v) for v in node.values]
            op = type(node.op).__name__
            return f"{op}(" + ",".join(values) + ")"

        elif isinstance(node, ast.Call):
            func = self.build_term(node.func)
            args = [self.build_term(a) for a in node.args]
            return f"{func}(" + ",".join(args) + ")"

        else:
            return "aUNKNOWN"

    def handle_constant(self, node): #conversion 1 -> a1, 2 -> a2, etc
        value = node.value

        if value not in self.const_map:
            name = f"a{self.const_counter}"
            self.const_map[value] = name
            self.const_counter += 1

        return self.const_map[value]

    #class ast.Assign(targets, value, type_comment)
    def visit_Assign(self, node):
        for target in node.targets:
            if isinstance(target, ast.Name):
                t = f"a{target.id}"
                v = self.build_term(node.value)

                self.data["assign"].append({
                    "term": f"fASSIGN({t},{v})"
                })

        self.generic_visit(node)


    #class ast.AugAssign(target, op, value)
    def visit_AugAssign(self, node):
        if isinstance(node.target, ast.Name):
            t = f"a{node.target.id}"
            right = self.build_term(node.value)
            op = type(node.op).__name__

            term = f"fASSIGN({t},{op}({t},{right}))"

            self.data["assign"].append({
                "term": term
            })

        self.generic_visit(node)


    #class ast.AnnAssign(target, annotation, value, simple)
    def visit_AnnAssign(self, node):
        if isinstance(node.target, ast.Name) and node.value:
            t = f"a{node.target.id}"
            v = self.build_term(node.value)

            self.data["assign"].append({
                "term": f"fASSIGN({t},{v})"
            })

        self.generic_visit(node)

    #class ast.Name(id, ctx)
    """def visit_Name(self, node):
        if isinstance(node.ctx, ast.Load):
            self.data["variables"]["load"].add(node.id)
        elif isinstance(node.ctx, ast.Store):
            self.data["variables"]["store"].add(node.id)
        self.generic_visit(node)"""

    #class ast.FunctionDef(name, args, body, decorator_list, returns, type_comment, type_params) 
    def visit_FunctionDef(self, node):
        body_terms = [self.build_term(expr) for expr in node.body]

        self.data["func"].append({
            "name": node.name,
            "args": [i.arg for i in node.args.args],
            "body": body_terms
        })

        self.generic_visit(node)


    #class ast.ClassDef(name, bases, keywords, body, decorator_list, type_params)
    def visit_ClassDef(self, node):
        body_terms = [self.build_term(stmt) for stmt in node.body]

        self.data["classes"].append({
            "name": node.name,
            "body": body_terms
        })

        self.generic_visit(node)

    
    #class ast.Call(func, args, keywords)
    def visit_Call(self, node):
        term = self.build_term(node)

        self.data["calls"].append({
            "term": term
        })

        self.generic_visit(node)

    
    #control de flujo

    #class ast.If(test, body, orelse)
    def visit_If(self, node):
        term = f"If({self.build_term(node.test)},"

        body = [self.build_term(stmt) for stmt in node.body]
        orelse = [self.build_term(stmt) for stmt in node.orelse]

        term += f"[{','.join(body)}],[{','.join(orelse)}])"

        self.data["controlflow"].append({
            "type": "cif",
            "term": term
        })

        self.generic_visit(node)


    #class ast.For(target, iter, body, orelse, type_comment)
    def visit_For(self, node):
        target = self.build_term(node.target)
        iterator = self.build_term(node.iter)
        body = [self.build_term(stmt) for stmt in node.body]

        term = f"For({target},{iterator},[{','.join(body)}])"

        self.data["controlflow"].append({
            "type": "cfor",
            "term": term
        })

        self.generic_visit(node)


    #class ast.While(test, body, orelse)
    def visit_While(self, node):
        test = self.build_term(node.test)
        body = [self.build_term(stmt) for stmt in node.body]

        term = f"While({test},[{','.join(body)}])"

        self.data["controlflow"].append({
            "type": "cwhile",
            "term": term
        })

        self.generic_visit(node)


    def visit_IfExp(self, node):
        test = self.build_term(node.test)
        body = self.build_term(node.body)
        orelse = self.build_term(node.orelse)

        term = f"IfExp({test},{body},{orelse})"

        self.data["controlflow"].append({
            "type": "cIfExp",
            "term": term
        })

        self.generic_visit(node)

        

