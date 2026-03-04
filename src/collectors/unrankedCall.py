import ast

class Collector2(ast.NodeVisitor):
    def __init__(self):
        self.data = []
        self.const_map = {}
        self.const_counter = 1

    def build_term(self, node):
        if node is None:
            return ""

        elif isinstance(node, ast.Constant):
            return self.handle_constant(node)

        elif isinstance(node, ast.Name):
            return node.id

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
            args = [a for arg in node.args if (a := self.build_term(arg))]
            
            if not func: 
                return ""
            return f"{func}(" + ",".join(args) + ")"
        
        elif isinstance(node, ast.Attribute):
            value = self.build_term(node.value)
            return f"{value}.{node.attr}"

        return ""


    def handle_constant(self, node): #conversion 1 -> a1, 2 -> a2, etc
        value = node.value

        if value not in self.const_map:
            name = f"{self.const_counter}"
            self.const_map[value] = name
            self.const_counter += 1

        return self.const_map[value]

    #class ast.Assign(targets, value, type_comment)
    def visit_Assign(self, node):
        t = self.build_term(node.targets[0])
        v = self.build_term(node.value)
        self.data.append(f"assign({t},{v})")

        self.generic_visit(node)


    #class ast.AugAssign(target, op, value)
    def visit_AugAssign(self, node):
        t = self.build_term(node.target)
        right = self.build_term(node.value)
        op = type(node.op).__name__
        if t and right:
            self.data.append(f"assign({t},{op}({t},{right}))")

        self.generic_visit(node)


    #class ast.AnnAssign(target, annotation, value, simple)
    def visit_AnnAssign(self, node):
        if node.value:
            t = self.build_term(node.target)
            v = self.build_term(node.value)
            self.data.append(f"assign({t},{v})")

        self.generic_visit(node)


    #class ast.FunctionDef(name, args, body, decorator_list, returns, type_comment, type_params) 
    def visit_FunctionDef(self, node):
        args = [a.arg for a in node.args.args]
        body = [t for s in node.body if (t := self.build_term(s))]
        self.data.append(f"def_{node.name}({','.join(args)}{','.join(body)})")

        self.generic_visit(node)

    def visit_Expr(self, node):
        #Esto captura los print() sueltos que no son asignaciones
        term = self.build_term(node.value)
        if term:
            self.data.append(term)
        self.generic_visit(node)

    #class ast.ClassDef(name, bases, keywords, body, decorator_list, type_params)
    def visit_ClassDef(self, node):
        body_terms = [t for stmt in node.body if (t := self.build_term(stmt))]
        term = f"class_{node.name}({','.join(body_terms)})"
        self.data.append(term)
        self.generic_visit(node)

    def visit_Return(self, node):
        if node.value:
            v = self.build_term(node.value)
            self.data.append(f"return({v})")
        else:
            self.data.append("return()")
        self.generic_visit(node)

    
    
    #control de flujo

    #class ast.If(test, body, orelse)
    def visit_If(self, node):
        test = self.build_term(node.test)
        # Usamos el filtro 'if t' para evitar los nulls internos
        body = [t for stmt in node.body if (t := self.build_term(stmt))]
        orelse = [t for stmt in node.orelse if (t := self.build_term(stmt))]

        term = f"if({test},[{','.join(body)}],[{','.join(orelse)}])"
        self.data.append(term) # <--- Cambio clave
        self.generic_visit(node)


    #class ast.For(target, iter, body, orelse, type_comment)
    def visit_For(self, node):
        target = self.build_term(node.target)
        iterator = self.build_term(node.iter)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]

        term = f"for({target},{iterator},[{','.join(body)}])"
        self.data.append(term) # <--- Cambio clave
        self.generic_visit(node)


    #class ast.While(test, body, orelse)
    def visit_While(self, node):
        test = self.build_term(node.test)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]

        term = f"while({test},[{','.join(body)}])"
        self.data.append(term) # <--- Cambio clave
        self.generic_visit(node)


    def visit_IfExp(self, node):
        self.generic_visit(node)

        

