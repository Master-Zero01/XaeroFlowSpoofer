# XaeroFlowSpoofer

**Client-side chunk-finding countermeasure for Paper servers** — spoofs fluid flows and surface terrain *in the packets sent to clients* to frustrate client-side minimap mods (e.g. Xaero) that try to detect old/new chunk boundaries. The server world and physics remain unchanged — only what the client sees is slightly altered.

---

## Contents

- [Features](#features)  
- [Compatibility](#compatibility)  
- [Quick install (server)](#quick-install-server)  
- [Build from source (Maven)](#build-from-source-maven)  
- [Configuration](#configuration)  
- [How it works (brief)](#how-it-works-brief)  
- [Tuning & recommendations](#tuning--recommendations)  
- [Testing](#testing)  
- [Troubleshooting](#troubleshooting)  
- [Security & ethics](#security--ethics)  
- [Contributing](#contributing)  
- [License](#license)

---

## Features

- **Fluid spoofing** — intercepts outgoing `BLOCK_CHANGE` / `MULTI_BLOCK_CHANGE` packets and occasionally shows water/lava as air (or stationary) near chunk edges.
- **Fake terrain noise** — sends batched (`MULTI_BLOCK_CHANGE`) fake surface block updates along chunk edges to break deterministic terrain patterns clients rely on.
- **Client-only deception** — server-side physics, farms, and builds are unaffected.
- **Efficient** — uses `MULTI_BLOCK_CHANGE`, throttling (default: every 5 ticks), and capped edits per tick to minimize CPU and network impact.
- **No NMS** — implemented with ProtocolLib for cross-version stability.

---

## Compatibility

- **Server**: Paper (1.16+; tested on 1.20 / 1.21 family builds).  
- **ProtocolLib**: required — tested with **ProtocolLib 5.4.0** (artifact `net.dmulloy2:ProtocolLib:5.4.0`).  
- **Java**: JDK 17+ recommended (project examples used Java 17 — adjust `pom.xml` if you use a different Java).

> Always match your Paper/ProtocolLib versions. If you use a different Paper release, change the `paper-api` dependency version in `pom.xml`.

---

## Quick install (server)

1. Download/compile the plugin jar and place it in your server `plugins/` folder.  
2. Download `ProtocolLib` (place `ProtocolLib.jar` in `plugins/`).  
3. Start the server. The plugin will create a `config.yml` in `plugins/XaeroFlowSpoofer/`.  
4. Join with a client mod (like Xaero Minimap) and test near newly generated chunks.

---

## Build from source (Maven)

Example `pom.xml` should include `paper-api` and ProtocolLib repositories/dependencies (see project `pom.xml`). Standard build:

```bash
mvn clean package
# Result: target/XaeroFlowSpoofer-<version>.jar
