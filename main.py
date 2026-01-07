import ast, astor

def main():
    with open("test_1.py","r") as f:
        file = f.read()
        tree = ast.parse(file)
        print(astor.dump_tree(tree))

if __name__ == "__main__":
    main()