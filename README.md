# Java Graphing Calculator

A lightweight, interactive graphing calculator built in Java using Swing. It allows users to input mathematical functions (like `sin(x)`, `x^2`, etc.), and displays their graphs. Features include zooming, panning, gridlines, axis ticks with labels, and automatic or manual control over tick spacing.

---

## Features

- Plot mathematical functions (e.g., `sin(x)`, `x^2 + 2x`, `exp(-x)`)
- Zoom in/out using mouse scroll
- Pan across the graph with click-and-drag
- Automatically adjusting gridlines and axis ticks
- Tick labels with smart formatting (e.g., decimals vs. integers)
- Responsive redraw when window is resized

---

## Technologies Used

- Java 8+
- Swing (for GUI rendering)
- [exp4j] https://www.objecthunter.net/exp4j/ (for parsing and evaluating user-defined math expressions)

---

## Folder Structure

GraphingCalculatorProject/
│
├── src/
│   ├── GraphingCalculator.java     # Main class with GUI window and input field
│   └── GraphPanel.java             # Custom JPanel that handles graph rendering
│
├── lib/
│   └── exp4j-0.4.8.jar             # External math expression library
│
├── .vscode/
│   ├── launch.json                # VS Code launch configuration
│   └── settings.json              # VS Code Java project settings
│
└── README.md

---

## How to Run

1. **Install Java** (JDK 8 or higher).
2. **Download exp4j** and place it in the `lib/` folder.
   - Download from: https://www.objecthunter.net/exp4j/download.html
3. **Compile the project** from the root folder:
   ```cmd
   javac -cp "lib/exp4j-0.4.8.jar;src" src\*.java
4. **Run the project** 
   java -cp "lib/exp4j-0.4.8.jar;src" GraphingCalculator

