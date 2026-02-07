# Hexagonal Pixel Dungeon - Development Notes

## Quick Reference

**Hexagonal Grid Resources:**
- https://www.redblobgames.com/grids/hexagons/

**Quick Commands:**
```bash
# Run desktop version (fastest for development)
./gradlew desktop:debug

# Build Android debug APK
./gradlew android:assembleDebug

# Install to connected Android device
./gradlew android:installDebug
```

---

## Project Status

### Current Implementation

**Coordinate System:**
- Using **odd-q vertical layout** (offset coordinates)
- Converting to/from **cube coordinates** for calculations
- Implemented in `HexMath.java` class

**Rendering:**
- Tiles: 18x16 pixels (was 16x16)
- Modified `HexTileMap.java` for hexagonal rendering
- Modified `DungeonTileMap.java` for hex-to-pixel conversions
- Sprite positioning adjusted in `Sprite.java`

**Core Systems:**
- Distance calculations using cube coordinates
- Basic neighbor detection modified
- Level painting still uses square grid underneath

---

## TODO List

### High Priority

#### 🐛 Critical Bugs
- [ ] **Bug: Level 15 boss crash** - Game consistently crashes when fighting the final boss
  - Difficult to reproduce on desktop
  - Happens reliably on Android
  - Need better logging and save file debugging tools
- [ ] **Bug: Fix search pattern** - Search functionality broken with hexagonal grid
  - Likely related to neighbor/adjacency calculations
  - May need to update PathFinder for 6-directional movement
- [ ] **Bug: Fix wall-assist** - Wall-assist feature not working correctly
  - Related to `screenToTile()` method in `DungeonTileMap.java`
  - Hex coordinate conversion issues
- [x] **Bug: Cursed wands crash** - ✅ FIXED
  - Was caused by LightningBolt effect passing neighbor offsets as absolute positions
  - Fixed in CursedWand.java - see `docs/hexagonal/bug-fixes/cursed-wand-lightning-crash.md`
  - This fix should be reported upstream to Shattered Pixel Dungeon

#### 🎨 Core Hexagonal Features
- [ ] **Hex: Complete level repainting** - Adapt level generation for hexagonal patterns
  - Rooms should feel natural on hex grid
  - Corridors need rethinking for 6-directional layout
  - Door placement adjustments
- [ ] **Hex: Pathfinding system** - Update PathFinder for hexagonal grid
  - 6 neighbors instead of 4/8
  - A* algorithm adjustments
  - Line of sight calculations
- [ ] **Hex: Neighbor calculations** - Complete the neighbor system
  - Update `PathFinder.NEIGHBORS` array for 6-directional
  - Fix diagonal-related logic
  - Update FOV (Field of View) calculations

### Medium Priority

#### 🎮 Game Mechanics
- [ ] **Hex: Area of effect spells/abilities** - Adjust circular effects for hex grid
  - Fireball, explosion radiuses
  - Cone/arc effects
  - Radial buffs/debuffs
- [ ] **Hex: Gas/liquid spreading** - Already reduced, but verify balance
  - Gas spreads in 6 directions now (was 4/8)
  - Ensure it feels balanced
  - Test with different gas types
- [ ] **Hex: Projectile trajectories** - Update for hexagonal angles
  - Wands, thrown weapons
  - Bouncing projectiles
  - Line-targeting abilities
- [ ] **Hex: Enemy AI movement** - Verify mob pathfinding works correctly
  - Test with different mob types
  - Fleeing behavior
  - Pursuit and ambush tactics

#### 🖼️ Assets & Visuals
- [ ] **Hex: Update terrain assets** - Create/modify hexagonal tiles
  - Walls (need seamless connections)
  - Chasms (hexagonal edges)
  - Water and grass
  - Doors and barricades
  - Stairs (up and down)
- [ ] **Hex: Update special terrain** - Hexagonal versions of special tiles
  - Traps
  - Plants and vegetation
  - Alchemy pots
  - Pedestals and statues
  - Signs and bookcases
- [ ] **Hex: UI adjustments** - Ensure UI elements align with hex grid
  - Cell highlighting/selection
  - Targeting reticle
  - Range indicators
  - Area effect previews

### Low Priority

#### 📝 Metadata & Branding
- [ ] **Meta: Change game name** - Update to "Hexagonal Pixel Dungeon"
  - Update in `build.gradle`
  - Update app display name
  - Update launcher icons (if needed)
- [ ] **Meta: Update contact email** - Set personal contact info
- [ ] **Meta: Version numbering** - Establish versioning scheme
  - Current base: Shattered PD 3.1.0
  - How to version hexagonal fork?
  - Consider: v0.1.0-hex, v1.0.0-hex, etc.
- [ ] **Meta: Update README** - More details about hexagonal changes
- [ ] **Meta: Credits** - Ensure proper attribution

#### 🔧 Developer Tools
- [ ] **Tool: Save file export** - Add ability to export save from Android
  - Debugging is difficult without this
  - Could add to settings menu
  - Export to cloud or local storage
- [ ] **Tool: Scenario testing** - Quick access to specific game states
  - Jump to level 15 boss
  - Spawn specific items/enemies
  - Test edge cases quickly
- [ ] **Tool: Debug visualization** - Visual aids for hex grid
  - Show hex coordinates overlay
  - Visualize pathfinding
  - Display neighbor relationships
- [ ] **Tool: Performance profiling** - Monitor hex calculations
  - Are coordinate conversions expensive?
  - Rendering performance
  - Memory usage

#### 🎯 Future Enhancements
- [ ] **Feature: Hex-specific level types** - New level layouts that leverage hex grid
  - Honeycomb patterns
  - Spiral layouts
  - Radial boss arenas
- [ ] **Feature: Hex-specific mechanics** - Gameplay that uses 6-directional movement
  - Pushing/pulling in hex directions
  - Hex-based puzzles
  - Directional shields/attacks
- [ ] **Polish: Animation smoothing** - Movement between hex tiles
  - Ensure smooth transitions
  - Character rotation for 6 directions
  - Particle effects alignment

---

## Known Technical Challenges

### Coordinate Conversion Complexity
The project uses multiple coordinate systems:
1. **Offset coordinates** (odd-q vertical) - main storage format
2. **Cube coordinates** (q, r, s where q+r+s=0) - for calculations
3. **Pixel coordinates** - for rendering

**Conversion methods in `HexMath.java`:**
- `HexToPixel(x, y)` - offset to screen pixels
- `PixelToHex(point)` - screen pixels to offset
- `ToCube(x, y)` - offset to cube
- `FromCube(q, r)` - cube to offset

### Square Grid Legacy
Level generation still uses the original square grid:
- Rooms are rectangular
- Corridors are orthogonal
- This creates visual/gameplay disconnect

**Options:**
1. Keep square generation, just render as hexagons (current approach)
2. Convert to true hex-based generation (major refactor)
3. Hybrid: square-based rooms, hex-aware corridors

### PathFinding Integration
`PathFinder.java` needs significant updates:
- Original uses 4 or 8 directions
- Hex needs exactly 6 directions
- FOV and LOS calculations affected
- May impact performance

---

## Testing Checklist

When testing hexagonal changes, verify:

- [ ] Movement in all 6 directions
- [ ] Sprite positioning (hero, mobs, items)
- [ ] Tile rendering (walls, floors, special terrain)
- [ ] Click/tap targeting accuracy
- [ ] Pathfinding to clicked tile
- [ ] Enemy AI movement and pursuit
- [ ] Area effects (explosions, gas, etc.)
- [ ] Line-of-sight and fog of war
- [ ] UI element alignment
- [ ] Performance on Android devices

---

## Architecture Notes

### Key Classes Modified

**SPD-classes module:**
- `HexMath.java` - NEW: Hexagonal coordinate utilities
- `Cube.java` - NEW: Base class for hex-based rooms
- `HexTileMap.java` - NEW: Hexagonal tile rendering
- `PathFinder.java` - TODO: Update for 6-directional movement

**core module:**
- `DungeonTileMap.java` - Modified: Hex rendering, coordinate conversion
- `Sprite.java` - Modified: Hex-aware sprite positioning
- `Level.java` - Modified: Uses HexMath for distance calculations
- `HexRoom.java` - NEW: Hexagonal room base class

### Design Principles

1. **Minimal Divergence**: Keep changes localized to avoid merge conflicts
2. **Layered Approach**: Hex rendering on top of square generation (for now)
3. **Backward Compatible**: Don't break existing save files if possible
4. **Performance Conscious**: Hex calculations should not significantly impact FPS

---

## Upstream Merging Strategy

**Goal:** Regularly merge updates from Shattered Pixel Dungeon

**Process:**
1. Monitor ShatteredPD releases: https://github.com/00-Evan/shattered-pixel-dungeon
2. Create merge branch
3. Resolve conflicts (mostly in modified classes)
4. Test thoroughly (hex rendering might break)
5. Update hexagonal code as needed

**Files likely to conflict:**
- `DungeonTileMap.java`
- `Sprite.java`
- `Level.java`
- `build.gradle` (version numbers)

---

## Version History

**Current Base:** Shattered Pixel Dungeon v3.1.0 (build 846)

**Hexagonal Progress:**
- v0.0.1: Initial hex rendering, basic sprite positioning
- v0.0.2: (planned) Bug fixes, improved neighbor calculations
- v0.1.0: (planned) Complete pathfinding, stable hex movement

---

*Last updated: 2025*
*For comprehensive project vision, see: `/AI_AGENT_GUIDE.md`*