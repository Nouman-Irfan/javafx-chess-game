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

<div align="center">

## ⚡ Professional Desktop Chess Experience

Built using Java, JavaFX, and Object-Oriented Programming principles.

---

</div>

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

<img src="PICTURES/gameplay.png" width="500"/>

---

## ⚔️ Mid Game Battle

<img src="PICTURES/midgame.png" width="500"/>

---

## ♚ Checkmate Screen

<img src="PICTURES/checkmate.png" width="500"/>

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

```txt
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

```txt
C:\javafx-sdk-24
```

---

# ⚙️ Configure JavaFX in IntelliJ IDEA

## 1️⃣ Open Project Structure

```txt
File → Project Structure
```

---

## 2️⃣ Add JavaFX Library

Go to:

```txt
Libraries → + → Java
```

Select:

```txt
javafx-sdk-24/lib
```

Press OK ✅

---

## 3️⃣ Configure VM Options

Go to:

```txt
Run → Edit Configurations
```

Inside **VM Options**, paste:

```txt
--module-path "YOUR_JAVAFX_LIB_PATH" --add-modules javafx.controls,javafx.fxml
```

Example:

```txt
--module-path "C:\javafx-sdk-24\lib" --add-modules javafx.controls,javafx.fxml
```

---

# 📦 Assets Placement Guide

⚠️ All images and assets MUST remain inside:

```txt
src/ASSETS/
```

---

## ♟️ Chess Piece Images

Store all chess piece PNG files inside:

```txt
src/ASSETS/pieces/
```

Required files:

```txt
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

```txt
src/ASSETS/game_end/
```

Required files:

```txt
white_wins.png
black_wins.png
draw.png
```

---

## 🧩 Chess Board Image

Board image location:

```txt
src/ASSETS/board.png
```

⚠️ Do NOT rename asset files unless updating image paths in code.

---

# 🚀 How To Run The Project

## 📥 Clone Repository

```bash
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
