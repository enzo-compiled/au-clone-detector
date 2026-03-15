import ast

class Collector2(ast.NodeVisitor):
    def __init__(self, offset = 1):
        self.data = []
        self.const_map = {}
        self.const_counter = offset
        self.var_map = {}
        self.var_counter = offset

    def handle_variable(self, name):
        if name not in self.var_map:
            self.var_map[name] = f"v{self.var_counter}"
            self.var_counter += 1
        return self.var_map[name]

    def handle_constant(self, node): 
        value = node.value

        if value not in self.const_map:
            name = f"c{self.const_counter}"
            self.const_map[value] = name
            self.const_counter += 1

        return self.const_map[value]

    def build_term(self, node):
        if node is None:
            return ""

        elif isinstance(node, ast.Constant):
            return self.handle_constant(node)

        elif isinstance(node, ast.Name):
            return self.handle_variable(node.id)

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
            if isinstance(node.func, ast.Name):
                func = node.func.id 
            else:
                func = self.build_term(node.func)
            args = [a for arg in node.args if (a := self.build_term(arg))]
            
            if not func: 
                return ""
            return f"{func}(" + ",".join(args) + ")"
        
        elif isinstance(node, ast.Attribute):
            value = self.build_term(node.value)
            return f"{value}.{node.attr}"

        elif isinstance(node, ast.Expr):
            return self.build_term(node.value)

        elif isinstance(node, ast.Assign):
            t = self.build_term(node.targets[0])
            v = self.build_term(node.value)
            if t and v:
                return f"assign({t},{v})"
            return ""

        elif isinstance(node, ast.FunctionDef):
            args = [self.handle_variable(a.arg) for a in node.args.args]
            body = [t for s in node.body if (t := self.build_term(s))]
            partes = args + body
            return f"def_{node.name}({','.join(partes)})"
        
        elif isinstance(node, ast.Return):
            if node.value:
                v = self.build_term(node.value)
            else:
                v = ""
            return f"return({v})"

        return ""


    #class ast.Assign(targets, value, type_comment)
    def visit_Assign(self, node):
        t = self.build_term(node.targets[0])
        v = self.build_term(node.value)
        self.data.append(f"assign({t},{v})")


    #class ast.AugAssign(target, op, value)
    def visit_AugAssign(self, node):
        t = self.build_term(node.target)
        right = self.build_term(node.value)
        op = type(node.op).__name__
        if t and right:
            self.data.append(f"assign({t},{op}({t},{right}))")


    #class ast.FunctionDef(name, args, body, decorator_list, returns, type_comment, type_params) 
    def visit_FunctionDef(self, node):
        args = [self.handle_variable(a.arg) for a in node.args.args]
        body = [t for s in node.body if (t := self.build_term(s))]
        partes = args + body
        self.data.append(f"def_{node.name}({','.join(partes)})")

    def visit_Expr(self, node):
        if isinstance(node.value, ast.Constant):
            return
        term = self.build_term(node.value)
        if term:
            self.data.append(term)

    #class ast.ClassDef(name, bases, keywords, body, decorator_list, type_params)
    def visit_ClassDef(self, node):
        body_terms = [t for stmt in node.body if (t := self.build_term(stmt))]
        term = f"class_{node.name}({','.join(body_terms)})"
        self.data.append(term)

    def visit_Return(self, node):
        if node.value:
            v = self.build_term(node.value)
            self.data.append(f"return({v})")
        else:
            self.data.append("return()")

    
    
    #control de flujo

    #class ast.If(test, body, orelse)
    def visit_If(self, node):
        test = self.build_term(node.test)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]
        orelse = [t for stmt in node.orelse if (t := self.build_term(stmt))]
        partes = [test] + body + orelse
        self.data.append(f"if({','.join(partes)})")

    def visit_For(self, node):
        target = self.build_term(node.target)
        iterator = self.build_term(node.iter)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]
        self.data.append(f"for({target},{iterator},{','.join(body)})")

    def visit_While(self, node):
        test = self.build_term(node.test)
        body = [t for stmt in node.body if (t := self.build_term(stmt))]
        self.data.append(f"while({test},{','.join(body)})")

    def visit_IfExp(self, node):
        test = self.build_term(node.test)
        body = self.build_term(node.body)
        orelse = self.build_term(node.orelse)
        self.data.append(f"ifexp({test},{body},{orelse})")
        

