# Au Clone Detector App

**AU Clone Detector** is a tool for detecting code clones based on the concept of **anti-unification**, enabling the identification of structural and semantic similarities between code fragments through their representation as Abstract Syntax Trees (AST).

---

## Description

Code clone detection is a relevant problem in software engineering, as duplication can negatively impact maintainability, introduce bugs, and increase system complexity.

This project implements an approach based on:

-  **Anti-unification**: a technique to generalize expressions and extract common patterns.
-  **Abstract Syntax Trees (AST)**: structural representation of source code.
-  **Structural and semantic analysis** instead of plain text comparison.

---

## How It Works

1. Source code is parsed into AST representations.
2. Pairs of subtrees are compared.
3. **Anti-unification** is applied to compute a common generalization (you can use Nominal or Unranked anti-unification in this app).
4. Potential clones are reported.

---

## Installation

### Requirements

- Python 3.x *(adjust if needed)*
- Project dependencies

### Steps

```bash
git clone https://github.com/enzo-compiled/au-clone-detector.git
cd au-clone-detector
pip install -r requirements.txt
```
## Run the app

### uv

Run as a desktop app:

```bash
uv run flet run
```

### Linux

```bash
flet build linux -v
```

For more details on building Linux package, refer to the [Linux Packaging Guide](https://docs.flet.dev/publish/linux/).

### Windows

```bash
flet build windows -v
```

For more details on building Windows package, refer to the [Windows Packaging Guide](https://docs.flet.dev/publish/windows/).

# Sources

- You can check out this [link](https://www3.risc.jku.at/projects/stout/library.html) the **Library of Unification and Anti-Unification Algorithms**
- For Nominal algorithm, there is this [web app](https://nau-eq.web.app/index.html), developed in the java language, and the paper [Nominal Anti-Unification Modulo Equational Theories](https://nau-eq.web.app/preprint_jlamp.pdf) for **Alexander Baumgartner and Daniele Nantes-Sobrinho**, 2025.
- For the Unranked algorithm, there's also a [web app](https://www3.risc.jku.at/projects/stout/software/antiunify.php) (developed in java too) based in the paper [Anti-Unification for Unranked Terms and Hedges](http://link.springer.com/content/pdf/10.1007%2Fs10817-013-9285-6.pdf), by Temur Kutsia, Jordi Levy, Mateu Villaret, 2014.
