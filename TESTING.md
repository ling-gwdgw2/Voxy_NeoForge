# Voxy NeoForge 1.21.1 Testing Guide

## Prerequisites

Before setting up the testing environment, ensure you have:

- **Java 21 or later** (required for Minecraft 1.21.1)
- **Prism Launcher / CurseForge / Modrinth App** installed
- **Internet connection** for downloading Minecraft and mod dependencies

---

## Step 1: Create NeoForge 1.21.1 Profile

### 1.1 Create New Instance
1. Open Prism Launcher / CurseForge
2. In the Create New Instance dialog:
   - **Name:** `Voxy NeoForge 1.21.1 Testing`
   - **Version:** `1.21.1`
   - **Mod Loader:** **NeoForge**
   - **Loader Version:** `21.1.65` or newer (e.g., `21.1.73`+)

### 1.2 Configure Java Runtime & Memory
1. Open Instance Settings → **Java**
2. Ensure Java 21+ is selected
3. Set **Maximum memory allocation** to at least **4096 MiB** (recommended: 6144 - 8192 MiB for shaders and large LOD render distance).

### 1.3 JVM Arguments (Recommended)
```
-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1
```

---

## Step 2: Install Required Dependencies

Ensure the following mods are installed in the `mods/` directory:

| Mod | Required Version | Purpose |
| :--- | :--- | :--- |
| **Sodium (NeoForge)** | 0.6.9+ (or 0.8.x) | Core renderer & In-game Settings Menu |
| **Iris Shaders (NeoForge)** | 1.8.0+ (or 1.8.14) | *(Optional)* Shaderpack integration (Photon, Complementary, Solas) |

---

## Step 3: Install Voxy Mod

### 3.1 Build Voxy from Source
```bash
./gradlew build
```
The compiled mod file is located at:
```
build/libs/voxyNeoForge-V1-1.0.0.jar
```

### 3.2 Install Voxy
Copy `build/libs/voxyNeoForge-V1-1.0.0.jar` into your Minecraft instance `mods/` folder.

---

## Step 4: In-Game Settings UI (Sodium Voxy Tab)

In-game, open **Options → Video Settings → Voxy Tab**:

1. **General Page:**
   - **Enable Voxy:** Master switch (`[x]`)
   - **Chunk Ingestion:** Auto LOD capture (`[x]`)
   - **VRAM Geometry Budget:** `Auto` or `512MB - 1536MB`
   - **Auto World Pre-generation:** `[x]` / `[ ]`
   - **Auto Pre-gen Radius:** Slider (`16 - 128 chunks`)
   - **Pre-gen Worker Threads:** Slider (`1 - 4 threads`)
2. **Rendering Page:**
   - **LOD Rendering:** Toggle visual output
   - **Pixels^2 Subdivision:** Detail quality
   - **LOD Render Distance:** Slider (e.g. 16 = 512 chunks, 32 = 1024 chunks)
   - **LOD Boundary Overlap:** Smooth transitions (0-4)
   - **World Curvature:** Spherical planet effect (0 = flat, 50-5000)
   - **LOD Water Reflection (SSR):** Translucent screen-space reflections
   - **Environmental Fog:** Atmospheric blending
   - **Debug Statistics:** F3 HUD display

---

## Step 5: Testing Checklist

### 1. Basic Functionality
- [ ] **Mod loads without crashes** (NeoForge 1.21.1)
- [ ] **World loads successfully** (Singleplayer & Multiplayer)
- [ ] **LOD terrain renders seamlessly** beyond vanilla render distance
- [ ] **Fluid rendering works** (water, river, ocean LODs)
- [ ] **Biome tinting applies correctly** (grass, leaves, water color transitions)

### 2. World Pre-Generation (`/voxy pregen`)
- [ ] Test command: `/voxy pregen 32`
- [ ] Progress HUD appears on action bar with `%`, chunks count, and rate
- [ ] Generation radiates in a **circular shape 360°** outward from the player
- [ ] Chunks already stored in DB are **skipped instantly**
- [ ] Command `/voxy pregen cancel` cancels generation immediately
- [ ] Leaving world ("Save & Quit to Title") **stops background threads immediately**

### 3. Performance & Stability
- [ ] Stable 60-144+ FPS during fast flight (`/fly`, spectator mode)
- [ ] **ZSTD Compression**: Check world folder `.voxy` database size (saving >50% disk space)
- [ ] **No Black Square Chunks**: Distant LODs render in daylight without dark glitches
- [ ] **Zero Memory Leaks**: Memory remains stable over extended gameplay

### 4. Shaders & Iris Compatibility
- [ ] Test with **Photon Shader v1.3b** (Atmosphere, volumetric clouds, shadow blending)
- [ ] Test with **Complementary Reimagined / Unbound**
- [ ] Test with **Solas Shader**
- [ ] Verify water SSR reflections and fog integration

---

## Step 6: Troubleshooting

### 1. Viewing Logs
- Open `logs/latest.log` or check `crash-reports/`
- Look for `[Voxy]` prefix in log lines

### 2. Common Issues & Solutions
- **Multiplayer missing chunks**: Voxy auto-recovers lighting asynchronously; ensure server connection is stable.
- **VRAM Out of Memory**: Lower `VRAM Geometry Budget` to `512MB` or `768MB` in Video Settings → Voxy.
- **Shader depth mismatch**: Ensure Iris 1.8.x and compatible shaderpack (Photon, Complementary, Solas) are used.

---

**Happy Testing!**
