const SOLVED = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

let currentMode = "2d";
let cube3d = null;

// ===== Dynamic color palette =====
// Default (classic) scheme: U=white, R=red, F=green, D=yellow, L=orange, B=blue
const DEFAULT_FACE_COLORS = {
  U: '#f8fafc',
  R: '#ef4444',
  F: '#22c55e',
  D: '#facc15',
  L: '#fb923c',
  B: '#3b82f6'
};

// If user pastes a net using color letters (W/Y/R/O/G/B), we'll map those to hex.
const COLOR_LETTER_TO_HEX = {
  W: '#f8fafc',
  Y: '#facc15',
  R: '#ef4444',
  O: '#fb923c',
  G: '#22c55e',
  B: '#3b82f6'
};

let currentFaceColors = { ...DEFAULT_FACE_COLORS };

function applyFaceColors(faceColors) {
  currentFaceColors = { ...currentFaceColors, ...faceColors };
  for (const face of Object.keys(DEFAULT_FACE_COLORS)) {
    const v = currentFaceColors[face];
    if (v) document.documentElement.style.setProperty(`--${face}`, v);
  }

  // Also set variables for color-letter nets (W/Y/R/O/G/B) in case the backend
  // returns those directly.
  for (const [k, v] of Object.entries(COLOR_LETTER_TO_HEX)) {
    document.documentElement.style.setProperty(`--${k}`, v);
  }
  if (cube3d && typeof cube3d.setFaceColors === 'function') {
    const hexMap = {};
    for (const f of Object.keys(DEFAULT_FACE_COLORS)) {
      const v = currentFaceColors[f];
      if (typeof v === 'string' && v.startsWith('#')) hexMap[f] = parseInt(v.slice(1), 16);
    }
    cube3d.setFaceColors(hexMap);
  }
}

function tryApplyPaletteFromNet(text) {
  const lines = String(text || '')
    .split(/\r?\n/)
    .map(l => l.replace(/\s+/g, ''))
    .filter(Boolean);
  if (lines.length < 9) return false;

  const l9 = lines.slice(0, 9);
  const ok =
    l9[0].length >= 3 && l9[1].length >= 3 && l9[2].length >= 3 &&
    l9[3].length >= 12 && l9[4].length >= 12 && l9[5].length >= 12 &&
    l9[6].length >= 3 && l9[7].length >= 3 && l9[8].length >= 3;
  if (!ok) return false;

  const pick = (r, c) => (l9[r] && l9[r][c] ? l9[r][c] : null);
  const centers = {
    U: pick(1, 1),
    L: pick(4, 1),
    F: pick(4, 4),
    R: pick(4, 7),
    B: pick(4, 10),
    D: pick(7, 1)
  };

  const faceColors = {};
  for (const face of Object.keys(centers)) {
    const c = String(centers[face] || '').toUpperCase();
    const hex = COLOR_LETTER_TO_HEX[c];
    if (!hex) return false;
    faceColors[face] = hex;
  }

  applyFaceColors(faceColors);
  return true;
}

const inputEl = document.getElementById("input");
const fileEl = document.getElementById("file");
const dropEl = document.getElementById("drop");

const applyBtn = document.getElementById("apply");
const solveBtn = document.getElementById("solve");
const resetBtn = document.getElementById("reset");

const statusEl = document.getElementById("status");
const solutionEl = document.getElementById("solution");
const moveLabelEl = document.getElementById("moveLabel");
const movesCountEl = document.getElementById("movesCount");
const movesCount3dEl = document.getElementById("movesCount3d");
const copyBtn = document.getElementById("copy");

const mode2dBtn = document.getElementById("mode2d");
const mode3dBtn = document.getElementById("mode3d");
const cubePanel2d = document.getElementById("cubePanel2d");
const cubePanel3d = document.getElementById("cubePanel3d");

let current = SOLVED;
let animating = false;
let animToken = 0;

const faces = {};
document.querySelectorAll(".face").forEach(faceEl => {
  const f = faceEl.dataset.face;
  const grid = faceEl.querySelector(".grid");
  faces[f] = {faceEl, stickers: []};
  for (let i=0;i<9;i++){
    const s = document.createElement("div");
    s.className = "sticker";
    s.dataset.i = i;
    grid.appendChild(s);
    faces[f].stickers.push(s);
  }
});

// Paint only the 2D net (no 3D update)
function paint2d(facelets){
  if (!facelets || facelets.length !== 54) return;
  current = facelets;
  const order = ["U","R","F","D","L","B"];
  let idx = 0;
  for (const f of order){
    for (let i=0;i<9;i++){
      const col = facelets[idx++];
      faces[f].stickers[i].style.background = `var(--${col})`;
    }
  }
}

// Paint both 2D and 3D (used for initial setup / direct state changes)
function paint(facelets){
  paint2d(facelets);
  // Also update 3D if loaded
  if (cube3d) {
    cube3d.updateFromFacelets(facelets);
  }
}

async function postText(url, text){
  try {
    const res = await fetch(url, {method:"POST", body: text});
    const contentType = res.headers.get("content-type");
    
    if (!contentType || !contentType.includes("application/json")) {
      throw new Error("Server returned non-JSON response: " + res.status);
    }

    const text_response = await res.text();
    if (!text_response.trim()) {
      throw new Error("Server returned empty response");
    }

    const data = JSON.parse(text_response);
    
    if (!res.ok) {
      throw new Error(data.error || `Request failed with status ${res.status}`);
    }
    
    return data;
  } catch (err) {
    if (err instanceof SyntaxError) {
      throw new Error("Invalid JSON from server: " + err.message);
    }
    throw err;
  }
}

function setStatus(t){ statusEl.textContent = t; }
function setSolution(t){ solutionEl.textContent = t; }
function setMoveLabel(t){ moveLabelEl.textContent = t; }
function setMovesCount(t){
  movesCountEl.textContent = t;
  if (movesCount3dEl) movesCount3dEl.textContent = t;
}

function affectedFaces(face){
  // For 2D animation: pulse face + its ring neighbors
  switch(face){
    case "U": return ["U","F","R","B","L"];
    case "D": return ["D","F","R","B","L"];
    case "F": return ["F","U","R","D","L"];
    case "B": return ["B","U","R","D","L"];
    case "R": return ["R","U","F","D","B"];
    case "L": return ["L","U","F","D","B"];
    default: return [face];
  }
}

async function animateStep(move, nextFacelets){
  const f = move[0];
  const token = animToken;

  faces[f].faceEl.classList.add("turning");
  for (const a of affectedFaces(f)){
    faces[a].faceEl.classList.add("pulse");
  }

  // Animate the move visually
  if (currentMode === "3d" && cube3d) {
    await cube3d.animateMove(move);
    if (token !== animToken) return;
  } else {
    await sleep(250);
    if (token !== animToken) return;
  }
  
  // Always use server's trace state for both 2D and 3D
  const after = nextFacelets || current;
  if (after) {
    // Update 2D
    paint2d(after);
    // Reset 3D to clean state and paint server's colors
    // This ensures 3D matches 2D exactly
    if (cube3d) {
      cube3d.updateFromFacelets(after);
    }
    current = after;
  }

  await sleep(60);
  faces[f].faceEl.classList.remove("turning");
  for (const a of affectedFaces(f)){
    faces[a].faceEl.classList.remove("pulse");
  }
}

function sleep(ms){ return new Promise(r=>setTimeout(r, ms)); }

function parseMovesString(solutionText) {
  return String(solutionText || '')
    .trim()
    .split(/\s+/)
    .filter(m => /^[URFDLB](2|'|)?$/.test(m));
}

// Apply / Set on cube
applyBtn.addEventListener("click", async ()=>{
  if (animating) return;
  try{
    setStatus("Reading input…");
    if (!inputEl.value.trim()) {
      setStatus("Please enter moves or a cube state.");
      return;
    }

    // If this looks like a color-letter net, update the UI palette to match its centers.
    tryApplyPaletteFromNet(inputEl.value);

    const data = await postText("/api/state", inputEl.value);
    paint(data.facelets);
    setSolution("—");
    setMoveLabel("Move: —");
    setMovesCount("Moves: —");
    setStatus("Cube updated.");
  }catch(e){
    console.error("Apply error:", e);
    setStatus("Error: " + (e.message || "Failed to update cube"));
  }
});

// Solve
solveBtn.addEventListener("click", async ()=>{
  if (animating) return;
  const token = ++animToken;

  try{
    animating = true;
    applyBtn.disabled = true;
    solveBtn.disabled = true;

    setStatus("Solving…");
    if (!inputEl.value.trim()) {
      setStatus("Please enter moves or a cube state to solve.");
      return;
    }

    // If a color-letter net is provided, match the palette to its center colors.
    tryApplyPaletteFromNet(inputEl.value);
    
    // Initialize 3D if not already done (for animation during solve)
    if (!cube3d) {
      cube3d = new RubiksCube3D("canvas3d");
      // Push the current palette into the 3D renderer
      applyFaceColors({});
      cube3d.updateFromFacelets(current);
    }
    
    const data = await postText("/api/solve", inputEl.value);
    if (token !== animToken) return;

    paint(data.facelets);
    const sol = data.solution || "—";
    setSolution(sol);

    let moves = Array.isArray(data.moves) ? data.moves : [];
    const trace = Array.isArray(data.trace) ? data.trace : [];
    if (!moves.length && sol && sol !== "—") moves = parseMovesString(sol);

    setMovesCount(`Moves: ${moves.length || "—"}`);

    if (!moves.length){
      setStatus("Already solved.");
      setMoveLabel("Move: —");
      return;
    }

    setStatus("Animating solve…");

    for (let i=0;i<moves.length;i++){
      if (token !== animToken) return;
      const mv = moves[i];
      setMoveLabel(`Move: ${mv} (${i+1}/${moves.length})`);
      await animateStep(mv, trace[i+1]);
      await sleep(60);
    }

    setStatus("Solved.");
    setMoveLabel("Move: —");
  }catch(e){
    console.error("Solve error:", e);
    setStatus("Error: " + (e.message || "Failed to solve cube"));
  }finally{
    animating = false;
    applyBtn.disabled = false;
    solveBtn.disabled = false;
  }
});

// Reset
resetBtn.addEventListener("click", ()=>{
  ++animToken;
  animating = false;
  applyBtn.disabled = false;
  solveBtn.disabled = false;

  inputEl.value = "";
  applyFaceColors(DEFAULT_FACE_COLORS);
  paint(SOLVED);
  setSolution("—");
  setMoveLabel("Move: —");
  setMovesCount("Moves: —");
  setStatus("Ready. Cube starts solved.");
});

// Copy
copyBtn.addEventListener("click", async ()=>{
  const t = solutionEl.textContent || "";
  if (!t || t==="—") return;
  try{
    await navigator.clipboard.writeText(t);
    setStatus("Copied.");
  }catch{
    setStatus("Copy blocked by browser.");
  }
});

// Drag & drop file
dropEl.addEventListener("dragover", (e)=>{ e.preventDefault(); dropEl.classList.add("hover"); });
dropEl.addEventListener("dragleave", ()=> dropEl.classList.remove("hover"));
dropEl.addEventListener("drop", async (e)=>{
  e.preventDefault();
  dropEl.classList.remove("hover");
  const f = e.dataTransfer.files && e.dataTransfer.files[0];
  if (!f) return;
  inputEl.value = await f.text();
  setStatus(`Loaded ${f.name}.`);
});
fileEl.addEventListener("change", async ()=>{
  const f = fileEl.files && fileEl.files[0];
  if (!f) return;
  inputEl.value = await f.text();
  setStatus(`Loaded ${f.name}.`);
});

// init
applyFaceColors(DEFAULT_FACE_COLORS);
paint(SOLVED);

// Mode switching
mode2dBtn.addEventListener("click", ()=>{
  currentMode = "2d";
  mode2dBtn.classList.add("active");
  mode3dBtn.classList.remove("active");
  cubePanel2d.classList.remove("hidden");
  cubePanel3d.classList.add("hidden");
});

mode3dBtn.addEventListener("click", ()=>{
  currentMode = "3d";
  mode3dBtn.classList.add("active");
  mode2dBtn.classList.remove("active");
  cubePanel2d.classList.add("hidden");
  cubePanel3d.classList.remove("hidden");
  
  if (!cube3d) {
    cube3d = new RubiksCube3D("canvas3d");
    applyFaceColors({});
    cube3d.updateFromFacelets(current);
  }
  cube3d.onResize();
});