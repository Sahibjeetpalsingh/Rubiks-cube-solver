class RubiksCube3D {
  constructor(containerId) {
    this.containerId = containerId;
    this.container = document.getElementById(containerId);
    this.scene = null;
    this.camera = null;
    this.renderer = null;
    this.cubes = [];
    this.faceOrder = ['U', 'R', 'F', 'D', 'L', 'B'];
    this.faceStickers = {
      U: new Array(9),
      R: new Array(9),
      F: new Array(9),
      D: new Array(9),
      L: new Array(9),
      B: new Array(9)
    };
    this.camX = 0.5;
    this.camY = 0.5;
    this.animating = false;

    // Temp vectors to avoid allocations
    this._tmpA = new THREE.Vector3();
    this._tmpB = new THREE.Vector3();

    // Default face colors (can be overridden at runtime)
    this.faceColorHex = {
      U: 0xf8fafc,
      R: 0xef4444,
      F: 0x22c55e,
      D: 0xfacc15,
      L: 0xfb923c,
      B: 0x3b82f6
    };
    this.state = 'UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB';

    this.init();
  }

  init() {
    if (!this.container) return;

    this.scene = new THREE.Scene();
    this.scene.background = new THREE.Color(0xffffff);

    this.camera = new THREE.PerspectiveCamera(
      75,
      this.container.clientWidth / this.container.clientHeight,
      0.1,
      1000
    );
    this.camera.position.set(4, 4, 4);
    this.camera.lookAt(0, 0, 0);

    this.renderer = new THREE.WebGLRenderer({ antialias: true });
    this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
    this.renderer.setPixelRatio(window.devicePixelRatio);
    this.container.appendChild(this.renderer.domElement);

    this.createCubePieces();
    this.setupMouseControls();
    this.animate();

    window.addEventListener('resize', () => this.onResize());
  }

  createCubePieces() {
    const cubeGeometry = new THREE.BoxGeometry(0.92, 0.92, 0.92);
    const stickerGeometry = new THREE.PlaneGeometry(0.78, 0.78);

    for (let x = -1; x <= 1; x++) {
      for (let y = -1; y <= 1; y++) {
        for (let z = -1; z <= 1; z++) {
          const cube = new THREE.Mesh(
            cubeGeometry,
            new THREE.MeshStandardMaterial({ color: 0x1f2933 })
          );
          cube.position.set(x, y, z);
          cube.userData = { x, y, z };
          this.scene.add(cube);
          this.cubes.push(cube);
          this.attachFaceStickers(cube, stickerGeometry);
        }
      }
    }

    const ambient = new THREE.AmbientLight(0xffffff, 0.7);
    this.scene.add(ambient);
    const directional = new THREE.DirectionalLight(0xffffff, 0.6);
    directional.position.set(5, 5, 5);
    this.scene.add(directional);

    this.updateFromFacelets(this.state);
  }

  attachFaceStickers(cube, geometry) {
    const { x, y, z } = cube.userData;
    const offset = 0.51;

    if (y === 1) {
      this.addSticker(
        cube,
        'U',
        1 - z,
        x + 1,
        new THREE.Vector3(0, offset, 0),
        new THREE.Euler(-Math.PI / 2, 0, 0),
        geometry
      );
    }
    if (y === -1) {
      this.addSticker(
        cube,
        'D',
        z + 1,
        x + 1,
        new THREE.Vector3(0, -offset, 0),
        new THREE.Euler(Math.PI / 2, 0, 0),
        geometry
      );
    }
    if (z === 1) {
      this.addSticker(
        cube,
        'F',
        1 - y,
        x + 1,
        new THREE.Vector3(0, 0, offset),
        new THREE.Euler(0, 0, 0),
        geometry
      );
    }
    if (z === -1) {
      this.addSticker(
        cube,
        'B',
        1 - y,
        1 - x,
        new THREE.Vector3(0, 0, -offset),
        new THREE.Euler(0, Math.PI, 0),
        geometry
      );
    }
    if (x === 1) {
      this.addSticker(
        cube,
        'R',
        1 - y,
        1 - z,
        new THREE.Vector3(offset, 0, 0),
        new THREE.Euler(0, -Math.PI / 2, 0),
        geometry
      );
    }
    if (x === -1) {
      this.addSticker(
        cube,
        'L',
        1 - y,
        z + 1,
        new THREE.Vector3(-offset, 0, 0),
        new THREE.Euler(0, Math.PI / 2, 0),
        geometry
      );
    }
  }

  addSticker(cube, face, row, col, position, rotation, geometry) {
    if (row < 0 || row > 2 || col < 0 || col > 2) return;
    const material = new THREE.MeshStandardMaterial({ color: 0xffffff, side: THREE.DoubleSide });
    const sticker = new THREE.Mesh(geometry, material);
    sticker.position.copy(position);
    sticker.rotation.copy(rotation);
    sticker.renderOrder = 1;
    cube.add(sticker);
    this.faceStickers[face][row * 3 + col] = sticker;
    sticker.userData = { face, index: row * 3 + col, colorChar: face };
  }

  // Reset all cubies to their home positions and orientations
  resetGeometry() {
    // Reassign positions: cubes were created in order x=-1..1, y=-1..1, z=-1..1
    let i = 0;
    for (let x = -1; x <= 1; x++) {
      for (let y = -1; y <= 1; y++) {
        for (let z = -1; z <= 1; z++) {
          const cube = this.cubes[i++];
          cube.position.set(x, y, z);
          cube.userData.x = x;
          cube.userData.y = y;
          cube.userData.z = z;
          cube.quaternion.identity();
        }
      }
    }
  }

  // Sync sticker colors from facelets WITHOUT resetting geometry
  // Used after animation to correct any drift while preserving physical positions
  syncColors(facelets) {
    if (!facelets) return;
    const arr = typeof facelets === 'string' ? facelets.split('') : facelets;
    if (arr.length !== 54) return;
    
    // Find where stickers currently are based on geometry
    const layout = this.buildStickerLayout();
    let idx = 0;
    for (const face of this.faceOrder) {
      for (let i = 0; i < 9; i++) {
        const mesh = layout[face][i];
        if (mesh) this.applyColorToSticker(mesh, arr[idx]);
        idx++;
      }
    }
    this.state = arr.join('');
  }

  updateFromFacelets(facelets) {
    if (!facelets) return;
    const arr = typeof facelets === 'string' ? facelets.split('') : facelets;
    if (arr.length !== 54) return;
    
    // Reset cube geometry to home state before applying colors
    // This ensures stickers are at known positions
    this.resetGeometry();
    
    // Now stickers are at their original positions, apply colors directly
    let idx = 0;
    for (const face of this.faceOrder) {
      for (let i = 0; i < 9; i++) {
        const mesh = this.faceStickers[face][i];
        if (mesh) this.applyColorToSticker(mesh, arr[idx]);
        idx++;
      }
    }
    this.state = arr.join('');
  }

  applyColorToSticker(sticker, facelet) {
    sticker.material.color.setHex(this.colorToHex(facelet));
    sticker.userData.colorChar = facelet;
  }

  setFaceColors(faceColorHex) {
    if (!faceColorHex) return;
    for (const k of Object.keys(faceColorHex)) {
      const key = String(k).toUpperCase();
      const v = faceColorHex[k];
      if (!this.faceOrder.includes(key)) continue;
      if (typeof v === 'number') this.faceColorHex[key] = v;
      else if (typeof v === 'string') {
        const s = v.trim();
        if (s.startsWith('#')) this.faceColorHex[key] = parseInt(s.slice(1), 16);
        else if (s.startsWith('0x') || s.startsWith('0X')) this.faceColorHex[key] = parseInt(s.slice(2), 16);
      }
    }
    // Repaint current visible state with new palette.
    this.updateFromFacelets(this.state);
  }

  faceFromOffset(sticker) {
    // Determine which face the sticker is on by its *position* relative to its parent cubie.
    // This is more robust than relying on plane normals (which can be flipped by rotations).
    if (!sticker || !sticker.parent) return null;
    sticker.parent.updateWorldMatrix(true, false);
    sticker.updateWorldMatrix(true, false);

    const p = this._tmpA;
    const s = this._tmpB;
    sticker.parent.getWorldPosition(p);
    sticker.getWorldPosition(s);
    s.sub(p);

    const absX = Math.abs(s.x);
    const absY = Math.abs(s.y);
    const absZ = Math.abs(s.z);

    if (absX >= absY && absX >= absZ) return s.x > 0 ? 'R' : 'L';
    if (absY >= absX && absY >= absZ) return s.y > 0 ? 'U' : 'D';
    return s.z > 0 ? 'F' : 'B';
  }

  buildStickerLayout() {
    const layout = {
      U: new Array(9),
      R: new Array(9),
      F: new Array(9),
      D: new Array(9),
      L: new Array(9),
      B: new Array(9)
    };

    for (const cubie of this.cubes) {
      for (const child of cubie.children) {
        // Stickers are the plane meshes we attached via addSticker()
        if (!child || !child.userData || typeof child.userData.colorChar !== 'string') continue;
        const face = this.faceFromOffset(child);
        if (!face) continue;
        const coords = this.faceletCoords(face, cubie.userData);
        if (!coords) continue;
        const idx = coords.row * 3 + coords.col;
        layout[face][idx] = child;
      }
    }

    return layout;
  }

  faceletCoords(face, cubeData) {
    const { x, y, z } = cubeData;
    switch (face) {
      case 'U':
        return { row: 1 - z, col: x + 1 };
      case 'D':
        return { row: z + 1, col: x + 1 };
      case 'F':
        return { row: 1 - y, col: x + 1 };
      case 'B':
        return { row: 1 - y, col: 1 - x };
      case 'R':
        return { row: 1 - y, col: 1 - z };
      case 'L':
        return { row: 1 - y, col: z + 1 };
      default:
        return null;
    }
  }

  rebuildStateFromGeometry() {
    const layout = this.buildStickerLayout();
    this.state = this.faceOrder.reduce((acc, face) => {
      const faceArr = layout[face];
      for (let i = 0; i < 9; i++) {
        const mesh = faceArr[i];
        acc += mesh && mesh.userData && mesh.userData.colorChar ? mesh.userData.colorChar : face;
      }
      return acc;
    }, '');
  }

  async animateMove(move) {
    if (this.animating) return;
    this.animating = true;

    const face = move[0];
    const isPrime = move.includes("'");
    const isDouble = move.includes('2');

    let rotAxis;
    let layerAxis;
    let layerValue;

    switch (face) {
      case 'U':
        rotAxis = new THREE.Vector3(0, 1, 0);
        layerAxis = 'y';
        layerValue = 1;
        break;
      case 'D':
        rotAxis = new THREE.Vector3(0, -1, 0);
        layerAxis = 'y';
        layerValue = -1;
        break;
      case 'F':
        rotAxis = new THREE.Vector3(0, 0, 1);
        layerAxis = 'z';
        layerValue = 1;
        break;
      case 'B':
        rotAxis = new THREE.Vector3(0, 0, -1);
        layerAxis = 'z';
        layerValue = -1;
        break;
      case 'R':
        rotAxis = new THREE.Vector3(1, 0, 0);
        layerAxis = 'x';
        layerValue = 1;
        break;
      case 'L':
        rotAxis = new THREE.Vector3(-1, 0, 0);
        layerAxis = 'x';
        layerValue = -1;
        break;
      default:
        this.animating = false;
        return;
    }

    const layerCubes = this.cubes.filter(c => Math.abs(c.userData[layerAxis] - layerValue) < 0.1);

    const grp = new THREE.Group();
    this.scene.add(grp);
    layerCubes.forEach(c => {
      this.scene.remove(c);
      grp.add(c);
    });

    // Standard cube notation: clockwise when viewing the face
    // Three.js right-hand rule: positive angle = counterclockwise along axis
    // So we need NEGATIVE angle for standard (non-prime) moves
    let totalAngle = -Math.PI / 2;
    if (isPrime) totalAngle = Math.PI / 2;
    if (isDouble) totalAngle *= 2;

    const start = Date.now();
    const dur = 250;

    return new Promise(resolve => {
      const anim = () => {
        const t = Math.min((Date.now() - start) / dur, 1);
        const eased = t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;

        grp.rotation.set(0, 0, 0);
        grp.rotateOnAxis(rotAxis, totalAngle * eased);

        if (t < 1) {
          requestAnimationFrame(anim);
        } else {
          layerCubes.forEach(c => {
            const p = new THREE.Vector3();
            const q = new THREE.Quaternion();
            c.getWorldPosition(p);
            c.getWorldQuaternion(q);
            grp.remove(c);
            this.scene.add(c);

            const newX = Math.round(p.x);
            const newY = Math.round(p.y);
            const newZ = Math.round(p.z);
            c.position.set(newX, newY, newZ);
            c.userData.x = newX;
            c.userData.y = newY;
            c.userData.z = newZ;
            c.quaternion.copy(q);
          });
          this.scene.remove(grp);
          this.animating = false;
          this.rebuildStateFromGeometry();
          resolve();
        }
      };
      anim();
    });
  }

  colorToHex(col) {
    const key = String(col || '').toUpperCase();
    return this.faceColorHex[key] || 0x808080;
  }

  setupMouseControls() {
    let rightDrag = false;
    let leftDrag = false;
    let lastX = 0;
    let lastY = 0;
    let dragStartX = 0;
    let dragStartY = 0;
    let selectedCube = null;

    const raycaster = new THREE.Raycaster();
    const mouse = new THREE.Vector2();

    const getCube = (clientX, clientY) => {
      const rect = this.renderer.domElement.getBoundingClientRect();
      mouse.x = ((clientX - rect.left) / rect.width) * 2 - 1;
      mouse.y = -((clientY - rect.top) / rect.height) * 2 + 1;
      raycaster.setFromCamera(mouse, this.camera);

      const hits = raycaster.intersectObjects(this.cubes, true);
      if (hits.length > 0) {
        let obj = hits[0].object;
        while (obj.parent && !this.cubes.includes(obj)) {
          obj = obj.parent;
        }
        if (this.cubes.includes(obj)) {
          return obj;
        }
      }
      return null;
    };

    this.renderer.domElement.addEventListener('mousedown', (e) => {
      if (e.button === 2) {
        rightDrag = true;
        lastX = e.clientX;
        lastY = e.clientY;
        e.preventDefault();
        return;
      }

      if (e.button === 0 && !this.animating) {
        const cube = getCube(e.clientX, e.clientY);
        if (cube) {
          selectedCube = cube;
          leftDrag = true;
          dragStartX = e.clientX;
          dragStartY = e.clientY;
        }
      }
    });

    document.addEventListener('mousemove', (e) => {
      if (rightDrag) {
        this.camY += (e.clientX - lastX) * 0.01;
        this.camX += (e.clientY - lastY) * 0.01;
        lastX = e.clientX;
        lastY = e.clientY;
        return;
      }

      if (leftDrag && selectedCube && !this.animating) {
        const dx = e.clientX - dragStartX;
        const dy = e.clientY - dragStartY;
        const dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 30) {
          this.animating = true;
          leftDrag = false;

          const cubePos = selectedCube.userData;
          const cameraRight = new THREE.Vector3(1, 0, 0).applyQuaternion(this.camera.quaternion);
          const cameraUp = new THREE.Vector3(0, 1, 0).applyQuaternion(this.camera.quaternion);

          let layerAxis;
          let layerValue;
          let rotAxis;
          let angle;

          if (Math.abs(dx) > Math.abs(dy)) {
            const alignX = Math.abs(cameraUp.x);
            const alignY = Math.abs(cameraUp.y);
            const alignZ = Math.abs(cameraUp.z);

            if (alignX > alignY && alignX > alignZ) {
              layerAxis = 'x';
              layerValue = cubePos.x;
              rotAxis = new THREE.Vector3(1, 0, 0);
              angle = dx * cameraUp.x > 0 ? Math.PI / 2 : -Math.PI / 2;
            } else if (alignY > alignX && alignY > alignZ) {
              layerAxis = 'y';
              layerValue = cubePos.y;
              rotAxis = new THREE.Vector3(0, 1, 0);
              angle = dx * cameraUp.y > 0 ? Math.PI / 2 : -Math.PI / 2;
            } else {
              layerAxis = 'z';
              layerValue = cubePos.z;
              rotAxis = new THREE.Vector3(0, 0, 1);
              angle = dx * cameraUp.z > 0 ? Math.PI / 2 : -Math.PI / 2;
            }
          } else {
            const alignX = Math.abs(cameraRight.x);
            const alignY = Math.abs(cameraRight.y);
            const alignZ = Math.abs(cameraRight.z);

            if (alignX > alignY && alignX > alignZ) {
              layerAxis = 'x';
              layerValue = cubePos.x;
              rotAxis = new THREE.Vector3(1, 0, 0);
              angle = dy * cameraRight.x > 0 ? Math.PI / 2 : -Math.PI / 2;
            } else if (alignY > alignX && alignY > alignZ) {
              layerAxis = 'y';
              layerValue = cubePos.y;
              rotAxis = new THREE.Vector3(0, 1, 0);
              angle = dy * cameraRight.y > 0 ? Math.PI / 2 : -Math.PI / 2;
            } else {
              layerAxis = 'z';
              layerValue = cubePos.z;
              rotAxis = new THREE.Vector3(0, 0, 1);
              angle = dy * cameraRight.z > 0 ? Math.PI / 2 : -Math.PI / 2;
            }
          }

          const layerCubes = this.cubes.filter(c => Math.abs(c.userData[layerAxis] - layerValue) < 0.1);
          const grp = new THREE.Group();
          this.scene.add(grp);
          layerCubes.forEach(c => {
            this.scene.remove(c);
            grp.add(c);
          });

          const start = Date.now();
          const dur = 250;

          const anim = () => {
            const t = Math.min((Date.now() - start) / dur, 1);
            const eased = t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;

            grp.rotation.set(0, 0, 0);
            grp.rotateOnAxis(rotAxis, angle * eased);

            if (t < 1) {
              requestAnimationFrame(anim);
            } else {
              layerCubes.forEach(c => {
                const p = new THREE.Vector3();
                const q = new THREE.Quaternion();
                c.getWorldPosition(p);
                c.getWorldQuaternion(q);
                grp.remove(c);
                this.scene.add(c);

                const newX = Math.round(p.x);
                const newY = Math.round(p.y);
                const newZ = Math.round(p.z);
                c.position.set(newX, newY, newZ);
                c.userData.x = newX;
                c.userData.y = newY;
                c.userData.z = newZ;
                c.quaternion.copy(q);
              });
              this.scene.remove(grp);
              this.animating = false;
              this.rebuildStateFromGeometry();
              selectedCube = null;
            }
          };
          anim();
        }
      }
    });

    document.addEventListener('mouseup', (e) => {
      if (e.button === 2) {
        rightDrag = false;
      } else {
        leftDrag = false;
        selectedCube = null;
      }
    });

    this.renderer.domElement.addEventListener('contextmenu', (e) => e.preventDefault());
  }

  animate() {
    requestAnimationFrame(() => this.animate());

    this.camera.position.x = 5 * Math.sin(this.camY) * Math.cos(this.camX);
    this.camera.position.y = 5 * Math.sin(this.camX);
    this.camera.position.z = 5 * Math.cos(this.camY) * Math.cos(this.camX);
    this.camera.lookAt(0, 0, 0);

    this.renderer.render(this.scene, this.camera);
  }

  onResize() {
    const width = this.container.clientWidth;
    const height = this.container.clientHeight;

    if (width > 0 && height > 0) {
      this.camera.aspect = width / height;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(width, height);
    }
  }
}