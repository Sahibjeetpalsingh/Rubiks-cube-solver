# 🎲 Rubik's Cube Solver

A web-based **Rubik's Cube Solver** with real-time 2D and 3D visualization. Uses **Kociemba's Two-Phase Algorithm** to find near-optimal solutions in 20 moves or fewer.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-rubikscube.fly.dev-brightgreen?style=for-the-badge)](https://rubikscube.fly.dev/)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

---

## ✨ Features

- 🧩 **Kociemba's Two-Phase Algorithm** - Finds optimal solutions in ~20 moves
- 🎬 **Animated Solving** - Watch the cube solve step-by-step in real-time
- 🖼️ **Dual Visualization** - Switch between 2D net view and interactive 3D view
- 📱 **Fully Responsive** - Works on desktop, tablet, and mobile devices
- 📁 **File Import** - Drag & drop `.txt` files with scrambles or 9×12 net format
- 📋 **Copy Solution** - One-click copy of the solution moves
- 🎯 **Standard Notation** - Supports all standard Rubik's Cube move notations

---

## 🚀 Live Demo

**👉 [rubikscube.fly.dev](https://rubikscube.fly.dev/)**

---

## 📸 Screenshots

### 2D View
The classic unfolded net view showing all 6 faces simultaneously.

### 3D Interactive View
Fully interactive 3D cube - drag to rotate the view, watch animations in real-time.

---

## 🎮 How to Use

### 1. Enter Your Scramble
Type moves in standard notation or paste a 9×12 color net:

```
R U R' U' R' F R2 U' R' U' R U R' F'
```

### 2. Apply the Scramble
Click **Apply** to scramble the virtual cube.

### 3. Solve & Watch
Click **Solve** to compute and animate the solution!

---

## 📝 Move Notation

| Move | Description |
|------|-------------|
| `R` | Right face clockwise |
| `R'` | Right face counter-clockwise |
| `R2` | Right face 180° |
| `L` | Left face clockwise |
| `U` | Upper face clockwise |
| `D` | Down face clockwise |
| `F` | Front face clockwise |
| `B` | Back face clockwise |

---

## 🛠️ Local Development

### Prerequisites
- **Java 17+** ([Download](https://adoptium.net/))

### Run Locally

**Windows:**
```bash
git clone https://github.com/Sahibjeetpalsingh/Rubiks-cube-solver.git
cd Rubiks-cube-solver
run.bat
```

**Mac/Linux:**
```bash
git clone https://github.com/Sahibjeetpalsingh/Rubiks-cube-solver.git
cd Rubiks-cube-solver
chmod +x run.sh
./run.sh
```

The server starts at **http://localhost:8080**

### Manual Build
```bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin RubikWebServer
```

---

## 🐳 Docker

```bash
# Build
docker build -t rubiks-solver .

# Run
docker run -p 8080:8080 rubiks-solver
```

---

## ☁️ Deployment

This project is configured for multiple deployment platforms:

| Platform | Config File | Status |
|----------|-------------|--------|
| [Fly.io](https://fly.io) | `fly.toml` | ✅ Deployed |
| [Railway](https://railway.app) | `railway.json` | Ready |
| [Render](https://render.com) | `render.yaml` | Ready |
| Docker | `Dockerfile` | Ready |

### Deploy to Fly.io
```bash
fly auth login
fly deploy
```

---

## 🧠 Algorithm

This solver uses **Kociemba's Two-Phase Algorithm**, one of the most efficient methods for solving a Rubik's Cube:

### Phase 1
Reduces the cube to the "G1" subgroup where only moves `<U, D, R2, L2, F2, B2>` are needed.

### Phase 2
Solves the cube completely using only half-turn moves on R, L, F, B faces.

**Average solution length:** ~18-20 moves (half-turn metric)

---

## 📁 Project Structure

```
rubiks-solver-2d-animated/
├── public/                 # Frontend assets
│   ├── index.html         # Main HTML
│   ├── styles.css         # Responsive styles
│   ├── app.js             # 2D visualization & logic
│   ├── cube3d.js          # 3D Three.js visualization
│   └── docs.html          # Documentation page
├── src/                    # Java backend
│   ├── RubikWebServer.java # HTTP server
│   ├── Solver.java        # Main solver interface
│   ├── Search.java        # Kociemba algorithm
│   ├── CoordCube.java     # Coordinate cube model
│   ├── CubieCube.java     # Cubie cube model
│   ├── FaceCube.java      # Face color model
│   └── ...                # Additional utilities
├── Dockerfile             # Docker configuration
├── fly.toml               # Fly.io deployment
├── railway.json           # Railway deployment
├── render.yaml            # Render deployment
├── run.bat                # Windows run script
└── run.sh                 # Unix run script
```

---

## 🎨 Color Scheme

| Face | Color | Code |
|------|-------|------|
| **U** (Up) | White | `#ffffff` |
| **R** (Right) | Red | `#dc2626` |
| **F** (Front) | Green | `#16a34a` |
| **D** (Down) | Yellow | `#eab308` |
| **L** (Left) | Orange | `#f97316` |
| **B** (Back) | Blue | `#2563eb` |

---

## 👨‍💻 Authors

- **Sahibjeet Singh** - [GitHub](https://github.com/Sahibjeetpalsingh)
- **Bhuvesh**

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🙏 Acknowledgments

- [Herbert Kociemba](http://kociemba.org/cube.htm) - Two-Phase Algorithm
- [Three.js](https://threejs.org/) - 3D graphics library
- [Fly.io](https://fly.io) - Free hosting platform

---

<p align="center">
  <b>⭐ Star this repo if you found it helpful!</b>
</p>
