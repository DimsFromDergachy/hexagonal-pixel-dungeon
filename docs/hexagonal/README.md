# Hexagonal Pixel Dungeon - Documentation Index

This directory contains documentation specific to the hexagonal transformation of Shattered Pixel Dungeon.

## 📚 Documentation Overview

### For AI Agents & Developers

| Document | Purpose | Audience |
|----------|---------|----------|
| **[AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md)** | Comprehensive project vision, goals, development guidelines | AI assistants, new contributors |
| **[HEXAGONAL_COORDINATES.md](HEXAGONAL_COORDINATES.md)** | Deep technical dive into coordinate systems | Developers working on hex math |
| **[dt.md](dt.md)** | Quick reference, TODO list, development notes | Day-to-day development |
| **[visual-hierarchy.md](visual-hierarchy.md)** | Visual class hierarchy documentation | Understanding rendering system |

### Quick Links

- **[Root README](../../README.md)** - Project introduction and getting started
- **[WARP.md](../../WARP.md)** - Development commands and architecture
- **[Getting Started Guides](../)** - Platform-specific compilation instructions

## 🎯 Where to Start

### "I want to understand the project vision"
→ Start with **[AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md)**

### "I want to understand the hexagonal coordinate system"
→ Read **[HEXAGONAL_COORDINATES.md](HEXAGONAL_COORDINATES.md)**

### "I want to know what needs to be done"
→ Check **[dt.md](dt.md)** TODO section

### "I want to contribute code"
→ Read **[AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md)** → **[WARP.md](../../WARP.md)** → **[dt.md](dt.md)**

## 🔧 Quick Development Commands

```bash
# Desktop development (fastest iteration)
./gradlew desktop:debug

# Android debug build
./gradlew android:assembleDebug

# Install to connected Android device
./gradlew android:installDebug
```

## 📖 Key Concepts

### Coordinate Systems

The hexagonal implementation uses three coordinate systems:

1. **Offset Coordinates** (odd-q vertical) - Storage format
2. **Cube Coordinates** (q, r, s) - Calculation format  
3. **Pixel Coordinates** - Rendering format

**→ Details:** [HEXAGONAL_COORDINATES.md](HEXAGONAL_COORDINATES.md)

### Tile Dimensions

- Original: 16×16 pixels (square)
- Hexagonal: 18×16 pixels (wider)

### Implementation Strategy

- **Rendering Layer**: Fully converted to hexagonal
- **Level Generation**: Still uses square grid (planned for future)
- **Game Logic**: Mostly unchanged, distance calculations adapted

## 🐛 Known Issues

Critical bugs tracked in [dt.md](dt.md):

- [ ] Level 15 boss crash
- [ ] Search pattern broken
- [ ] Wall-assist not working
- [ ] Cursed wands crash (upstream issue)

## 🗺️ Project Structure

```
Pixel.Dungeon/
├── AI_AGENT_GUIDE.md           ← Start here for AI agents
├── WARP.md                     ← Development commands
├── README.md                   ← Project introduction
├── docs/
│   ├── hexagonal/              ← You are here!
│   │   ├── README.md           ← This file
│   │   ├── HEXAGONAL_COORDINATES.md  ← Technical deep dive
│   │   ├── dt.md               ← Quick reference & TODO
│   │   ├── visual-hierarchy.md ← Rendering classes
│   │   └── example_v.0.0.1.png ← Screenshot
│   ├── getting-started-*.md    ← Platform setup guides
│   └── recommended-changes.md  ← Customization guide
├── SPD-classes/
│   └── src/.../utils/
│       ├── HexMath.java        ← Core hex utilities
│       └── Cube.java           ← Hex room base class
└── core/
    └── src/.../
        ├── levels/Level.java   ← Uses hex distance
        ├── sprites/Sprite.java ← Hex positioning
        └── tiles/DungeonTileMap.java  ← Hex rendering
```

## 🎓 Learning Path

**Beginner** (understanding the project):
1. Read [AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md) - Project Overview section
2. Try running `./gradlew desktop:debug`
3. Browse [dt.md](dt.md) - Current Status section

**Intermediate** (working with hex code):
1. Read [HEXAGONAL_COORDINATES.md](HEXAGONAL_COORDINATES.md) - Coordinate Systems
2. Study `HexMath.java` implementation
3. Review [dt.md](dt.md) - TODO list

**Advanced** (contributing features):
1. Read [HEXAGONAL_COORDINATES.md](HEXAGONAL_COORDINATES.md) - Complete guide
2. Review [AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md) - Development Guidelines
3. Check [WARP.md](../../WARP.md) - Architecture details
4. Pick a task from [dt.md](dt.md)

## 🤝 Contributing

This is a personal learning project, but contributions are welcome!

**Before contributing:**
- Read the development philosophy in [AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md)
- Understand we prefer minimal divergence from upstream
- Check [dt.md](dt.md) for current priorities

**Development principles:**
- ✅ Incremental changes
- ✅ Maintain backward compatibility  
- ✅ Keep git history clean
- ❌ No large refactors
- ❌ Don't break existing game logic

## 📞 Support & Resources

### External Resources
- [Red Blob Games - Hexagons](https://www.redblobgames.com/grids/hexagons/) - Essential hex grid reference
- [Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/) - Upstream project
- [libGDX](https://libgdx.com/) - Game framework

### Support Original Authors
This project builds on their amazing work:
- [Evan Debenham (Shattered PD)](https://www.patreon.com/ShatteredPixel)
- [Watabou (Original PD)](https://pixeldungeon.watabou.ru/#donate)

## 📝 Version Information

- **Base Version**: Shattered Pixel Dungeon v3.1.0 (build 846)
- **Hexagonal Version**: v0.0.1 (experimental)
- **License**: GPLv3

---

**Last Updated:** 2025  
**Maintained By:** Dmitry Tyurnikov (learning project)

*For questions about AI agent collaboration, see: [AI_AGENT_GUIDE.md](../../AI_AGENT_GUIDE.md)*