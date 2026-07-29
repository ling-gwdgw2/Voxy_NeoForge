# voxyNeoForge V1 (Minecraft 1.21.1)

> **Unofficial NeoForge port** of the Voxy mod, enhanced with native Iris/Oculus Shader pipeline support, direct Sodium GUI integration, and Radium/Lithium compatibility.

## Special Thanks

**All credit for Voxy goes to [MCRcortex](https://github.com/MCRcortex)**, the original author and creator of this incredible LOD rendering mod.

- **Original Repository:** [MCRcortex/voxy](https://github.com/MCRcortex/voxy)
- **Original Author:** [MCRcortex](https://github.com/MCRcortex)

This repository is a community port to NeoForge 1.21.1, created because the original author indicated they will not be backporting to this version. We are deeply grateful for MCRcortex's work on Voxy.

## License Notice

The original Voxy mod is licensed under **All Rights Reserved** by MCRcortex. This port is provided for personal use. Please respect the original author's licensing terms.

---

## About

**voxyNeoForge V1** is a Level-of-Detail (LOD) rendering mod for Minecraft NeoForge 1.21.1 that extends your view distance far beyond vanilla limits by rendering distant terrain at lower detail levels using GPU-driven voxel mesh rendering.

## Key Improvements in voxyNeoForge V1

- **✨ Iris & Oculus Shader Support:** Full GLSL pipeline patching, depth buffer attachments, and shadow pass compatibility for Iris and Oculus shaders.
- **⚙️ Native Direct Sodium GUI Integration:** Automatic injection of Voxy's options tab into Sodium's Video Settings screen without requiring external API mods.
- **⚡ Radium & Lithium Interop Fix:** Classloader-safe palette conversion supporting both Lithium (`lithium`) and Radium (`radium`) performance mods.
- **🎨 Custom Branding & Logo:** Refreshed voxyNeoForge V1 visual identity and branding.

## Why This Port?

| Aspect | Native NeoForge Port (this repo) | Sinytra Connector |
|--------|----------------------------------|-------------------|
| **Performance** | No translation overhead | Runtime translation layer |
| **Shader Support** | Native Iris/Oculus GLSL integration | Requires translation bridge |
| **Mod Integration** | Direct Sodium & NeoForge API calls | Fabric API emulation via FFAPI |
| **GUI Integration** | Direct Native Injection into Sodium GUI | External API dependency |
| **Stability** | Tested against NeoForge directly | May have edge cases from translation |

## Features

### Working Features
- LOD terrain rendering beyond vanilla render distance
- Smooth transitions between LOD and vanilla chunks
- **Iris / Oculus Shader Pack Compatibility** (Lighting, shadows, depth cutout)
- **Direct Sodium Video Settings Integration** (Voxy tab inside Sodium settings)
- **Radium & Lithium Compatibility** (Support for optimized block state palettes)
- Block model baking for all render types (solid, cutout, cutout_mipped, translucent)
- Delayed chunk unloading to prevent pop-out effects
- Dynamic LOD sub-division scaling based on realtime FPS

## Requirements

### Required Dependencies

| Dependency | Version | Link |
|------------|---------|------|
| Minecraft | 1.21.1 | - |
| NeoForge | 21.1.x | [NeoForge](https://neoforged.net/) |
| Sodium | mc1.21.1-0.6.13-neoforge | [Modrinth](https://modrinth.com/mod/sodium/version/mc1.21.1-0.6.13-neoforge) |
| Forgified Fabric API | 0.116.7+2.2.0+1.21.1 | [Modrinth](https://modrinth.com/mod/forgified-fabric-api/version/0.116.7+2.2.0+1.21.1) |

### Recommended Dependencies

| Dependency | Purpose | Link |
|------------|---------|------|
| Iris / Oculus | Shader pack rendering support | [Modrinth](https://modrinth.com/mod/iris) |
| Radium / Lithium | General chunk and tick performance improvements | [Modrinth](https://modrinth.com/mod/radium) |
| Reese's Sodium Options | Enhanced UI layout for Sodium settings | [Modrinth](https://modrinth.com/mod/reeses-sodium-options) |

## Installation

> **Note:** Due to Voxy's ARR (All Rights Reserved) license, compiled JARs are built from source.

1. Install NeoForge for Minecraft 1.21.1
2. Install required dependencies (see above)
3. Build voxyNeoForge V1 from source (see below)
4. Place the built JAR (`voxyNeoForge-V1-1.0.0.jar`) in your `mods` folder

## Building from Source

```bash
git clone https://github.com/ling-gwdgw2/Voxy_NeoForge.git
cd Voxy_NeoForge
./gradlew build
```

The built JAR will be located at:
`build/libs/voxyNeoForge-V1-1.0.0.jar`

## Contributing

For development guidelines and reference architecture, see [CLAUDE.md](CLAUDE.md).

## Links

- **Original Voxy:** [github.com/MCRcortex/voxy](https://github.com/MCRcortex/voxy)
- **voxyNeoForge V1 Repository:** [github.com/ling-gwdgw2/Voxy_NeoForge](https://github.com/ling-gwdgw2/Voxy_NeoForge)
