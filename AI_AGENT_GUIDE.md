# AI Agent Guide for Hexagonal Pixel Dungeon

This document provides comprehensive guidance for AI agents working with the Hexagonal Pixel Dungeon project. It describes the project vision, technical approach, current state, and guidelines for effective collaboration.

## Project Overview

**Hexagonal Pixel Dungeon** is a personal learning project that transforms [Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/) from a square grid roguelike to a hexagonal grid roguelike. This is an experimental fork maintained as a hobby project focused on game development learning and exploration.

### Project Background

- **Primary Goal**: Personal challenge and learning experience in game development
- **Developer Background**: .NET backend developer exploring game modification for the first time
- **Project Nature**: Long-term hobby project with ongoing incremental development
- **Upstream**: Based on Shattered Pixel Dungeon by Evan Debenham, which is based on Pixel Dungeon by Watabou

### Core Philosophy

1. **Learning-Focused**: This is a learning project where experimentation and iteration are encouraged
2. **Minimal Divergence**: Keep the codebase relatively close to the original for easier upstream merging
3. **Backward Compatibility**: Maintain compatibility with original Shattered Pixel Dungeon where possible
4. **Visual Transformation**: Primary focus is visual/rendering changes, not major gameplay overhauls
5. **Preservation of Content**: Keep the same items, enemies, and core mechanics

## The Hexagonal Transformation

### Vision

Transform the game from a square grid (4/8-directional movement) to a hexagonal grid (6-directional movement) while preserving the core gameplay experience. The goal is to maintain the feel and balance of the original game with primarily visual changes.

### Technical Approach

#### Coordinate System

- **Implementation**: Using **cube coordinates** and **offset coordinates** (odd-q vertical layout) for hexagonal grid management
- **Reference**: Implementation follows principles from [Red Blob Games' Hexagon Guide](https://www.redblobgames.com/grids/hexagons/)
- **Underlying Grid**: The original square grid system remains intact for level generation
- **Rendering Layer**: Hexagonal transformation happens primarily at the rendering level

**📘 For comprehensive technical details, see:** [`docs/hexagonal/HEXAGONAL_COORDINATES.md`](docs/hexagonal/HEXAGONAL_COORDINATES.md)

#### Tile Dimensions

- **Original**: 16x16 pixels (square tiles)
- **Hexagonal**: 18x16 pixels (wider hexagonal tiles)
- **Rationale**: Hexagons require wider tiles to maintain visual fidelity

**For detailed mathematical explanation, see:** [`docs/hexagonal/HEXAGONAL_COORDINATES.md`](docs/hexagonal/HEXAGONAL_COORDINATES.md)

#### Architecture Decisions

1. **Layered Approach**: Keep original level generation logic (square grid), transform at render time
2. **Minimal Core Changes**: Avoid deep modifications to core game logic where possible
3. **Sprite Positioning**: Modified to accommodate hexagonal layout
4. **Neighbor Calculations**: Adjusted for 6-directional hexagonal adjacency

### Current Implementation Status

#### ✅ Completed

- [x] Hexagonal tile rendering (18x16 pixels)
- [x] Sprite positioning for mobs and hero on hex grid
- [x] Initial neighbor calculation adjustments
- [x] Basic hexagonal visual layout

#### 🚧 In Progress

- [ ] Level repainting for hexagonal patterns
- [ ] Asset updates for hexagonal tiles (walls, chasms, stairs, etc.)
- [ ] Complete neighbor/pathfinding system for hex grid

#### 🐛 Known Bugs

1. **Search Pattern Bug**: Search functionality not working correctly with hex grid
2. **Wall-Assist Bug**: Wall-assist feature broken in hexagonal context
3. **Cursed Wands Crash**: Cursed wands cause game crash (inherited from original game)
4. **Level 15 Boss Crash**: Fighting the boss on level 15 consistently crashes the game

#### 📋 Planned

- Game metadata updates (name, email, version)
- Full hexagonal asset set
- Level generation adaptation for hex patterns
- Mechanical adjustments for hex grid (e.g., gas expansion has more directions)

## Development Guidelines for AI Agents

### ✅ Do These Things

1. **Help with Debugging**: Assist in investigating crashes and bugs, especially:
   - The level 15 boss crash
   - Search pattern issues
   - Wall-assist problems
   - Reproduction steps for difficult-to-replicate bugs

2. **Maintain Git History**: Breaking changes are acceptable as long as they're well-documented in git history

3. **Stay Informed**: Provide high-level explanations of game systems and architecture

4. **Suggest Incremental Changes**: Propose small, testable modifications rather than large rewrites

5. **Respect Learning Process**: Explain concepts and reasoning, don't just provide solutions

6. **Consider Android Development**: Remember that primary testing happens on Android devices

7. **Upstream Awareness**: Keep in mind that merging new content from Shattered Pixel Dungeon is a goal

### ❌ Avoid These Things

1. **Don't Over-Refactor**: Resist the urge to "clean up" code extensively - minimal divergence from original is important

2. **Don't Break Core Logic**: The original game logic that works should remain intact

3. **Don't Assume Context**: Always check the current state of files before making suggestions

4. **Don't Change Balance**: Game balance and mechanics should remain consistent with original

5. **Don't Remove Backward Compatibility**: Maintaining compatibility with upstream is crucial

6. **Don't Suggest Complete Rewrites**: Incremental changes are preferred over architectural overhauls

## Project Structure

### Key Modules

```
Pixel.Dungeon/
├── SPD-classes/          # Low-level engine (com.watabou.*)
│   ├── noosa/            # 2D game engine framework
│   ├── utils/            # Utility classes
│   ├── glwrap/           # OpenGL wrapper
│   └── input/            # Input handling
├── core/                 # Main game logic (com.shatteredpixel.*)
│   ├── actors/           # Game entities (Hero, Mobs, NPCs, Buffs)
│   ├── levels/           # Dungeon generation and management
│   ├── items/            # All game items
│   ├── scenes/           # Game screens
│   ├── ui/               # User interface components
│   └── mechanics/        # Game mechanics and systems
├── android/              # Android platform code
├── desktop/              # Desktop platform code
├── ios/                  # iOS platform code
└── docs/hexagonal/       # Hexagonal-specific documentation
```

### Hexagonal-Specific Areas

Most hexagonal changes should be concentrated in:
- Rendering layer (`SPD-classes/noosa/`)
- Visual components (`core/.../ui/`)
- Sprite positioning (`core/.../sprites/`)
- Level painting/rendering (`core/.../levels/painters/`)

### Areas to Be Cautious With

- **Core game logic** in `Dungeon.java`, `Hero.java`, `Actor.java`
- **Item mechanics** - should remain unchanged
- **Balance calculations** - damage, stats, progression
- **Save system** - critical for backward compatibility

## Common Development Tasks

### Quick Start

```bash
# Desktop development (fastest iteration)
./gradlew desktop:debug

# Android debug build
./gradlew android:assembleDebug

# Install to Android device
./gradlew android:installDebug
```

### Debugging Workflow

1. **Desktop First**: Use `./gradlew desktop:debug` for rapid testing
2. **Android Testing**: Use `./gradlew android:installDebug` for device testing
3. **Save File Location**: 
   - Desktop: Look in user's home directory
   - Android: Difficult to extract - consider implementing save export feature

### Testing Hexagonal Changes

When testing hexagonal modifications:
1. Check rendering in different dungeon layouts
2. Test pathfinding and movement
3. Verify sprite positioning with multiple mob types
4. Test edge cases (corners, walls, chasms)
5. Verify UI elements align correctly with hex grid

## Known Challenges

### Bug Reproduction

**Challenge**: Some bugs are difficult to reproduce consistently, especially:
- The level 15 boss crash
- Specific combat scenarios
- Android-specific issues

**Strategies**:
- Add extensive logging for problematic areas
- Create test scenarios that force specific conditions
- Consider adding debug modes for quick scenario access

### Save File Management

**Challenge**: Save files on Android devices are not easily transferable to desktop for debugging

**Potential Solutions**:
- Implement save file export feature
- Add cloud save functionality
- Create debugging tools that work on-device

### Upstream Merging

**Challenge**: Keeping up with Shattered Pixel Dungeon updates while maintaining hexagonal changes

**Strategies**:
- Keep changes concentrated in specific areas
- Document all hexagonal-specific modifications
- Regularly merge from upstream
- Maintain feature branches for experimental work

## Game Mechanics Adjustments

### Hexagonal-Specific Changes

1. **Gas Expansion**: Adjusted to account for 6 directions instead of 4/8
2. **Movement**: Transitioning from 8-directional to 6-directional
3. **Line of Sight**: May need adjustment for hexagonal geometry
4. **Area Effects**: Circular/radial effects need hex grid consideration

### Unchanged Systems

- Item mechanics and effects
- Character stats and progression
- Enemy AI behavior (except pathfinding)
- Combat calculations
- Buff/debuff systems

## Helpful Resources

### External Documentation

- **Hexagonal Grids**: https://www.redblobgames.com/grids/hexagons/
- **Original Shattered PD**: https://shatteredpixel.com/shatteredpd/
- **libGDX Documentation**: https://libgdx.com/wiki/

### Internal Documentation

- `WARP.md` - Development commands and architecture overview
- `docs/getting-started-*.md` - Platform-specific setup guides
- `docs/hexagonal/` - Hexagonal-specific notes and examples
- `docs/hexagonal/dt.md` - Quick reference and TODO list
- `docs/hexagonal/HEXAGONAL_COORDINATES.md` - **Detailed technical documentation of coordinate systems**
- `docs/hexagonal/visual-hierarchy.md` - Visual class hierarchy

## Version Information

- **Base Version**: Shattered Pixel Dungeon 3.1.0 (build 846)
- **LibGDX Version**: 1.12.1
- **Target Platforms**: Android (API 14-34), Desktop, iOS
- **License**: GPLv3 (inherited from Shattered Pixel Dungeon)

## Support Original Authors

This project is built on the shoulders of giants. Please support the original creators:

- **Evan Debenham** (Shattered Pixel Dungeon): https://www.patreon.com/ShatteredPixel
- **Watabou** (Original Pixel Dungeon): https://pixeldungeon.watabou.ru/#donate

## Contributing Philosophy

This is a personal learning project, but contributions and suggestions are welcome if they:
1. Align with the hexagonal transformation vision
2. Maintain code quality and readability
3. Preserve compatibility with upstream where possible
4. Include clear explanations for learning purposes
5. Respect the incremental, experimental nature of the project

## AI Agent Quick Reference

**When I ask for help with:**
- **Crashes/Bugs**: Focus on investigation, logging, and root cause analysis
- **Hexagonal Logic**: Suggest approaches that maintain original game feel
- **Code Understanding**: Provide high-level architectural explanations
- **New Features**: Propose minimal changes that integrate smoothly
- **Refactoring**: Only suggest if it significantly aids hexagonal transformation
- **Upstream Merging**: Help identify conflicts and integration strategies

**Remember:**
- This is a learning project - explain, don't just solve
- Backward compatibility matters
- Desktop and Android are primary platforms
- Visual changes first, gameplay changes second
- Git history should tell a clear story

---

*This guide is a living document. As the project evolves, this file should be updated to reflect new insights, challenges, and approaches.*