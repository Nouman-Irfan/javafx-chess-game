# JavaFX-Chess-Game
♟️ A modern Java-based Chess Game featuring complete chess mechanics, move validation, turn-based gameplay, and interactive board logic. Built using object-oriented programming principles with a focus on clean architecture, game logic, and strategic gameplay. 🚀
<div align="center">

# ♟️ JavaFX Chess Game

### 🚀 Advanced Chess Engine & Interactive GUI Built with JavaFX

<img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=openjdk">
<img src="https://img.shields.io/badge/Framework-JavaFX-1E90FF?style=for-the-badge">
<img src="https://img.shields.io/badge/Game-Chess-success?style=for-the-badge">
<img src="https://img.shields.io/badge/Architecture-OOP-red?style=for-the-badge">
<img src="https://img.shields.io/badge/GUI-Desktop_App-blueviolet?style=for-the-badge">
<img src="https://img.shields.io/badge/License-GPLv3-yellow?style=for-the-badge">

<br><br>

<img src="https://upload.wikimedia.org/wikipedia/en/c/cc/JavaFX_Logo.png" width="150"/>

---

# ♚ Strategic Chess Experience Powered by JavaFX ♚

### 💻 A fully interactive JavaFX Chess Game implementing complete chess mechanics, intelligent move validation, smooth graphical gameplay, and modern object-oriented architecture.

<br>

## 👨‍💻 Developed By

# Muhammad Nouman & Aqsa Ismail

<br>

### 🚀 Built with passion for software engineering, game development, and problem solving.

</div>

---

# 🌟 Project Highlights

✨ Interactive JavaFX GUI
✨ Complete Chess Rules Implementation
✨ Accurate Move Validation System
✨ Dynamic Piece Rendering
✨ Real-Time Check & Checkmate Detection
✨ Smooth Turn-Based Gameplay
✨ Modern Object-Oriented Architecture
✨ Professional Asset Management
✨ Beginner-Friendly Yet Scalable Code Structure

---

# 📸 Game Preview

<div align="center">

## ♟️ Main Gameplay

![Gameplay](PICTURES/gameplay.png)

---

## ⚔️ Mid Game Battle

![Midgame](PICTURES/midgame.png)

---

## ♚ Checkmate Screen

![Checkmate](PICTURES/checkmate.png)

</div>

---

# 🎮 Gameplay Features

<table>
<tr>
<td>♙ Pawn Logic</td>
<td>♖ Rook Logic</td>
<td>♘ Knight Logic</td>
</tr>

<tr>
<td>♗ Bishop Logic</td>
<td>♕ Queen Logic</td>
<td>♔ King Logic</td>
</tr>

<tr>
<td>⚔️ Piece Capturing</td>
<td>🧠 Move Validation</td>
<td>🚫 Illegal Move Prevention</td>
</tr>

<tr>
<td>♚ Check Detection</td>
<td>🏆 Checkmate Detection</td>
<td>🔄 Turn Management</td>
</tr>

<tr>
<td>🎨 GUI Rendering</td>
<td>🖱️ Mouse Interaction</td>
<td>⚡ Real-Time Updates</td>
</tr>
</table>

---

# 🛠️ Technologies Used

<div align="center">

| Technology             | Purpose                   |
| ---------------------- | ------------------------- |
| ☕ Java                 | Core Programming Language |
| 🎨 JavaFX              | GUI Development           |
| 🧠 OOP                 | Code Architecture         |
| ♟️ Chess Logic         | Gameplay Mechanics        |
| ⚡ Event Handling       | User Interaction          |
| 💻 Desktop Application | Application Environment   |

</div>

---

# 📂 Project Structure

```txt id="vtr7za"
JavaFX-Chess-Game/
│
├── src/
│   │
│   ├── Main.java
│   │
│   └── ASSETS/
│       │
│       ├── board.png
│       │
│       ├── pieces/
│       │   ├── wp.png
│       │   ├── wr.png
│       │   ├── wn.png
│       │   ├── wb.png
│       │   ├── wq.png
│       │   ├── wk.png
│       │   ├── bp.png
│       │   ├── br.png
│       │   ├── bn.png
│       │   ├── bb.png
│       │   ├── bq.png
│       │   └── bk.png
│       │
│       └── game_end/
│           ├── white_wins.png
│           ├── black_wins.png
│           └── draw.png
│
├── PICTURES/
│   ├── gameplay.png
│   ├── midgame.png
│   └── checkmate.png
│
├── README.md
├── LICENSE
└── .gitignore
```

---

# ⚙️ JavaFX Setup Guide

## ✅ Requirements

Before running the project, install:

* ☕ Java JDK 17 or higher
* 🎨 JavaFX SDK
* 💻 IntelliJ IDEA

---

# 📥 Download JavaFX SDK

Download JavaFX SDK from:

https://gluonhq.com/products/javafx/

After downloading:
1️⃣ Extract the folder
2️⃣ Remember the JavaFX SDK location

Example:

```txt id="dffl9g"
C:\javafx-sdk-24
```

---

# ⚙️ Configure JavaFX in IntelliJ IDEA

## 1️⃣ Open Project Structure

```txt id="v4l0eg"
File → Project Structure
```

---

## 2️⃣ Add JavaFX Library

Go to:

```txt id="jlwmr7"
Libraries → + → Java
```

Select:

```txt id="92sm5x"
javafx-sdk-24/lib
```

Press OK ✅

---

## 3️⃣ Configure VM Options

Go to:

```txt id="nd4r2z"
Run → Edit Configurations
```

Inside **VM Options**, paste:

```txt id="i08xci"
--module-path "YOUR_JAVAFX_LIB_PATH" --add-modules javafx.controls,javafx.fxml
```

Example:

```txt id="cjlwm6"
--module-path "C:\javafx-sdk-24\lib" --add-modules javafx.controls,javafx.fxml
```

---

# 📦 Assets Placement Guide

⚠️ All images and assets MUST remain inside:

```txt id="qwb8y2"
src/ASSETS/
```

---

## ♟️ Chess Piece Images

Store all chess piece PNG files inside:

```txt id="1fp7n6"
src/ASSETS/pieces/
```

Required files:

```txt id="r4l72w"
wp.png
wr.png
wn.png
wb.png
wq.png
wk.png

bp.png
br.png
bn.png
bb.png
bq.png
bk.png
```

---

## 🏆 Game End Screens

Store game result images inside:

```txt id="e6j9im"
src/ASSETS/game_end/
```

Required files:

```txt id="od7xjx"
white_wins.png
black_wins.png
draw.png
```

---

## 🧩 Chess Board Image

Board image location:

```txt id="z7s5b9"
src/ASSETS/board.png
```

---

# 🚀 How To Run The Project

## 📥 Clone Repository

```bash id="x2w2u7"
git clone https://github.com/Nouman-Irfan/JavaFX-Chess-Game.git
```

---

## 💻 Open Project

1️⃣ Open IntelliJ IDEA
2️⃣ Click **Open**
3️⃣ Select project folder
4️⃣ Configure JavaFX SDK
5️⃣ Run `Main.java`

---

# 📚 Learning Objectives

This project was developed to strengthen understanding of:

* 🧠 Object-Oriented Programming
* 🎨 GUI Development using JavaFX
* ⚡ Event Handling
* ♟️ Chess Algorithms
* 💻 Software Architecture
* 🚀 Game Development
* 🔍 Problem Solving
* 📦 Resource Management

---

# 🔒 License

This project is licensed under the **GNU GPL v3 License** 📜

---

<div align="center">

# 👨‍💻 Authors

## Muhammad Nouman & Aqsa Ismail

💻 Computer Science Students
🚀 Aspiring Software Engineers
♟️ Java & Game Developers

<br>

### ⭐ If you like this project, consider giving it a star on GitHub!

<br>

## 🚀 “Great developers don’t just write code — they build systems.”

</div>
