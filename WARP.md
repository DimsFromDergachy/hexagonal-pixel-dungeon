# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is **Hexagonal Pixel Dungeon**, a variant of Shattered Pixel Dungeon built using libGDX for cross-platform game development. It's a roguelike dungeon crawler game with randomized levels, enemies, and items. The project supports Android, iOS, and Desktop platforms.

## Common Development Commands

### Building and Running

**Desktop Development (Recommended for rapid iteration):**
```bash
# Run in debug mode (fastest for development)
./gradlew desktop:debug

# Build release JAR
./gradlew desktop:release

# Generate platform-specific executable
./gradlew desktop:jpackageImage
```

**Android Development:**
```bash
# Build debug APK
./gradlew android:assembleDebug

# Build release APK (with R8 optimization)
./gradlew android:assembleRelease

# Install debug build to connected device
./gradlew android:installDebug
```

**iOS Development:**
```bash
# Build iOS project
./gradlew ios:build

# Create iOS app bundle
./gradlew ios:createIPA
```

### Testing and Code Quality

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew core:test
./gradlew SPD-classes:test

# Clean build artifacts
./gradlew clean

# Generate Javadoc
./gradlew javadoc
```

### Development-Specific Tasks

```bash
# Copy Android native libraries (automatically runs before Android builds)
./gradlew android:copyAndroidNatives

# Process image assets (if you modify sprites/tiles)
python image_tools/process_tiles.py
```

## Architecture and Code Structure

### Module Organization

The project uses a multi-module Gradle structure:

- **`SPD-classes`** - Low-level engine code (`com.watabou.*`)
  - `noosa` - 2D game engine framework
  - `utils` - Utility classes and data structures
  - `glwrap` - OpenGL wrapper and graphics utilities
  - `input` - Input handling system

- **`core`** - Main game logic (`com.shatteredpixel.shatteredpixeldungeon.*`)
  - `actors/` - Game entities (Hero, Mobs, NPCs, Buffs)
  - `levels/` - Dungeon level generation and management
  - `items/` - All game items (weapons, armor, potions, scrolls, etc.)
  - `scenes/` - Game screens (title, game, menus)
  - `ui/` - User interface components
  - `mechanics/` - Game mechanics and systems

- **`android`** - Android platform-specific code
- **`ios`** - iOS platform-specific code  
- **`desktop`** - Desktop platform-specific code
- **`services`** - Platform services (updates, news)

### Key Architecture Patterns

**Game State Management:**
- `Dungeon.java` - Central game state holder (hero, level, items, progress)
- `Hero.java` - Player character state and actions
- `Level.java` - Current dungeon level state and logic

**Entity System:**
- `Actor.java` - Base class for all game entities that act over time
- `Char.java` - Base for all characters (heroes, mobs, NPCs)
- `Mob.java` - Enemy AI and behavior
- `Buff.java` - Temporary status effects system

**Scene Management:**
- `Game.java` (from SPD-classes) - Core game loop and scene management
- `PixelScene.java` - Base scene class with common UI functionality
- `GameScene.java` - Main gameplay screen

**Level Generation:**
- `RegularLevel.java` - Standard dungeon floor generation
- `Room.java` system - Modular room generation (standard, special, secret)
- `Painter.java` - Low-level terrain painting utilities

### Important Development Notes

**Rendering System:**
- Uses custom 2D engine built on libGDX
- Pixel-perfect rendering with texture atlases
- UI scales automatically across different screen sizes

**Save System:**
- Uses `Bundle` system for serialization
- Game state persists in `Dungeon.java`
- Version compatibility handled through class aliases

**Localization:**
- Messages handled through `Messages.java`
- String resources in `/assets/messages/`

**Asset Management:**
- Graphics in `/core/src/main/assets/`
- Audio, sprites, and other resources loaded through `Assets.java`

**Build Configuration:**
- Debug builds include debug services and have `.indev` package suffix
- Release builds use R8 optimization and include GitHub update services
- Different native library versions for Android compatibility

## Development Workflow

1. **For rapid iteration**: Use `./gradlew desktop:debug` for quick testing
2. **For Android testing**: Use `./gradlew android:installDebug` 
3. **For code changes**: Most game logic is in the `core` module
4. **For UI changes**: Look in `core/src/.../ui/` packages
5. **For new content**: Follow existing patterns in `items/`, `actors/mobs/`, etc.

The desktop build is generally preferred for development as it starts faster and doesn't require an emulator or device.