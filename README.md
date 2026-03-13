## Blackjack Interpreter



A small DSL and simulation engine for running Blackjack experiments, e.g.:

```text
simulate 1000 rounds;
```

The interpreter parses the input using ANTLR and runs the simulation with a simple Blackjack engine.

---

## How to run this project

### 1. Clone the repository

```bash
git clone https://github.com/Tesa44/Blackjack-interpreter.git
```

Open the project in **IntelliJ IDEA**.


### 2. Add ANTLR library in IntelliJ

To make sure ANTLR is available in the project:

1. In IntelliJ, **right‑click** the project root folder.
2. Select **Open Module Settings**.
3. Go to the **Dependencies** tab.
4. Click **+** → **Library** and add the **ANTLR** library.
5. Apply and close the dialog.


### 3. Generate ANTLR recognizers

1. In IntelliJ, locate `ExprLexer.g4` and `ExprParser.g4` in the `src` folder.
2. Right‑click **`ExprLexer.g4`** and choose **Generate ANTLR Recognizer**.
3. In the dialog, set the **Output directory** to a folder named `gen` (for example `.../Blackjack-interpreter/gen`) and confirm.
4. Repeat the same for **`ExprParser.g4`**, also outputting into the same `gen` folder.

You should now have a `gen` folder containing the generated ANTLR Java files.

### 4. Mark `gen` as a generated sources root

1. In IntelliJ’s **Project** view, right‑click the `gen` folder.
2. Select **Mark Directory As → Generated Sources Root**.

This tells IntelliJ to treat the ANTLR output as source code for compilation.

### 5. Build the project

In IntelliJ:

- Use **Build → Build Project** (or press `Ctrl+F9`).
- Ensure there are no compilation errors.

### 6. Run the interpreter

1. Open `Main.java` in `src`.
2. Right‑click inside the file and choose **Run 'Main.main()'**.

You should see output similar to:

```text
Simulated 1000 rounds -> playerWins=..., dealerWins=..., pushes=...
```

That’s it – you now have a working Blackjack simulation interpreter.