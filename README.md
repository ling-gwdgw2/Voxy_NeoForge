# voxyNeoForge V1 (Minecraft 1.21.1)

> **Unofficial Native NeoForge Port** of the Voxy LOD mod for Minecraft 1.21.1, upgraded with native Sodium 0.8.12 GUI integration, Iris 1.8.14 & Photon Shader pipeline support, Smart Adaptive VRAM budgeting, and high-performance multi-threading.

---

## 🙏 Special Thanks & Attribution

**All original credit for Voxy goes to [MCRcortex](https://github.com/MCRcortex)**, the creator of this revolutionary GPU-driven LOD rendering engine.

- **Original Repository:** [MCRcortex/voxy](https://github.com/MCRcortex/voxy)
- **Original Author:** [MCRcortex](https://github.com/MCRcortex)
- **NeoForge 1.21.1 Port:** [ling-gwdgw2/Voxy_NeoForge](https://github.com/ling-gwdgw2/Voxy_NeoForge)

---

## 📜 License Notice

The original Voxy mod is licensed under **All Rights Reserved (ARR)** by MCRcortex. This community port is provided for personal use and non-commercial development. Please respect the original author's copyright.

---

## 🚀 Key Improvements in voxyNeoForge V1 (Changelog)

### 1. 🎨 Full Iris 1.8.14 & Photon Shader Compatibility
- **Native `#define VOXY` Macro**: Directly injected into Iris shader preprocessors, allowing shaders like *Photon v1.3b*, *Complementary Shaders*, and *Solas* to render distant LOD terrain seamlessly.
- **Depth Sampler Aliasing**: Added multi-alias bindings (`vxDepthTexOpaque`, `vxDepthTexTrans`, `dhDepthTex`, `dhDepthTex0`) to prevent missing texture crashes.
- **Shadow Pass Isolation**: Isolated shadow matrix calculations to prevent `Framebuffer incomplete (36054)` crashes and high-altitude flickering during fast flight.

### 2. 🧠 Smart Adaptive VRAM Geometry Budgeting
- **Auto-scaled VRAM Allocation**: Dynamically calculates safe geometry buffers based on GPU VRAM capacity:
  - **4 GB GPUs**: ~`512 MB` (~67 million quads)
  - **6 GB GPUs**: ~`768 MB` (~100 million quads — leaves 5+ GB for shaders/game)
  - **8 GB GPUs**: ~`1024 MB`
  - **12+ GB GPUs**: ~`1536 MB`
- **Eliminates PCIe Stuttering**: Prevents VRAM overflow onto system RAM, eliminating micro-stutters and sudden FPS drops.
- **In-Game VRAM Budget Setting**: Added configurable `VRAM Geometry Budget` in Sodium settings.

### 3. ⚙️ Direct Native Sodium 0.8.12 GUI Integration
- Built directly on Sodium's modern `ConfigEntryPoint` API (`ConfigBuilder`).
- Complete in-game settings tab under **Options -> Video Settings -> Voxy**:
  - *Enable Voxy / Master Switch*
  - *Service Worker Threads*
  - *Chunk Ingestion Toggle*
  - *Subdivision Size Slider (Dynamic Meshlet Resolution)*
  - *LOD Render Distance Slider (Up to 2048 chunks)*
  - *LOD Boundary Overlap (Prevents chunk seam pop-in)*
  - *World Curvature / Earth Curve Ratio (Spherical planet effect)*
  - *Environmental Fog Toggle*
  - *F3 Debug Render Statistics*

### 4. 🧵 Multi-Threading & CPU Workload Balancing
- Set background worker threads (`ModelBakerySubsystem`, `AsyncNodeManager`, `UnifiedServiceThreadPool`) to `Thread.NORM_PRIORITY - 1` and `Daemon`.
- Prevents Voxy background voxelization from starving Minecraft's main game loop or Sodium's chunk builders.

---

## 📋 Compatibility & Requirements

### Required Dependencies
| Dependency | Version | Mod Link |
| :--- | :--- | :--- |
| **Minecraft** | `1.21.1` | - |
| **NeoForge** | `21.1.x`+ | [NeoForge](https://neoforged.net/) |
| **Sodium** | `0.6.13` / `0.8.12` | [Modrinth](https://modrinth.com/mod/sodium) |
| **Forgified Fabric API** | `0.116.x`+ | [Modrinth](https://modrinth.com/mod/forgified-fabric-api) |

### Recommended Mods
| Dependency | Version | Mod Link |
| :--- | :--- | :--- |
| **Iris Shaders** | `1.8.x`+ | [Modrinth](https://modrinth.com/mod/iris) |
| **Lithium / Radium** | `0.15.x`+ | [Modrinth](https://modrinth.com/mod/lithium) |
| **Entity Culling** | `1.10.x`+ | [Modrinth](https://modrinth.com/mod/entityculling) |
| **FerriteCore** | `7.0.x`+ | [Modrinth](https://modrinth.com/mod/ferrite-core) |
| **ImmediatelyFast** | `1.3.x`+ | [Modrinth](https://modrinth.com/mod/immediatelyfast) |

---

## 🔨 Building from Source

```bash
git clone https://github.com/ling-gwdgw2/Voxy_NeoForge.git
cd Voxy_NeoForge
./gradlew build
```

The compiled mod JAR will be located at:
`build/libs/voxyNeoForge-V1-1.0.0.jar`

---

## 🌐 Links & References

- **Upstream Project (Fabric):** [github.com/MCRcortex/voxy](https://github.com/MCRcortex/voxy)
- **voxyNeoForge Repository:** [github.com/ling-gwdgw2/Voxy_NeoForge](https://github.com/ling-gwdgw2/Voxy_NeoForge)
