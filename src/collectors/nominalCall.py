import ast

# ! CONSIDERAR ABSTRACCIÓN (. dot) y quizás extra atoms.

class Collector(ast.NodeVisitor):
    def __init__(self):
        self.data = [] #las lineas
        self.const_map = {}
        self.const_counter = 1
        #renombrar vars
        self.var_map = {}
        self.var_counter = 1

    def handle_variable(self, name):
        #nombres reales a nombres genéricos (v_0,v_1,...)
        if name == "self": 
            return "self" 
        
        if name not in self.var_map:
            self.var_map[name] = f"v{self.var_counter}"
            self.var_counter += 1
        return self.var_map[name]

    def handle_constant(self, node):
        value = node.value
        if value not in self.const_map:
            name = f"a{self.const_counter}"
            self.const_map[value] = name
            self.const_counter += 1
        return self.const_map[value]

    def build_term(self, node):
        if node is None:
            return ""

        if isinstance(node, ast.Constant):
            return self.handle_constant(node)

        elif isinstance(node, ast.Name):
            return self.handle_variable(node.id) #renombramiento

        elif isinstance(node, ast.BinOp): #ej 1 + 2 + 3 = Add(Add(1,2),3)
            left = self.build_term(node.left)
            right = self.build_term(node.right)
            op = type(node.op).__name__
            return f"{op}(secuencia({left},{right}))"

        elif isinstance(node, ast.UnaryOp):
            operand = self.build_term(node.operand)
            op = type(node.op).__name__
            return f"{op}({operand})"

        elif isinstance(node, ast.BoolOp):
            values = [self.build_term(v) for v in node.values]
            op = type(node.op).__name__
            long = len(values)
            return f"{op}_{long}(" + ",".join(values) + ")"

        elif isinstance(node, ast.Call):
            """func = self.build_term(node.func)
            args = [a for arg in node.args if (a := self.build_term(arg))]
            if not func: 
                return "null"
            return f"{func}(" + ",".join(args) + ")" """
            func = self.build_term(node.func)
            args = [a for arg in node.args if (a := self.build_term(arg))]
            if not func: 
                return "null"
            return f"{func}(secuencia({','.join(args)}))" #se encapcula términos por problemas de aridad
        
        elif isinstance(node, ast.Attribute):
            value = self.build_term(node.value)
            return f"{value}.{node.attr}"

        elif isinstance(node, ast.Return):
            #v = self.build_term(node.value) if node.value else ""
            if node.value:
                v = self.build_term(node.value)
            else:
                v = ""
            return f"return(secuencia({v}))"

        return ""

        """elif isinstance(node, ast.Call):
            func = self.build_term(node.func)
            args = [self.build_term(a) for a in node.args]
            long = len(args)
            return f"{func}_{long}(" + ",".join(args) + ")"

        else:
            return"""

    """def handle_constant(self, node): #conversion 1 -> a1, 2 -> a2, etc
        value = node.value

        if value not in self.const_map:
            name = f"a{self.const_counter}"
            self.const_map[value] = name
            self.const_counter += 1

        return self.const_map[value]"""

    #class ast.Assign(targets, value, type_comment)
    def visit_Assign(self, node):
        """for target in node.targets:
            if isinstance(target, ast.Name):
                t = f"{target.id}"
                v = self.build_term(node.value)

                self.data["assign"].append({
                    "term": f"assign({t},{v})"
                })

        self.generic_visit(node)"""
        t = self.build_term(node.targets[0])
        v = self.build_term(node.value)
        if t and v:
            self.data.append(f"assign(secuencia({t},{v}))")
        self.generic_visit(node)


    #class ast.AugAssign(target, op, value)
    def visit_AugAssign(self, node):
        """if isinstance(node.target, ast.Name):
            t = f"{node.target.id}"
            right = self.build_term(node.value)
            op = type(node.op).__name__

            term = f"assign({t},{op}({t},{right}))"

            self.data["assign"].append({
                "term": term
            })

        self.generic_visit(node)"""
        t = self.build_term(node.target)
        right = self.build_term(node.value)
        op = type(node.op).__name__
        if t and right:
            self.data.append(f"assign({t},{op}({t},{right}))")

        self.generic_visit(node)


    #class ast.AnnAssign(target, annotation, value, simple)
    def visit_AnnAssign(self, node):
        """if isinstance(node.target, ast.Name) and node.value:
            t = f"{node.target.id}"
            v = self.build_term(node.value)

            self.data["assign"].append({
                "term": f"assign({t},{v})"
            })

        self.generic_visit(node)"""
        if node.value:
            t = self.build_term(node.target)
            v = self.build_term(node.value)
            self.data.append(f"assign({t},{v})")

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
        """body_terms = [self.build_term(expr) for expr in node.body]
        args_names = [i.arg for i in node.args.args]
        self.data["func"].append({
            "name": f"{node.name}_{len(args_names)}",
            "args": args_names,
            "body": body_terms
        })

        self.generic_visit(node)"""
        """args = [a.arg for a in node.args.args]
        body = [t for s in node.body if (t := self.build_term(s))]
        self.data.append(f"def_{node.name}({','.join(args)}{','.join(body)})")

        self.generic_visit(node)"""
        # Renombrar argumentos de la función
        args = [self.handle_variable(a.arg) for a in node.args.args]
        # El cuerpo ya usa build_term, que usará handle_variable
        body = [t for s in node.body if (t := self.build_term(s))]
        
        # Estructura: def_nombre(args(v1,v2), body(linea1,linea2))
        term = f"def_{node.name}(secuencia({','.join(args)}),secuencia({','.join(body)}))"
        self.data.append(term)
        self.generic_visit(node)

    def visit_Expr(self, node):
        #Esto captura los print() sueltos que no son asignaciones
        term = self.build_term(node.value)
        if term:
            self.data.append(term)
        self.generic_visit(node)

    #class ast.ClassDef(name, bases, keywords, body, decorator_list, type_params)
    """def visit_ClassDef(self, node):
        body_terms = [self.build_term(stmt) for stmt in node.body]

        self.data["classes"].append({
            "name": node.name,
            "body": body_terms
        })

        self.generic_visit(node)"""
    def visit_ClassDef(self, node):
        body_terms = [t for stmt in node.body if (t := self.build_term(stmt))]
        term = f"class_{node.name}({','.join(body_terms)})"
        self.data.append(term)
        self.generic_visit(node)

    
    """def visit_Return(self, node):
        if node.value:
            v = self.build_term(node.value)
            self.data.append(f"return({v})")
        else:
            self.data.append("return()")
        self.generic_visit(node)"""

    
    #control de flujo

    #class ast.If(test, body, orelse)
    """def visit_If(self, node):
        term = f"If({self.build_term(node.test)},"

        body = [self.build_term(stmt) for stmt in node.body]
        orelse = [self.build_term(stmt) for stmt in node.orelse]

        term += f"[{','.join(body)}],[{','.join(orelse)}])"

        self.data["controlflow"].append({
            "type": "if",
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
            "type": "for",
            "term": term
        })

        self.generic_visit(node)


    #class ast.While(test, body, orelse)
    def visit_While(self, node):
        test = self.build_term(node.test)
        body = [self.build_term(stmt) for stmt in node.body]

        term = f"While({test},[{','.join(body)}])"

        self.data["controlflow"].append({
            "type": "while",
            "term": term
        })

        self.generic_visit(node)


    def visit_IfExp(self, node):
        test = self.build_term(node.test)
        body = self.build_term(node.body)
        orelse = self.build_term(node.orelse)

        term = f"IfExp({test},{body},{orelse})"

        self.data["controlflow"].append({
            "type": "IfExp",
            "term": term
        })

        self.generic_visit(node)"""
    def visit_If(self, node):
        test = self.build_term(node.test)
        # Usamos el filtro 'if t' para evitar los nulls internos
        body = [t for stmt in node.body if (t := self.build_term(stmt))]
        orelse = [t for stmt in node.orelse if (t := self.build_term(stmt))]

        term = f"if({test},secuencia({','.join(body)}),secuencia({','.join(orelse)}))"
        self.data.append(term)
        self.generic_visit(node)


    #class ast.For(target, iter, body, orelse, type_comment)
    def visit_For(self, node):
        target = self.build_term(node.target)
        iterator = self.build_term(node.iter)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]

        term = f"for({target},{iterator},secuencia({", ".join(body)}))"
        self.data.append(term)
        self.generic_visit(node)


    #class ast.While(test, body, orelse)
    def visit_While(self, node):
        test = self.build_term(node.test)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]

        term = f"while({test},secuencia({','.join(body)}))"
        self.data.append(term) 
        self.generic_visit(node)


    def visit_IfExp(self, node):
        test = self.build_term(node.test)
        body = self.build_term(node.body)
        orelse = self.build_term(node.orelse)

        term = f"ifexp({test},{body},{orelse})"
        self.data.append(term)
        self.generic_visit(node)

        

