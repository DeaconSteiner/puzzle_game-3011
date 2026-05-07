# puzzle_game-3011
This is a repo for the wordgame assignment for COSC 3011

# How to Compile and Run

### Step 1: Navigate to project folder. For example:

```bash
cd ~/repos/puzzle_game-3011
```

### Step 2: Compile Code

```bash
javac -d bin main.java src/puzzle/**/**/*.java
```
#### Explanation:
- `javac`: Java compiler
- `-d bin`: put compiled .class files into bin folder
- `src/puzzle/**/**/*.java`: compile all source files in wordgame package - Uses shell expansion to compile all files.

### Step 3: Run the Game
```bash
java -cp bin main
```

#### Explanation:
- `java`: runs Java programs
- `-cp bin`: tells Java to look for compiled classes in bin folder
- `main`: compiled main class

